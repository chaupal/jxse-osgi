/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.discovery;

import net.chaupal.practical.jxta.examples.RendezVousJack;
import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.impl.protocol.RdvAdv;
import net.jxta.impl.protocol.RouteAdv;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;

import org.junit.Test;

public class LocalAdvertisementsExample {

    public static final String Name = "Example_300";

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

            // Preparing context
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Checking if RendezVous_Jack should be a seed
            myNetworkConfigurator.clearRendezvousSeeds();
            String TheSeed = "tcp://" + InetAddress.getLocalHost().getHostAddress() + ":" + RendezVousJack.TcpPort;
            Tools.CheckForRendezVousSeedAddition(Name, TheSeed, myNetworkConfigurator);

            // Setting Configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);

            // Setting the Peer ID
            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);

            // Starting the network and waiting for a rendezvous connection
            //PeerGroup DefaultPeerGroup = myNetworkConfigurator.startNetwork();
            PeerGroup defaultPeerGroup = myNetworkManager.startNetwork();

            // Disabling any rendezvous autostart
            defaultPeerGroup.getRendezVousService().setAutoStart(false);

            Tools.PopInformationMessage(Name, "Waiting for rendezvous connection for maximum 60 seconds");
            if (myNetworkManager.waitForRendezvousConnection(60000)) {
                Tools.PopInformationMessage(Name, "Rendezvous connection successful!");
            } else {
                Tools.PopWarningMessage(Name, "Rendezvous connection NOT successful!");
            }

            // Retrieving local advertisements
            Tools.PopInformationMessage(Name, "Retrieving local advertisements");
            DiscoveryService TheDiscoveryService = defaultPeerGroup.getDiscoveryService();

            Enumeration<Advertisement> TheAdvEnum = TheDiscoveryService.getLocalAdvertisements(DiscoveryService.ADV, null, null);

            while (TheAdvEnum.hasMoreElements()) {

                Advertisement TheAdv = TheAdvEnum.nextElement();
                String ToDisplay = "Found " + TheAdv.getClass().getSimpleName();

                if (TheAdv.getClass().getName().compareTo(RouteAdv.class.getName()) == 0) {
                    // We found a route advertisement
                    RouteAdv Temp = (RouteAdv) TheAdv;
                    ToDisplay = ToDisplay + "\n\nto " + Temp.getDestPeerID().toString();
                } else if (TheAdv.getClass().getName().compareTo(RdvAdv.class.getName()) == 0) {
                    // We found a rendezvous advertisement
                    RdvAdv Temp = (RdvAdv) TheAdv;
                    ToDisplay = ToDisplay + "\n\nof " + Temp.getPeerID().toString();
                }

                // Displaying the advertisement
                Tools.PopInformationMessage(Name, ToDisplay);

                // Flushing advertisement
                TheDiscoveryService.flushAdvertisement(TheAdv);
            }

            // Stopping JXTA
            Tools.PopInformationMessage(Name, "Stopping the network");
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
