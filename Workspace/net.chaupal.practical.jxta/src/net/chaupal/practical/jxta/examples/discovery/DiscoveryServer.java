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
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.junit.Test;

/**
 * Illustrates the use of Discovery Service
 * Note this is for illustration purposes and is not meant as a blue-print
 */
public class DiscoveryServer implements DiscoveryListener {

    private transient NetworkManager manager;
    private transient DiscoveryService discovery;

    /**
     * Constructor for the DiscoveryServer
     */
    public DiscoveryServer() {
        try {
            manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "DiscoveryServer",
                    new File(new File(".cache"), "DiscoveryServer").toURI());
            manager.startNetwork();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {

            NetworkConfigurator networkConfigurator = null;
            networkConfigurator = manager.getConfigurator();

            networkConfigurator.setUseMulticast(true);

            networkConfigurator.setUseMulticast(true);
            PeerGroup netPeerGroup = manager.getNetPeerGroup();

            // get the discovery service
            discovery = netPeerGroup.getDiscoveryService();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        DiscoveryServer disocveryServer = new DiscoveryServer();
        disocveryServer.start();
    }

    /**
     * create a new pipe adv, publish it for 2 minut network time,
     * sleep for 3 minutes, then repeat
     */
    public void start() {
        long lifetime = 60 * 2 * 1000L;
        long expiration = 60 * 2 * 1000L;
        long waittime = 20 * 1000L;

        try {
            while (true) {
                PipeAdvertisement pipeAdv = getPipeAdvertisement();

                // publish the advertisement with a lifetime of 2 mintutes
                System.out.println(
                        "Publishing the following advertisement with lifetime :" + lifetime + " expiration :" + expiration);
                System.out.println(pipeAdv.toString());
                discovery.publish(pipeAdv, lifetime, expiration);
                discovery.remotePublish(pipeAdv, expiration);
                try {
                    System.out.println("Sleeping for :" + waittime);
                    Thread.sleep(waittime);
                } catch (Exception e) {// ignored
                }
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
     * Creates a pipe advertisement
     *
     * @return a Pipe Advertisement
     */
    public static PipeAdvertisement getPipeAdvertisement() {
        PipeAdvertisement advertisement = (PipeAdvertisement)
                AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());

        advertisement.setPipeID(IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID));
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName("Discovery tutorial");
        return advertisement;
    }

    /**
     * Stops the platform
     */
    public void stop() {
        // Stop JXTA
        manager.stopNetwork();
    }
}

