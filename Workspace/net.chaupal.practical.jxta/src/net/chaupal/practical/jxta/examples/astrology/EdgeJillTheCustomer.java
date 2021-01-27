/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.astrology;

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
import net.jxta.impl.util.JxtaBiDiPipe;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

public class EdgeJillTheCustomer implements PipeMsgListener {
    
    public static final String Name = "Edge Jill, The Customer";
    public static final int TcpPort = 9712;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public void pipeMsgEvent(PipeMsgEvent PME) {
        // We received a message
        Message receivedMessage = PME.getMessage();
        String TheText = receivedMessage.getMessageElement(AstrologyServiceExample.NameSpace, AstrologyServiceExample.PredictionElement).toString();

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
            NetworkManager networkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE, Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator networkConfigurator = networkManager.getConfigurator();
            
            // Checking if RendezVous Joe should be a seed
            networkConfigurator.clearRendezvousSeeds();
            String seed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousJoeTheAstrologer.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, seed, networkConfigurator);

            // Setting Configuration
            networkConfigurator.setTcpPort(TcpPort);
            networkConfigurator.setTcpEnabled(true);
            networkConfigurator.setTcpIncoming(true);
            networkConfigurator.setTcpOutgoing(true);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //networkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous connection with\n"
                    + RendezVousJoeTheAstrologer.Name + " for maximum 2 minutes");
            PeerGroup netPeerGroup = networkManager.startNetwork();

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            networkConfigurator.setPeerID(PID);
            
            // Disabling any rendezvous autostart
            netPeerGroup.getRendezVousService().setAutoStart(false);
            
            if (networkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(netPeerGroup.getRendezVousService(),Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }

            // Preparing the listener and Creating the BiDiPipe
            PipeMsgListener listener = new EdgeJillTheCustomer();
            
            // Retrieving the pipe advertisement from the module implementation advertisement
            ModuleSpecAdvertisement moduleSpecAdvertisement = AstrologyServiceExample.getModuleSpecificationAdvertisement();

            PipeAdvertisement pipeAdvertisement = moduleSpecAdvertisement.getPipeAdvertisement();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            JxtaBiDiPipe biDiPipe = new JxtaBiDiPipe(netPeerGroup, pipeAdvertisement, 30000, listener);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (biDiPipe.isBound()) {
            
                Tools.PopInformationMessage(Name, "Connection with astrology service established, asking for predictions!");

                // Sending request for prediction message !!!
                Message message = new Message();

                StringMessageElement stringMessageElement = new StringMessageElement(AstrologyServiceExample.BirthDateElement, "04112036", null);
                message.addMessageElement(AstrologyServiceExample.NameSpace, stringMessageElement);

                stringMessageElement = new StringMessageElement(AstrologyServiceExample.BirthLocationElement, "New Jersey, USA", null);
                message.addMessageElement(AstrologyServiceExample.NameSpace, stringMessageElement);

                stringMessageElement = new StringMessageElement(AstrologyServiceExample.CustomerNameElement, Name, null);
                message.addMessageElement(AstrologyServiceExample.NameSpace, stringMessageElement);

                biDiPipe.sendMessage(message);

                // Sleeping for 5 seconds
                Tools.GoToSleep(5000);
                
            }
            
            // Stopping the network and the bidipipe
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            biDiPipe.close();
            networkManager.stopNetwork();
            
        } catch (IOException Ex) {
            // Raised when access to local file and directories caused an error
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (PeerGroupException Ex) {
            // Raised when the net peer group could not be created
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (ConfiguratorException e1) {
			e1.printStackTrace();
		}

    }

}