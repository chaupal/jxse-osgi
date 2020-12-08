/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.multicastsockets;

import net.chaupal.practical.jxta.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
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
import net.jxta.socket.JxtaMulticastSocket;

public class RendezVousHansAMulticastParticipant {
    
    // Static attributes
    public static final String Name = "RendezVous Hans, a JXTA multicast socket participant";
    public static final int TcpPort = 9712;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    public static PipeAdvertisement getPipeAdvertisement() {
        
        // Creating a Pipe Advertisement
        PipeAdvertisement myPipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        PipeID MyPipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());

        myPipeAdvertisement.setPipeID(MyPipeID);
        myPipeAdvertisement.setType(PipeService.PropagateType);
        myPipeAdvertisement.setName("Test Multicast");
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

            Tools.CheckForMulticastUsage(Name, myNetworkConfigurator);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());
            
            // Waiting for other peers to connect to JXTA
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect to JXTA");

            // Creating the JXTA multicast socket
            JxtaMulticastSocket myMulticastSocket = new JxtaMulticastSocket(netPeerGroup, getPipeAdvertisement());
            Tools.PopInformationMessage(Name, "JXTA multicast socket created");
            
            // Creating a datagram and sending it
            String message = "Hello from " + Name;
            DatagramPacket MyDatagramPacket = new DatagramPacket(message.getBytes(), message.length());
            Tools.PopInformationMessage(Name, "Multicasting following message:\n\n" + message);
            myMulticastSocket.send(MyDatagramPacket);
            
            // Sleeping a little (30 seconds)
            Tools.GoToSleep(30000);

            // Closing the JXTA socket
            myMulticastSocket.close();
             
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