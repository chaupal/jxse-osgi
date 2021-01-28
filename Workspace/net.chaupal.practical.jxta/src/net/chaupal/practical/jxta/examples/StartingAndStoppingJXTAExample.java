package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peergroup.PeerGroup;

import java.io.IOException;

import org.junit.Test;

public class StartingAndStoppingJXTAExample {

    public static final String Name = "Example 100";

    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {

        try {
            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE, Name);

            // Starting JXTA
            Tools.PopInformationMessage(Name, "Starting JXTA network");
            PeerGroup peerGroup = myNetworkManager.startNetwork();

            // Displaying peer group information
            Tools.PopInformationMessage(Name, "Connected via Peer Group: " + peerGroup.getPeerGroupName());

            // Stopping JXTA
            Tools.PopInformationMessage(Name, "Stopping JXTA network");
            myNetworkManager.stopNetwork();

        } catch (IOException e) {
            // Raised when access to local file and directories caused an error
            e.printStackTrace();
        } catch (PeerGroupException e) {
            // Raised when the net peer group could not be created
            e.printStackTrace();
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}
    }
}
