/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *  
 */

package net.chaupal.practical.jxta.tools;

import net.jxta.impl.membership.pse.FileKeyStoreManager;
import net.jxta.impl.platform.NetworkManager;

import java.io.File;

public class KeystoreCreation {

    public static void main(String args[]) throws Throwable {

        try {

            // Preparing data
            String MyKeyStoreFileName = "MyKeyStoreFile";
            String MyKeyStoreLocation = "." + File.separator + "MyKeyStoreLocation";
            String MyKeyStorePassword = "My Key Store Password";
            String MyKeyStoreProvider = "Me Myself And I";

            File MyKeyStoreDirectory = new File(MyKeyStoreLocation);
            File MyKeyStoreFile = new File(MyKeyStoreLocation + File.separator
                    + MyKeyStoreFileName);

            // Deleting any existing key store and content
            NetworkManager.RecursiveDelete(MyKeyStoreDirectory);
            MyKeyStoreDirectory.mkdirs();

            // Creating the key store
            FileKeyStoreManager MyFileKeyStoreManager = new FileKeyStoreManager(
                    (String) null, MyKeyStoreProvider, MyKeyStoreFile);

            MyFileKeyStoreManager.createKeyStore(MyKeyStorePassword.toCharArray());

            // Checking initialization
            if (MyFileKeyStoreManager.isInitialized()) {

                System.out.println("Keystore initialized successfully");

            } else {

                System.out.println("Keystore NOT initialized successfully");

            }

        } catch (Exception Ex) {

            Ex.printStackTrace();

        }

    }

}
