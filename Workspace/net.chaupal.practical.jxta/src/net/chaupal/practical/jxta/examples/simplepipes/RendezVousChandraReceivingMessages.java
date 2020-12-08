/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.simplepipes;

import net.chaupal.practical.jxta.tools.Tools;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.membership.MembershipService;
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

public class RendezVousChandraReceivingMessages implements PipeMsgListener {
    
    // PipeService.UnicastType or PipeService.UnicastSecureType or PipeService.PropagateType
    public static final String PipeType = PipeService.UnicastType;

    // Static attributes
    public static final String Name = "RendezVous Chandra, Receiving Messages";
    public static final int TcpPort = 9710;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    // Catching messages
    public void pipeMsgEvent(PipeMsgEvent PME) {
        
        // We received a message
        Message receivedMessage = PME.getMessage();
        String TheText = receivedMessage.getMessageElement("DummyNameSpace", "HelloElement").toString();

        // Notifying the user
        Tools.PopInformationMessage(Name, "Received message:\n\n" + TheText);
        
    }
    
    public static PipeAdvertisement getPipeAdvertisement() {
        
        // Creating a Pipe Advertisement
        PipeAdvertisement myPipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        PipeID myPipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());

        myPipeAdvertisement.setPipeID(myPipeID);
        myPipeAdvertisement.setType(PipeType);
        myPipeAdvertisement.setName("Test Pipe");
        myPipeAdvertisement.setDescription("Created by " + Name);
        
        return myPipeAdvertisement;
        
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
            
            // Saving the configuration
            //myNetworkConfigurator.save();

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup netPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());


            // Retrieving the PSE membership service
            MembershipService myMembershipService = netPeerGroup.getMembershipService();

            // Waiting for other peers to connect to JXTA
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect to JXTA");

            // Creating an input pipe
            PipeService myPipeService = netPeerGroup.getPipeService();
            myPipeService.createInputPipe(getPipeAdvertisement(), new RendezVousChandraReceivingMessages());
            
            // Displaying pipe advertisement to identify potential compilation issues
            System.out.println(RendezVousChandraReceivingMessages.getPipeAdvertisement().toString());
            
            // Going to sleep for some time
            Tools.GoToSleep(60000);
            
            // Retrieving connected peers
            Tools.popConnectedPeers(netPeerGroup.getRendezVousService(), Name);
            
            // Resigning all credentials
            myMembershipService.resign();

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();
            
        } catch (IOException ex) {
            
            Tools.PopErrorMessage(Name, ex.toString());
            
        } catch (PeerGroupException ex) {
            
            Tools.PopErrorMessage(Name, ex.toString());
            
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}