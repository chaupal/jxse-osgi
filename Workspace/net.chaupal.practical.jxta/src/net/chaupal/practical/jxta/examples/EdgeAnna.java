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

import org.junit.Test;

public class EdgeAnna {

    public static final String Name = "Edge_Anna";
    public static final int TcpPort = 9712;
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

            // Checking if RendezVousJack should be a seed
            myNetworkConfigurator.clearRendezvousSeeds();
            String theSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousJack.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, theSeed, myNetworkConfigurator);

            // Setting Configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);
            //myNetworkConfigurator.setHttp2Enabled(false);

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);
            //myNetworkManager.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous\nconnection with "
                    + RendezVousJack.Name + " for maximum 2 minutes");
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            // Disabling any rendezvous autostart
            netPeerGroup.getRendezVousService().setAutoStart(false);

            if (myNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(netPeerGroup.getRendezVousService(), Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();

        } catch (IOException ioe) {
            // Raised when access to local file and directories caused an error
            ioe.printStackTrace();
        } catch (PeerGroupException pge) {
            // Raised when the net peer group could not be created
            pge.printStackTrace();
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}