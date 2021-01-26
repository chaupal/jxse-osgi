package net.chaupal.practical.jxta.examples;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.exception.ConfiguratorException;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

public class RetrieveModifySaveExistingConfigurationExample {

    public static final String Name = "Example 120";

    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {

        try {
            
            // Preparing the configuration storage location
            String localPath = "." + System.getProperty("file.separator") + "JXTA_platform_config";
            File configurationFile = new File(localPath);

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name,
                    configurationFile.toURI());
            
            // Checking for configuration existence
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();
            
            if (myNetworkConfigurator.exists()) {
                // Found existing configuration
                Tools.PopInformationMessage(Name, "Found existing configuration");
            } else {
                // No configuration found
                Tools.PopInformationMessage(Name, "No configuration found at:\n\n"
                    + configurationFile.getCanonicalPath());
            }
                
            // Modifying new name
            String newName = "My new name @ " + System.currentTimeMillis();
            Tools.PopInformationMessage(Name, "Setting new name to: " + newName);
            myNetworkConfigurator.setName(newName);
                
            // Saving modifications
            Tools.PopInformationMessage(Name, "Saving configuration at:\n\n"
                    + configurationFile.getCanonicalPath());
            myNetworkConfigurator.save();
            
        } catch (IOException ex) {
            // Raised when access to local file and directories caused an error
            ex.printStackTrace();
            
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }
        
}
