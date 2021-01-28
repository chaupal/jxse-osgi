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

import org.junit.Test;

import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.peergroup.core.Module;

public class RendezVousJoeTheAstrologer {
    
    // Static attributes
    public static final String Name = "RendezVous Joe, The Astrologer";
    public static final int TcpPort = 9710;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
 
    @Test
    public void test() {
    	main(null);
    }

    // public static final 

    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Creation of network manager
            NetworkManager networkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
                    Name, ConfigurationFile.toURI());
            
            // Retrieving the network configurator
            NetworkConfigurator networkConfigurator = networkManager.getConfigurator();
            
            // Setting more configuration
            networkConfigurator.setTcpPort(TcpPort);
            networkConfigurator.setTcpEnabled(true);
            networkConfigurator.setTcpIncoming(true);
            networkConfigurator.setTcpOutgoing(true);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //networkConfigurator.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup netPeerGroup = networkManager.startNetwork();

            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + networkConfigurator.getPeerID().toString());

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            networkConfigurator.setPeerID(PID);

            // Loading the astrology service
            Tools.PopInformationMessage(Name, "Loading the astrology service");
            Module astrologyModule = netPeerGroup.loadModule(AstrologyServiceExample.moduleClassID, AstrologyServiceExample.getModuleImplementationAdvertisement());
            
            // Starting the astrology service
            Tools.PopInformationMessage(Name, "Starting the astrology service");
            astrologyModule.startApp(new String[0]);
            
            // Waiting for other peers to connect to JXTA
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect to JXTA");
            
            // Ending the service
            Tools.PopInformationMessage(Name, "Click OK to end the astrology service");
            astrologyModule.stopApp();

            // Retrieving connected peers
            Tools.popConnectedPeers(netPeerGroup.getRendezVousService(), Name);
            
            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            networkManager.stopNetwork();
            
        } catch (ProtocolNotSupportedException Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (IOException Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (PeerGroupException Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}