/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on JXTA samples
 *
 */
package net.chaupal.practical.jxta.examples.discovery;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;

import java.io.File;
import java.util.Enumeration;

import org.junit.Test;

/**
 * Illustrates the use of the Discovery Service
 */
public class DiscoveryClient implements DiscoveryListener {

    private transient NetworkManager manager;
    private transient DiscoveryService discovery;

    /**
     * Constructor for the DiscoveryClient
     */
    public DiscoveryClient() {
        try {
            manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "DiscoveryClient",
                    new File(new File(".cache"), "DiscoveryClient").toURI());
            manager.startNetwork();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Get the NetPeerGroup
        PeerGroup netPeerGroup = manager.getNetPeerGroup();

        // get the discovery service
        discovery = netPeerGroup.getDiscoveryService();
    }

    @Test
    public void test() {
    	main(null);
    }

    /**
     * main
     *
     * @param args command line args
     */
    public static void main(String args[]) {
        DiscoveryClient disocveryClient = new DiscoveryClient();

        disocveryClient.start();
    }

    /**
     * loop forever attempting to discover advertisements every minute
     */
    public void start() {
        long waittime = 10 * 1000L;

        try {
            // Add ourselves as a DiscoveryListener for DiscoveryResponse events
            discovery.addDiscoveryListener(this);
            discovery.getRemoteAdvertisements(// no specific peer (propagate)
                    null, // Adv type
                    DiscoveryService.ADV, // Attribute = any
                    null, // Value = any
                    null, // one advertisement response is all we are looking for
                    1, // no query specific listener. we are using a global listener
                    null);
            while (true) {
                // wait a bit before sending a discovery message
                try {
                    System.out.println("Sleeping for :" + waittime);
                    Thread.sleep(waittime);
                } catch (Exception e) {
                    // ignored
                }
                System.out.println("Sending a Discovery Message");
                // look for any peer
//                discovery.getRemoteAdvertisements(
//                        // no specific peer (propagate)
//                        null,
//                        // Adv type
//                        DiscoveryService.ADV,
//                        // Attribute = name
//                        "Name",
//                        // Value = the tutorial
//                        "Discovery tutorial",
//                        // one advertisement response is all we are looking for
//                        1,
//                        // no query specific listener. we are using a global listener
//                        null);

                discovery.getRemoteAdvertisements(// no specific peer (propagate)
                        null, // Adv type
                        DiscoveryService.ADV, // Attribute = any
                        null, // Value = any
                        null, // one advertisement response is all we are looking for
                        1, // no query specific listener. we are using a global listener
                        null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called whenever a discovery response is received, which are
     * either in response to a query we sent, or a remote publish by another node
     *
     * @param ev the discovery event
     */
    public void discoveryEvent(DiscoveryEvent ev) {

        DiscoveryResponseMsg res = ev.getResponse();

        // let's get the responding peer's advertisement
        System.out.println(" [  Got a Discovery Response [" + res.getResponseCount() + " elements]  from peer : " + ev.getSource() + "  ]");

        Advertisement adv;
        Enumeration<Advertisement> en = res.getAdvertisements();

        if (en != null) {
            while (en.hasMoreElements()) {
                adv = (Advertisement) en.nextElement();
                System.out.println(adv);
            }
        }
    }

    /**
     * Stops the platform
     */
    public void stop() {
        // Stop JXTA
        manager.stopNetwork();
    }
}
