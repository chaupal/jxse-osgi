/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * Based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.monitoring;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

/**
 * Simple RELAY peer.
 */
public class Relay_Robert {

    // Static
    public static final String Name_RELAY = "RELAY";
    public static final PeerID PID_RELAY = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name_RELAY.getBytes());
    public static final int HttpPort_RELAY = 9900;
    public static final int TcpPort_RELAY = 9715;
    public static final File ConfigurationFile_RELAY = new File("." + System.getProperty("file.separator") + Name_RELAY);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            // Removing any existing configuration?
            NetworkManager.RecursiveDelete(ConfigurationFile_RELAY);

            // Creation of the network manager
            final NetworkManager myNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.RELAY,
                    Name_RELAY, ConfigurationFile_RELAY.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Setting Configuration
            myNetworkConfigurator.setUseMulticast(false);

            myNetworkConfigurator.setTcpPort(TcpPort_RELAY);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);

            myNetworkConfigurator.setHttpPort(HttpPort_RELAY);
            myNetworkConfigurator.setHttpEnabled(true);
            myNetworkConfigurator.setHttpIncoming(true);
            myNetworkConfigurator.setHttpOutgoing(true);

            // Starting the JXTA network
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            // Setting the Peer ID
            myNetworkConfigurator.setPeerID(PID_RELAY);

            // Starting the connectivity monitor
            new ConnectivityMonitor(netPeerGroup);

            // Stopping the network asynchronously
            ConnectivityMonitor.TheExecutor.schedule(
                new DelayedJxtaNetworkStopper(
                    myNetworkManager,
                    "Click to stop " + Name_RELAY,
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
