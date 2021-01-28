/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.discovery;

import net.chaupal.practical.jxta.examples.RendezVousJack;
import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;

import org.junit.Test;

public class EdgeMaximeTheSocializer implements DiscoveryListener {

    public static final String Name = "Edge_Maxime_The_Socializer";
    public static final int TcpPort = 9711;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    public void discoveryEvent(DiscoveryEvent e) {

        // Who triggered the event?
        DiscoveryResponseMsg msg = e.getResponse();

        if (msg != null) {
            Enumeration<Advertisement> TheEnumeration = msg.getAdvertisements();

            while (TheEnumeration.hasMoreElements()) {

                try {
                    PeerAdvertisement ThePeer = (PeerAdvertisement) TheEnumeration.nextElement();
                    Tools.PopInformationMessage(Name, "Received advertisement of: " + ThePeer.getName());
                } catch (ClassCastException Ex) {
                    // We are not dealing with a Peer Advertisement
                }
            }
        }

    }

    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {

        try {
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Checking if RendezVous_Jack should be a seed
            myNetworkConfigurator.clearRendezvousSeeds();
            String theSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousJack.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, theSeed, myNetworkConfigurator);

            // Setting Configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            myNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous\nconnection with "
                    + RendezVousJack.Name + " for maximum 1 minutes");
            PeerGroup NetPeerGroup = myNetworkManager.startNetwork();

            // Disabling any rendezvous autostart
            if (myNetworkManager.waitForRendezvousConnection(60000)) {
                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(), Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }

            // Launching query to retrieve peer advertisements
            Tools.PopInformationMessage(Name, "Start peer discovery and going to sleep for 60 seconds");

            DiscoveryService theDiscoveryService = NetPeerGroup.getDiscoveryService();
            theDiscoveryService.getRemoteAdvertisements(null, DiscoveryService.PEER,
                    null, null, 0, new EdgeMaximeTheSocializer());

            //DiscoveryListener myListener = new EdgeMaximeTheSocializer();
            //theDiscoveryService.addDiscoveryListener(myListener);

            //int myQueryID = theDiscoveryService.getRemoteAdvertisements(
            //                    null, DiscoveryService.PEER, null, null , 0);

            // Sleeping for 60 seconds
            Tools.GoToSleep(60000);

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();

        } catch (IOException Ex) {
            // Raised when access to local file and directories caused an error
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (PeerGroupException Ex) {
            // Raised when the net peer group could not be created
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}