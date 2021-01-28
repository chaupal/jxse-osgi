package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import java.io.IOException;
import net.jxse.OSGi.JxseOSGiFramework;
import net.jxse.OSGi.Services.JxseOSGiNetworkManagerService;
import net.jxse.configuration.JxsePeerConfiguration;
import net.jxse.configuration.JxsePeerConfiguration.ConnectionMode;
import net.jxta.configuration.JxtaConfigurationException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peergroup.PeerGroupID;

import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.util.tracker.ServiceTracker;

@Deprecated //CP: Has no function in an OSGI container
public class ConnectingWithOSGi {
    
    public static final String Name = "Example 160";
    
    // The NetworkManager service instance
    private static JxseOSGiNetworkManagerService TheNMS;

    // The OSGi service tracker for the NetworkManager Service
    private static ServiceTracker<?,?> ST;

    @Test
    public void main() {
    	main(null);
    }

    public static void main(String[] args) {

        try {

            // Starting the OSGi framwork (Felix)
            JxseOSGiFramework.INSTANCE.start();

            // Retrieving the NetworkManager service
            ST = JxseOSGiFramework.getServiceTracker(JxseOSGiNetworkManagerService.class);

            // Starting to track the service
            ST.open();

            // Retrieving the NetworkManager service for at most 5 seconds
            TheNMS = (JxseOSGiNetworkManagerService) ST.waitForService(5000);

            if (TheNMS==null) {
                Tools.PopErrorMessage(Name, "Could not retrieve the NetworkManager "
                    + "service within 5 seconds");
                System.exit(-1);
            }

            // Creating a peer configuration
            JxsePeerConfiguration MyConfig = new JxsePeerConfiguration();
            MyConfig.setConnectionMode(ConnectionMode.ADHOC);
            MyConfig.setPeerID(IDFactory.newPeerID(PeerGroupID.worldPeerGroupID));
            MyConfig.setPeerInstanceName("Poupoudipou");
            
            // Setting the configuration in the NetworkManager OSGi service
            TheNMS.setPeerConfiguration(MyConfig);

            // Retrieving a configured network manager
            NetworkManager MyNM = TheNMS.getConfiguredNetworkManager();

            // Starting and stopping the network
            MyNM.startNetwork();
            MyNM.stopNetwork();

            // Stopping the service tracking
            ST.close();
            TheNMS = null;

            // Stopping the OSGI framework
            JxseOSGiFramework.INSTANCE.stop();

            // Waiting for stop for maximum 20 seconds
            FrameworkEvent FE = JxseOSGiFramework.INSTANCE.waitForStop(20000);

            // Checking whether we stopped properly
            if ( FE.getType() != FrameworkEvent.STOPPED ) {
                Tools.PopErrorMessage(Name, "OSGi framework failed to stop after 20 seconds, event type: " + FE.getType() );
            } 

        } catch (PeerGroupException ex) {

            Tools.PopErrorMessage(Name, ex.toString());

        } catch (JxtaConfigurationException ex) {

            Tools.PopErrorMessage(Name, ex.toString());

        } catch (InterruptedException ex) {

            Tools.PopErrorMessage(Name, ex.toString());

        } catch (BundleException ex) {

            Tools.PopErrorMessage(Name, ex.toString());

        } catch (IOException ex) {

            Tools.PopErrorMessage(Name, ex.toString());

        } catch (Exception ex) {

            Tools.PopErrorMessage(Name, ex.toString());

        }
    }
        
}
