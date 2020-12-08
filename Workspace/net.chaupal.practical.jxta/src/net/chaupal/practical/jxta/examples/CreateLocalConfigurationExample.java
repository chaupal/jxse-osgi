/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *  
 */

package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.exception.ConfiguratorException;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class CreateLocalConfigurationExample {

    public static final String Name = "Example 110";

    @Test
    public void main() {
    	main(null);
    }

    public static void main(String[] args) {

        try {

            // Preparing the configuration storage location
            String localPath = "." + System.getProperty("file.separator") + "JXTA_platform_config";
            File configurationFile = new File(localPath);

            // Removing any existing configuration
            Tools.PopInformationMessage(Name, "Removing any existing local configuration");
            NetworkManager.RecursiveDelete(configurationFile);

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name,
                    configurationFile.toURI());

            // Setting the peer name 
            Tools.PopInformationMessage(Name, "Setting the peer name");
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();
            myNetworkConfigurator.setName("My peer name");

            // Saving persistence
            Tools.PopInformationMessage(Name, "Saving the local configuration in:\n\n"
                    + configurationFile.getCanonicalPath());

            myNetworkConfigurator.save();

        } catch (IOException e) {
            // Raised when access to local file and directories caused an error
            e.printStackTrace();
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

}
