/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.security;

import net.chaupal.practical.jxta.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.pse.FileKeyStoreManager;
import net.jxta.impl.membership.pse.PSEUtils;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;

public class WorkingWithAKeystore {
    
    public static final String Name = "Example_610";
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);
    
    public static final String myPrincipalName = "Principal - " + Name;
    public static final String myPrivateKeyPassword = "PrivateKey Password - " + Name;

    public static final String myKeyStoreFileName = "MyKeyStoreFile";
    public static final String myKeyStoreLocation = "." + System.getProperty("file.separator") + Name + File.separator + "MyKeyStoreLocation";
    public static final String myKeyStorePassword = "KeyStore Password - " + Name;
    public static final String myKeyStoreProvider = "KeyStore Provider - " + Name;

    public static final File myKeyStoreDirectory = new File(myKeyStoreLocation);
    public static final File myKeyStoreFile = new File(myKeyStoreLocation + File.separator + myKeyStoreFileName);

    public static final X509Certificate theX509Certificate;
    public static final PrivateKey thePrivateKey;
    
    static {
        
        // Static initialization
        PSEUtils.IssuerInfo forPSE = PSEUtils.genCert(Name, null);
        
        theX509Certificate = forPSE.cert;
        thePrivateKey = forPSE.issuerPkey;
        
    }

    public static void main(String[] args) {
        
        try {
            
            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);
            
            // Preparing data
            myKeyStoreDirectory.mkdirs();
            
            // Creating the key store
            FileKeyStoreManager myFileKeyStoreManager = new FileKeyStoreManager(
                (String)null, myKeyStoreProvider, myKeyStoreFile);
            
            myFileKeyStoreManager.createKeyStore(myKeyStorePassword.toCharArray());
            
            if (!myFileKeyStoreManager.isInitialized()) {
                Tools.PopInformationMessage(Name, "Keystore is NOT initialized");
            } else {
                Tools.PopInformationMessage(Name, "Keystore is initialized");
            }
            
            // Loading the (empty) keystore 
            KeyStore myKeyStore = myFileKeyStoreManager.loadKeyStore(myKeyStorePassword.toCharArray());
            
            // Setting data
            X509Certificate[] temp = { theX509Certificate };
            myKeyStore.setKeyEntry(PID.toString(), thePrivateKey, myPrivateKeyPassword.toCharArray(), temp);
            
            // Saving the data
            myFileKeyStoreManager.saveKeyStore(myKeyStore, myKeyStorePassword.toCharArray());
            
            // Reloading the KeyStore
            myKeyStore = myFileKeyStoreManager.loadKeyStore(myKeyStorePassword.toCharArray());
            
            // Retrieving Certificate
            X509Certificate myCertificate = (X509Certificate) myKeyStore.getCertificate(PID.toString());
            
            if (myCertificate==null) {
                Tools.PopInformationMessage(Name, "X509 Certificate CANNOT be retrieved");
            } else {
                Tools.PopInformationMessage(Name, "X509 Certificate can be retrieved");
                System.out.println(myCertificate.toString());
            }

            // Retrieving private key 
            PrivateKey myPrivateKey = (PrivateKey) myKeyStore.getKey(PID.toString(), myPrivateKeyPassword.toCharArray());
            
            if (myPrivateKey==null) {
                Tools.PopInformationMessage(Name, "Private key CANNOT be retrieved");
            } else {
                Tools.PopInformationMessage(Name, "Private key can be retrieved");
                System.out.println(myPrivateKey.toString());
            }

        } catch (NoSuchAlgorithmException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (UnrecoverableKeyException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (NoSuchProviderException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (KeyStoreException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (IOException Ex) {
            
            // Raised when access to local file and directories caused an error
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

}