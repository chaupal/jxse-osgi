/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

import org.junit.Test;

public class EdgeChihiro {

    public static final String Name = "Edge Chihiro";
    public static final int TcpPort = 9715;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

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

            // Adding Jack and Aminah as RendezVous seeds
            myNetworkConfigurator.clearRendezvousSeeds();

            String TheJackSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousJack.TcpPort;
            URI LocalSeedingRendezVousURI = URI.create(TheJackSeed);
            myNetworkConfigurator.addSeedRendezvous(LocalSeedingRendezVousURI);

            String TheAminahSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousAminah.TcpPort;
            URI LocalSeedingRendezVousURI2 = URI.create(TheAminahSeed);
            myNetworkConfigurator.addSeedRendezvous(LocalSeedingRendezVousURI2);

            // Setting Configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //MyNetworkConfigurator.setPeerID(PID);
            //MyNetworkManager.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and wait for a\n"
                    + "rendezvous connection for maximum 2 minutes");
            PeerGroup NetPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());

            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);

            if (myNetworkManager.waitForRendezvousConnection(120000)) {

                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(), Name);

            } else {

                Tools.PopInformationMessage(Name, "Did not connected to a rendezvous");

            }

            // Waiting for Jack to shutdown
            Tools.PopInformationMessage(Name, "Waiting for rendezvous to shut down");

            // Challenging existence of rendezvous
            Tools.PopInformationMessage(Name, "Waiting for cache to refresh for 30 seconds");

            // Going to sleep for some time
            Tools.GoToSleep(30000);

            // Waiting for rendezvous connection again
            Tools.PopInformationMessage(Name, "Waiting for rendezvous connection again");

            if (myNetworkManager.waitForRendezvousConnection(120000)) {

                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(), Name);

            } else {

                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");

            }

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
            Tools.PopErrorMessage(Name, e.toString());
		}

    }

}