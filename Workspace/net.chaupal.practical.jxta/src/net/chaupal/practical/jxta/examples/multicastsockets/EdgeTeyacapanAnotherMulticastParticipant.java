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
import java.net.InetAddress;

import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.socket.JxtaMulticastSocket;

public class EdgeTeyacapanAnotherMulticastParticipant {
    
    public static final String Name = "Edge Teyacapan, another JXTA multicast socket participant";
    public static final int TcpPort = 9710;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Creation of network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();
            
            // Checking if RendezVous_Hans_A_Multicast_Participant should be a seed
            myNetworkConfigurator.clearRendezvousSeeds();
            String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousHansAMulticastParticipant.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, myNetworkConfigurator);

            // Setting Configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous connection with\n"
                    + RendezVousHansAMulticastParticipant.Name + " for maximum 2 minutes");
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());
            
            // Disabling any rendezvous autostart
            netPeerGroup.getRendezVousService().setAutoStart(false);
            
            if (myNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(netPeerGroup.getRendezVousService(),Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }

            // Creating the JXTA multicast socket
            JxtaMulticastSocket myMulticastSocket = new JxtaMulticastSocket(netPeerGroup,
                RendezVousHansAMulticastParticipant.getPipeAdvertisement());
            Tools.PopInformationMessage(Name, "JXTA multicast socket created");
            
            // Reading a datagram
            byte[] myBuffer = new byte[1000];
            DatagramPacket myDatagramPacket = new DatagramPacket(myBuffer, myBuffer.length);

            myMulticastSocket.receive(myDatagramPacket);
            String myString = new String(myDatagramPacket.getData(), 0, myDatagramPacket.getLength());

            Tools.PopInformationMessage(Name, "Received multicast message:\n\n" + myString);
            
            //
            // REM: To return a message to the sender only:
            //
            // String myResponse = "Response";
            // DatagramPacket datagramReponse = new DatagramPacket(myResponse.getBytes(), myResponse.length());
            //
            // datagramResponse.setAddress(myDatagramPacket);
            // myMulticastSocket.send(datagramResponse);
            //
            
            // Closing the JXTA socket
            myMulticastSocket.close();
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();
            
        } catch (IOException ex) {
            ex.printStackTrace();           
            // Raised when access to local file and directories caused an error
            Tools.PopErrorMessage(Name, ex.toString());
            
        } catch (PeerGroupException ex) {
            ex.printStackTrace();
            // Raised when the net peer group could not be created
            Tools.PopErrorMessage(Name, ex.toString());
            
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}