/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.sockets;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;

import java.io.*;
import java.net.Socket;

public class RendezVousLidongTheJXTASocketServer {

    // Static attributes
    public static final String Name = "RendezVous Lidong, the JXTA socket server";
    public static final int TcpPort = 9710;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    public static PipeAdvertisement getPipeAdvertisement() {

        // Creating a Pipe Advertisement
        PipeAdvertisement myPipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        PipeID myPipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());

        myPipeAdvertisement.setPipeID(myPipeID);
        myPipeAdvertisement.setType(PipeService.UnicastType);
        myPipeAdvertisement.setName("Test Socket");
        myPipeAdvertisement.setDescription("Created by " + Name);

        return myPipeAdvertisement;

    }

    public static void main(String[] args) {

        try {

            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);

            // Creation of network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
                    Name, ConfigurationFile.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Setting more configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());

            // Waiting for other peers to connect to JXTA
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect to JXTA");

            // Creating the JXTA socket server
            int BackLog = 20;
            int Timeout = 30000;

            JxtaServerSocket myJXTAServerSocket = new JxtaServerSocket(netPeerGroup, getPipeAdvertisement(), BackLog, Timeout);
            Tools.PopInformationMessage(Name, "JXTA socket server created");

            Socket mySocket = myJXTAServerSocket.accept();

            if (mySocket != null) {

                Tools.PopInformationMessage(Name, "Socket connection established");

                // Retrieving the input streams
                InputStream myInputStream = mySocket.getInputStream();
                DataInput myDataInput = new DataInputStream(myInputStream);

                String IncomingMessage = myDataInput.readUTF();
                Tools.PopInformationMessage(Name, "Received socket message:\n\n" + IncomingMessage);

                // Retrieving the output stream
                OutputStream myOutputStream = mySocket.getOutputStream();
                DataOutput MyDataOutput = new DataOutputStream(myOutputStream);

                // Sending a message
                MyDataOutput.writeUTF("Hello from " + Name);
                myOutputStream.flush();

                // Sleeping for 30 seconds
                Tools.GoToSleep(30000);

                // Closing the streams
                myOutputStream.close();
                myInputStream.close();

                // Closing the socket
                mySocket.close();

            }

            // Closing the JXTA socket server
            myJXTAServerSocket.close();

            // Retrieving connected peers
            Tools.popConnectedPeers(netPeerGroup.getRendezVousService(), Name);

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();

        } catch (IOException Ex) {

            Tools.PopErrorMessage(Name, Ex.toString());

        } catch (PeerGroupException Ex) {

            Tools.PopErrorMessage(Name, Ex.toString());

        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}