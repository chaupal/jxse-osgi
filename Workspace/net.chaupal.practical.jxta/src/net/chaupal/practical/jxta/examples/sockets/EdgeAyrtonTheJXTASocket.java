/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.sockets;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.impl.socket.JxtaSocket;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

import java.io.*;
import java.net.InetAddress;

import org.junit.Test;

public class EdgeAyrtonTheJXTASocket {

    public static final String Name = "Edge Ayrton, the JXTA socket";
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

            // Creation of network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Checking if RendezVousLidongTheJXTASocketServer should be a seed
            myNetworkConfigurator.clearRendezvousSeeds();
            String theSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousLidongTheJXTASocketServer.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, theSeed, myNetworkConfigurator);

            // Setting Configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous connection with\n"
                    + RendezVousLidongTheJXTASocketServer.Name + " for maximum 2 minutes");
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());

            // Disabling any rendezvous autostart
            netPeerGroup.getRendezVousService().setAutoStart(false);

            if (myNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(netPeerGroup.getRendezVousService(), Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }

            // Creating the JXTA socket
            JxtaSocket mySocket = new JxtaSocket(netPeerGroup, null,
                    RendezVousLidongTheJXTASocketServer.getPipeAdvertisement(),
                    30000, true);

            if (mySocket != null) {

                // Retrieving the output stream
                OutputStream myOutputStream = mySocket.getOutputStream();
                DataOutput myDataOutput = new DataOutputStream(myOutputStream);

                // Sending a message
                myDataOutput.writeUTF("Hello from " + Name);
                myOutputStream.flush();

                // Sleeping for 10 seconds
                Tools.GoToSleep(10000);

                // Retrieving the input streams
                InputStream myInputStream = mySocket.getInputStream();
                DataInput myDataInput = new DataInputStream(myInputStream);

                String incomingMessage = myDataInput.readUTF();
                Tools.PopInformationMessage(Name, "Received socket message:\n\n" + incomingMessage);

                // Sleeping for 30 seconds
                Tools.GoToSleep(30000);

                // Closing the streams
                myOutputStream.close();
                myInputStream.close();

                // Closing the socket
                mySocket.close();

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
			e.printStackTrace();
		}

    }

}