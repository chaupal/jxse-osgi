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

import org.junit.Test;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.IOUtils;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;

public class RendezVousAdelaideAtOneEnd implements PipeMsgListener {
    
    // Static attributes
    public static final String Name = "RendezVous Adelaide, at one end";
    public static final int TcpPort = 9710;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public void pipeMsgEvent(PipeMsgEvent PME) {
        
        // We received a message
        Message ReceivedMessage = PME.getMessage();
        String TheText = ReceivedMessage.getMessageElement("DummyNameSpace", "HelloElement").toString();

        // Notifying the user
        Tools.PopInformationMessage(Name, "Received message:\n\n" + TheText);
        
    }

    public static PipeAdvertisement GetPipeAdvertisement() {
        
        // Creating a Pipe Advertisement
        PipeAdvertisement MyPipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        PipeID MyPipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());

        MyPipeAdvertisement.setPipeID(MyPipeID);
        MyPipeAdvertisement.setType(PipeService.UnicastType);
        MyPipeAdvertisement.setName("Test BidiPipe");
        MyPipeAdvertisement.setDescription("Created by " + Name);
        
        return MyPipeAdvertisement;      
    }
 
    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {
        
    	JxtaServerPipe myBiDiPipeServer = null;
    	try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Creation of network manager
            NetworkManager MyNetworkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator MyNetworkConfigurator = MyNetworkManager.getConfigurator();
            
            // Setting more configuration
            MyNetworkConfigurator.setTcpPort(TcpPort);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //MyNetworkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + MyNetworkConfigurator.getPeerID().toString());

            // Waiting for other peers to connect to JXTA
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect to JXTA");

            // Preparing the listener and creating the BiDiPipe
            PipeMsgListener MyListener = new RendezVousAdelaideAtOneEnd();
            myBiDiPipeServer = new JxtaServerPipe(NetPeerGroup, GetPipeAdvertisement());
            Tools.PopInformationMessage(Name, "Bidirectional pipe server created!");
            myBiDiPipeServer.setPipeTimeout(30000);
            
            JxtaBiDiPipe MyBiDiPipe = myBiDiPipeServer.accept();
            
            if (MyBiDiPipe != null) {
            
                MyBiDiPipe.setMessageListener(MyListener);
                Tools.PopInformationMessage(Name, "Bidirectional pipe connection established!");

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
                
                // Closing the bidi pipe
                MyBiDiPipe.close();

            }
            
            // Closing the bidi pipe server
            MyBiDiPipe.close();
            
            // Retrieving connected peers
            Tools.popConnectedPeers(NetPeerGroup.getRendezVousService(), Name);

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            MyNetworkManager.stopNetwork();
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (PeerGroupException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}
        finally {
        	IOUtils.closeQuietely(myBiDiPipeServer);
        }

    }

}