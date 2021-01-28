package net.chaupal.practical.jxta.examples.messenger;

import net.jxta.endpoint.EndpointListener;
import net.jxta.endpoint.EndpointService;

import java.io.File;
import net.jxta.document.MimeMediaType;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.EndpointAddress;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.TextDocumentMessageElement;
import net.jxta.endpoint.router.RouteController;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.RouteAdvertisement;

/**
 * Simple example to illustrate the use of direct messengers.
 */
public class Receiver {

    private static class ChatUnicastReceiver implements EndpointListener {
        public void processIncomingMessage(Message msg, EndpointAddress source, EndpointAddress destination) {
            MessageElement chat = msg.getMessageElement("Chat");

            if(null != chat) {
                System.out.println(chat.toString());
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
            NetworkManager manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "DirectMessengerReceiver",
                    new File(new File(".cache"), "DirectMessengerReceiver").toURI());

            manager.startNetwork();

            PeerGroup npg = manager.getNetPeerGroup();

            EndpointService endpoint = npg.getEndpointService();

            EndpointListener chatlistener = new ChatUnicastReceiver();

            endpoint.addIncomingMessageListener(chatlistener, "chatService", npg.getPeerID().getUniqueValue().toString() );

            synchronized(shutDown) {
                try {
                    while(!stopped) {
                        shutDown.wait(5000);

                        System.out.println("Proudly announcing the existance of " + npg.getPeerID());

                        RouteController routeControl = endpoint.getEndpointRouter().getRouteController();

                        RouteAdvertisement myRoute = routeControl.getLocalPeerRoute();

                        Message announce = new Message();

                        XMLDocument<?> routeDoc = (XMLDocument<?>) myRoute.getDocument(MimeMediaType.XMLUTF8);

                        announce.addMessageElement(new TextDocumentMessageElement("ChatAnnounce", routeDoc, null));

                        endpoint.propagate(announce, "chatAnnounce", null);
                    }
                } catch( InterruptedException woken ) {
                    Thread.interrupted();
                }
            }

            endpoint.removeIncomingMessageListener( "chatService", npg.getPeerID().getUniqueValue().toString() );

            manager.stopNetwork();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
