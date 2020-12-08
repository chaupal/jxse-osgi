/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxse.configuration.JxseHttpTransportConfiguration;
import net.jxse.configuration.JxsePeerConfiguration;
import net.jxse.configuration.JxseTcpTransportConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class ConfigurationObjects {

    public static final String Name = "Example 150";

    @Test
    public void main() {
    	main(null);
    }
    
    public static void main(String[] args) {

        try {

            // Creating a default configuration object
            JxsePeerConfiguration myConfig = new JxsePeerConfiguration();
            JxseHttpTransportConfiguration myHttpConfig = myConfig.getHttpTransportConfiguration();
            JxseTcpTransportConfiguration myTcpConfig = myConfig.getTcpTransportConfiguration();

            // Setting some configuration information
            myConfig.setPeerInstanceName("Config Object Test");
            myHttpConfig.setHttpIncoming(true);
            myTcpConfig.setTcpIncoming(true);

            // DON'T FORGET TO SET BACK THE TRANSPORT CONFIGURATION
            myConfig.setHttpTransportConfiguration(myHttpConfig);
            myConfig.setTcpTransportConfiguration(myTcpConfig);

            // Saving the configuration
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myConfig.storeToXML(baos, "My saving");
            System.out.println(baos.toString());

            // Reading the configuration
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            JxsePeerConfiguration myReadConfig = new JxsePeerConfiguration();
            myReadConfig.loadFromXML(bais);

            // Checking
            Tools.PopInformationMessage(Name, "The configuration name is: "
                    + myReadConfig.getPeerInstanceName());

            JxseHttpTransportConfiguration myReadHttpConfig = myReadConfig.getHttpTransportConfiguration();
            JxseTcpTransportConfiguration myReadTcpConfig = myReadConfig.getTcpTransportConfiguration();

            Tools.PopInformationMessage(Name, "HTTP Incoming is: "
                    + Boolean.valueOf(myReadHttpConfig.getHttpIncoming()));

            Tools.PopInformationMessage(Name, "TCP Incoming is: "
                    + Boolean.valueOf(myReadTcpConfig.getTcpIncoming()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
