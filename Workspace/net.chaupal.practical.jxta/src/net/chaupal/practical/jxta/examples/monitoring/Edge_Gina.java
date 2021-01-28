/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * Based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.monitoring;
import net.chaupal.practical.jxta.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.IModuleDefinitions;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

/**
 * Simple EDGE peer connecting via the NetPeerGroup.
 */
public class Edge_Gina {

    // Static

    public static final String Name_EDGE = "EDGE";
    public static final PeerID PID_EDGE = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name_EDGE.getBytes());
    public static final int TcpPort_EDGE = 9710;
    public static final int HttpPort_EDGE = 9900;
    public static final File ConfigurationFile_EDGE = new File("." + System.getProperty("file.separator") + Name_EDGE);

    public static final String ChildPeerGroupName = "Child peer group";
    public static final PeerGroupID ChildPeerGroupID = IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, ChildPeerGroupName.getBytes());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            // Removing any existing configuration?
            NetworkManager.RecursiveDelete(ConfigurationFile_EDGE);

            System.out.println(IModuleDefinitions.tcpProtoClassID);

            // Creation of the network manager
            final NetworkManager myNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name_EDGE, ConfigurationFile_EDGE.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Setting Configuration
            myNetworkConfigurator.setUseMulticast(false);

            myNetworkConfigurator.setTcpPort(TcpPort_EDGE);

            if ( Tools.PopYesNoQuestion(Name_EDGE, "Do you want to enable TCP?") == JOptionPane.YES_OPTION ) {

                myNetworkConfigurator.setTcpEnabled(true);
                myNetworkConfigurator.setTcpIncoming(true);
                myNetworkConfigurator.setTcpOutgoing(true);

            } else {
                myNetworkConfigurator.setTcpEnabled(false);
                myNetworkConfigurator.setTcpIncoming(false);
                myNetworkConfigurator.setTcpOutgoing(false);
            }

            myNetworkConfigurator.setHttpPort(TcpPort_EDGE);

            if ( Tools.PopYesNoQuestion(Name_EDGE, "Do you want to enable HTTP?") == JOptionPane.YES_OPTION ) {
                myNetworkConfigurator.setHttpEnabled(true);
                myNetworkConfigurator.setHttpIncoming(false);
                myNetworkConfigurator.setHttpOutgoing(true);
            } else {
                myNetworkConfigurator.setHttpEnabled(false);
                myNetworkConfigurator.setHttpIncoming(false);
                myNetworkConfigurator.setHttpOutgoing(false);
            }

            // Adding RDV seed
            myNetworkConfigurator.clearRendezvousSeeds();

            String rdvSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":"
                    + RendezVous_Mya.TcpPort_RDV;
            URI rendezVousSeedURI = URI.create(rdvSeed);
            myNetworkConfigurator.addSeedRendezvous(rendezVousSeedURI);

            // Adding Relay seed
            myNetworkConfigurator.clearRelaySeeds();

            String httpRelaySeed = "http://" + InetAddress.getLocalHost().getHostAddress() + ":"
                    + Relay_Robert.HttpPort_RELAY;
            URI httpRelaySeedURI = URI.create(httpRelaySeed);
            myNetworkConfigurator.addSeedRelay(httpRelaySeedURI);

            String tcpRelaySeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":"
                    + Relay_Robert.TcpPort_RELAY;
            URI tcpRelaySeedURI = URI.create(tcpRelaySeed);
            myNetworkConfigurator.addSeedRelay(tcpRelaySeedURI);

            // Starting the JXTA network
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            // Setting the Peer ID
            myNetworkConfigurator.setPeerID(PID_EDGE);

            // Starting the connectivity monitor
            new ConnectivityMonitor(netPeerGroup);

            // Disabling any rendezvous autostart
            netPeerGroup.getRendezVousService().setAutoStart(false);

            // Stopping the network asynchronously
            ConnectivityMonitor.TheExecutor.schedule(
                new DelayedJxtaNetworkStopper(
                    myNetworkManager,
                    "Click to stop " + Name_EDGE,
                    "Stop"),
                0,
                TimeUnit.SECONDS);

        } catch (IOException Ex) {

            System.err.println(Ex.toString());

        } catch (PeerGroupException Ex) {

            System.err.println(Ex.toString());

        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}
