/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.bidirectionalpipes;


import net.chaupal.practical.jxta.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.impl.util.JxtaBiDiPipe;

public class EdgeQuiniselaAtTheOtherEnd implements PipeMsgListener {
    
    public static final String Name = "Edge Quinisela, at the other end";
    public static final int TcpPort = 9712;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public void pipeMsgEvent(PipeMsgEvent PME) {
        
        // We received a message
        Message ReceivedMessage = PME.getMessage();
        String TheText = ReceivedMessage.getMessageElement("DummyNameSpace", "HelloElement").toString();

        // Notifying the user
        Tools.PopInformationMessage(Name, "Received message:\n\n" + TheText);
        
    }

    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Creation of network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
            
            // Checking if RendezVous_Adelaide_At_One_End should be a seed
            MyNetworkConfigurator.clearRendezvousSeeds();
            String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousAdelaideAtOneEnd.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);

            // Setting Configuration
            MyNetworkConfigurator.setTcpPort(TcpPort);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);
            MyNetworkConfigurator.setUseMulticast(false);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //MyNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous connection with\n"
                    + RendezVousAdelaideAtOneEnd.Name + " for maximum 2 minutes");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + MyNetworkConfigurator.getPeerID().toString());


            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);
            
            if (MyNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(),Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }

            // Preparing the listener and Creating the BiDiPipe
            PipeMsgListener MyListener = new EdgeQuiniselaAtTheOtherEnd();
            JxtaBiDiPipe MyBiDiPipe = new JxtaBiDiPipe(NetPeerGroup, RendezVousAdelaideAtOneEnd.GetPipeAdvertisement(), 30000, MyListener);

            Tools.GoToSleep(10000);

            if (MyBiDiPipe.isBound()) {
            
                Tools.PopInformationMessage(Name, "Bidirectional pipe created!");

                // Sending a hello message !!!
                Message MyMessage = new Message();
                StringMessageElement MyStringMessageElement = new StringMessageElement("HelloElement", "Hello from " + Name, null);
                MyMessage.addMessageElement("DummyNameSpace", MyStringMessageElement);

                MyBiDiPipe.sendMessage(MyMessage);

                // Sleeping for 10 seconds
                Tools.GoToSleep(10000);
            
                // Sending a goodbye message !!!
                MyMessage = new Message();
                MyStringMessageElement = new StringMessageElement("HelloElement", "Goodbye from " + Name, null);
                MyMessage.addMessageElement("DummyNameSpace", MyStringMessageElement);

                MyBiDiPipe.sendMessage(MyMessage);
            
                // Sleeping for 10 seconds
                Tools.GoToSleep(10000);
                
            }
            
            // Closing the bidipipe
            MyBiDiPipe.close();
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();
            
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