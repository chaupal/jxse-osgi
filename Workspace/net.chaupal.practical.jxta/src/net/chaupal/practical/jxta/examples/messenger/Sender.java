package net.chaupal.practical.jxta.examples.messenger;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.*;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.RouteAdvertisement;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple example to illustrate the use of direct messengers. A direct messenger
 * is acquired directly from the a physical message transport and bypasses the
 * normal JXTA network virtualization layers. There are several reasons why an
 * an application might want to use direct messengers :
 * <dl>
 *   <dt>Dedicated Resource</dt>
 *   <dd>Each direct messenger is a dedicated resource and is available only to
 *   the application or service which creates it. The messenger is not shared
 *   with other uses.</dd>
 *   <dt>Latency & Throughput</dt>
 *   <dd>Because the messenger bypasses the JXTA network virtualization layers
 *   it may offer somewhat better throughput and latency than regular shared
 *   JXTA virtual messengers.</dd>
 *   <dt>Presence & Disconnection</dt>
 *   <dd>Direct messengers are more immediately responsive to connection status
 *   changes. If the remote side closes the connection the application or
 *   service can recognize the state change immediately.</dd>
 * </dl>
 * Using direct messengers is not
 * <dl>
 *   <dt>Not Always Available</dt>
 * <dd>Creation of a direct messenger to another peer requires that the remote
 *   peer be directly reachable. In many cases this will not be possible. This
 *   usually means that it is necessary to support both direct messengers and
 *   indirect messengers in your application. Additionally, there may be extra
 *   overhead introduced into your application as a result of attempting to
 *   create direct messengers.</dd>
 *   <dt>Consume Extra Resources</dt>
 *   <dd>The resources used by direct messengers are not available to other JXTA
 *   applications and services (nor the rest of the system).</dd>
 *   <dt>Require More "Maintenance"</dt>
 *   <dd>If the remote peer's addresses change or the connection is closed your
 *   application will need to re-open the direct messenger. Normally the JXTA
 *   network virtualization layer hides all this work from your application.
 *   HTTP direct messengers (because of proxies) commonly require frequent
 *   re-creation.</dd>
 * </dl>
 */
public class Sender {
    /**
     *  The number of responses after which we will stop.
     */
    static final int TOTAL_RESPONSES = 10;

    /**
     *  Our listener for "chatAnnounce" messages.
     */
    private static class ChatAnnounceReceiver implements EndpointListener {

        /**
         *  The endpoint with which this listener is registered.
         */
        private final EndpointService endpoint;

        /**
         * The number of responses we have sent.
         */
        private final AtomicInteger responses = new AtomicInteger(0);

        public ChatAnnounceReceiver(EndpointService endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * {@inheritDoc}
         * <p/>
         * Receive a message sent to "chatAnnounce". We expect that the message
         * will contain a "ChatAnnounce" message element containing a JXTA
         * Route Advertisement for the peer which wishes us to send a response.
         */
        public void processIncomingMessage(Message msg, EndpointAddress source, EndpointAddress destination) {
            MessageElement announce = msg.getMessageElement("ChatAnnounce");

            if(null == announce) {
                // It doesn't seem to be the right kind of message.
                return;
            }

            RouteAdvertisement route;

            try {
                XMLDocument<?> routeDoc = (XMLDocument<?>) StructuredDocumentFactory.newStructuredDocument(announce);

                route = (RouteAdvertisement) AdvertisementFactory.newAdvertisement(routeDoc);
            } catch(Exception bad) {
                System.err.println("### - Bad Route");
                bad.printStackTrace(System.err);
                return;
            }

            System.out.println("Announcement from " + route.getDestPeerID() );

            // We have received an announcement from some peer. We will attempt
            // to create a direct messenger to respond to the announcement.
            // Currently we look for only TCP messengers.
            for(EndpointAddress anEA : route.getDestEndpointAddresses()) {
                MessageSender tcp = (MessageSender) endpoint.getMessageTransport("tcp");

                // Search for "tcp" endpoint addresses.
                if("tcp".equals(anEA.getProtocolName())) {
                    EndpointAddress destAddress;

                    if(endpoint.getGroup().equals(tcp.getEndpointService().getGroup())) {
                        // The TCP message transport is in our peer group. We can address the message directly.
                        destAddress = new EndpointAddress(anEA, "chatService", route.getDestPeerID().getUniqueValue().toString() );
                    } else {
                        // When a message transport receives a message it passes it to the endpoint service in it's own
                        // peer group for processing. If our peer group is not the same as the message transports then
                        // the endpoint service which will receive the message is not the same message service with
                        // which we registered our message listener. There is a solution though : Each endpoint service
                        // which uses message transports from it's parent peer groups will also register a redirection
                        // listener for messages destined for that endpoint service. The listener is named
                        // "EndpointService:"<PeerGroupID-unique value>" with the parameter being a concatination of the
                        // service name and parameter separated by a "/".
                        destAddress = new EndpointAddress(anEA, "EndpointService:jxta-NetGroup", "chatService" + "/" + route.getDestPeerID().getUniqueValue().toString() );
                    }

                    // We have an address be believe is worth trying. We will
                    // attempt to create a messenger to the address.
                    Messenger directMessenger = null;
                    try {
                        directMessenger = tcp.getMessenger(destAddress);

                        if(null == directMessenger) {
                            // The current address was unreachable. Try another.
                            System.err.println("### - getMessenger() failed for " + anEA );
                            continue;
                        }

                        // We have a direct messenger. Try to send a response.
                        System.out.println("Sending response to " + anEA );

                        String chatMessage = "Hello from " + endpoint.getGroup().getPeerID() + " via " + anEA + " @ " + new Date();

                        Message chat = new Message();

                        chat.addMessageElement(new StringMessageElement("Chat", chatMessage, null));

                        directMessenger.sendMessageB(chat, null, null);

                        // The message has been sent.

                        // Decide if we have sent enough responses.
                        int totalSent = responses.incrementAndGet();
                        if(totalSent >= TOTAL_RESPONSES) {
                            synchronized(shutDown) {
                                // We have sent all the responses we wanted to. Signal shutdown.
                                stopped = true;
                                shutDown.notifyAll();
                            }
                        }

                        break;
                    } catch(IOException failed) {
                        failed.printStackTrace(System.err);
                    } finally {
                        if(null != directMessenger) {
                            directMessenger.close();
                        }
                    }
                }
            }
        }
    }

    private static boolean stopped = false;

    private static String shutDown = "shutdown";

    /**
     * main
     *
     * @param args command line args
     */
    public static void main(String args[]) {
        try {
            // Configure and start JXTA.
            NetworkManager manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "DirectMessengerSender",
                    new File(new File(".cache"), "DirectMessengerSender").toURI());

            manager.startNetwork();

            PeerGroup npg = manager.getNetPeerGroup();

            // Register an endpoint listener for "chatAnnounce" messages.
            EndpointService endpoint = npg.getEndpointService();

            EndpointListener chatAnnouncelistener = new ChatAnnounceReceiver(endpoint);

            endpoint.addIncomingMessageListener(chatAnnouncelistener, "chatAnnounce", null );

            // Continue until shutdown
            synchronized(shutDown) {
                try {
                    while(!stopped) {
                        shutDown.wait(5000);
                    }
                } catch( InterruptedException woken ) {
                    Thread.interrupted();
                }
            }

            // De-register "chatAnnounce" listener.
            endpoint.removeIncomingMessageListener( "chatAnnounce", null );

            // Stop JXTA.
            manager.stopNetwork();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
