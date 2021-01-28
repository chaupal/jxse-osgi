package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkConfigurator;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class RendezVousAminah {

    public static final String Name = "RendezVous Aminah";
    public static final int TcpPort = 9713;
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {

        try {

            // Check for removal of any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS,
                    Name, ConfigurationFile.toURI());

            // Retrieving the configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Setting configuration
            myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);
            //myNetworkConfigurator.setCertificate(null);

            // Setting the Peer ID
            //Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + PID.toString());
            //myNetworkConfigurator.setPeerID(PID);
            //myNetworkManager.setPeerID(PID);

            // Starting the JXTA network
            Tools.PopInformationMessage(Name, "Start the JXTA network");
            PeerGroup NetPeerGroup = myNetworkManager.startNetwork();

            Tools.PopInformationMessage(Name, "Setting the peer ID to :\n\n" + myNetworkConfigurator.getPeerID().toString());

            // Waiting for rendezvous connection
            Tools.PopInformationMessage(Name, "Waiting for other peers to connect");

            // Retrieving connected peers
            Tools.popConnectedEdgePeers(NetPeerGroup.getRendezVousService(), Name);

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();

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
