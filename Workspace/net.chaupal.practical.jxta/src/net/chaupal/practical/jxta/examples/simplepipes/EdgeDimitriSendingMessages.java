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
import java.net.InetAddress;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

public class EdgeDimitriSendingMessages implements OutputPipeListener {
    
    public static final String Name = "Edge Dimitri, Sending Messages";
    public static final int TcpPort = 9712;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    // Catching connections to the pipe
    public void outputPipeEvent(OutputPipeEvent OPE) {
        
        try {

            // Notifying the user
            Tools.PopInformationMessage(Name, "Someone connected to the pipe, sending hello message!");

            // Retrieving the output pipe to the peer who connected to the input end
            OutputPipe MyOutputPipe = OPE.getOutputPipe();
            
            // Creating a Hello message !!!
            Message myMessage = new Message();
            StringMessageElement MyStringMessageElement = new StringMessageElement("HelloElement", "Hello from " + Name, null);
            myMessage.addMessageElement("DummyNameSpace", MyStringMessageElement);

            // Sending the message
            MyOutputPipe.send(myMessage);
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }
        
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
            
            // Checking if RendezVous_Jack should be a seed
            MyNetworkConfigurator.clearRendezvousSeeds();
            String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousChandraReceivingMessages.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, MyNetworkConfigurator);

            // Setting Configuration
            MyNetworkConfigurator.setTcpPort(TcpPort);
            MyNetworkConfigurator.setTcpEnabled(true);
            MyNetworkConfigurator.setTcpIncoming(true);
            MyNetworkConfigurator.setTcpOutgoing(true);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //MyNetworkConfigurator.setPeerID(PID);
            
            // Saving the configuration
            //MyNetworkConfigurator.save();

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network and to wait for a rendezvous connection with\n"
                    + RendezVousChandraReceivingMessages.Name + " for maximum 2 minutes");
            PeerGroup NetPeerGroup = MyNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + MyNetworkConfigurator.getPeerID().toString());

            // Disabling any rendezvous autostart
            NetPeerGroup.getRendezVousService().setAutoStart(false);
            
            if (MyNetworkManager.waitForRendezvousConnection(120000)) {
                Tools.popConnectedRendezvous(NetPeerGroup.getRendezVousService(),Name);
            } else {
                Tools.PopInformationMessage(Name, "Did not connect to a rendezvous");
            }
            
            // Creating an output pipe
            PipeService MyPipeService = NetPeerGroup.getPipeService();
            MyPipeService.createOutputPipe(RendezVousChandraReceivingMessages.getPipeAdvertisement(),
                    new EdgeDimitriSendingMessages());
            
            // Displaying pipe advertisement to identify potential compilation issues
            System.out.println(RendezVousChandraReceivingMessages.getPipeAdvertisement().toString());

            // Going to sleep for some time
            Tools.GoToSleep(60000);
            
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