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
import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.document.MimeMediaType;
import net.jxta.document.XMLElement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.id.IDFactory;
import net.jxta.impl.access.pse.PSEAccessService;
import net.jxta.impl.content.ContentServiceImpl;
import net.jxta.impl.membership.pse.FileKeyStoreManager;
import net.jxta.impl.membership.pse.PSEMembershipService;
import net.jxta.impl.membership.pse.PSEUtils;
import net.jxta.impl.membership.pse.StringAuthenticator;
import net.jxta.impl.peergroup.CompatibilityUtils;
import net.jxta.impl.peergroup.StdPeerGroup;
import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
import net.jxta.membership.MembershipService;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.IModuleDefinitions;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.peergroup.core.Module;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleImplAdvertisement;

public class CreatingACustomPSEPeerGroup {
    
    public static final String Name = "Example_620";
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

    public static final String PsePeerGroupName = "PSE peer group name";
    public static final PeerGroupID PsePeerGroupID = IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, PsePeerGroupName.getBytes());
    
    static {
        
        // Static initialization of certificates
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

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Setting the keystore
            myNetworkConfigurator.setKeyStoreLocation(myKeyStoreFile.toURI());
            myNetworkConfigurator.setPassword(myKeyStorePassword);

            // Starting the network
            PeerGroup myNetPeerGroup = myNetworkManager.startNetwork();

            // Checking membership implementation
            MembershipService npgMembership = myNetPeerGroup.getMembershipService();

            Tools.PopInformationMessage(Name, "NetPeerGroup membership implementation:\n"
                + npgMembership.getClass().getSimpleName());

            // Creating a child group with PSE
            PeerGroup ChildPeerGroup = myNetPeerGroup.newGroup(
                    PsePeerGroupID,
                    createAllPurposePeerGroupWithPSEModuleImplAdv(),
                    PsePeerGroupName,
                    "Checking PSE..."
                    );

            if (Module.START_OK != ChildPeerGroup.startApp(new String[0]))
                System.err.println("Cannot start PSE peergroup");

            // Checking membership implementation
            MembershipService childGroupMembership = ChildPeerGroup.getMembershipService();

            Tools.PopInformationMessage(Name, "Child group membership implementation:\n"
                + childGroupMembership.getClass().getSimpleName());

            // Joining the peer group
            AuthenticationCredential myAuthenticationCredit = new
                AuthenticationCredential( myNetPeerGroup, "StringAuthentication", null );

            StringAuthenticator myStringAuthenticator = (StringAuthenticator) childGroupMembership.apply(myAuthenticationCredit);

            myStringAuthenticator.setAuth1_KeyStorePassword(myKeyStorePassword);
            myStringAuthenticator.setAuth2Identity(PID);
            myStringAuthenticator.setAuth3_IdentityPassword(myPrivateKeyPassword);

            Credential myCredential = null;

            if (myStringAuthenticator.isReadyForJoin()) {
                myCredential = childGroupMembership.join(myStringAuthenticator);
            }

            if (myCredential!=null) {
                Tools.PopInformationMessage(Name, "Credentials created successfully");
            } else {
                Tools.PopInformationMessage(Name, "Credentials NOT created successfully");
            }

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();

        } catch (PeerGroupException Ex) {

            Tools.PopErrorMessage(Name, Ex.toString());

        } catch (NoSuchAlgorithmException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (UnrecoverableKeyException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (NoSuchProviderException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (KeyStoreException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (IOException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (ProtocolNotSupportedException Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

    public static ModuleImplAdvertisement createAllPurposePeerGroupWithPSEModuleImplAdv() {

        ModuleImplAdvertisement implAdv = CompatibilityUtils.createModuleImplAdvertisement(
        		IModuleDefinitions.allPurposePeerGroupSpecID, StdPeerGroup.class.getName(),
            "General Purpose Peer Group with PSE Implementation");

        // Create the service list for the group.
        StdPeerGroupParamAdv paramAdv = new StdPeerGroupParamAdv();

        // set the services
        paramAdv.addService(IModuleDefinitions.endpointClassID, IModuleDefinitions.refEndpointSpecID);
        paramAdv.addService(IModuleDefinitions.resolverClassID, IModuleDefinitions.refResolverSpecID);
        paramAdv.addService(IModuleDefinitions.membershipClassID, PSEMembershipService.pseMembershipSpecID);
        paramAdv.addService(IModuleDefinitions.accessClassID, PSEAccessService.PSE_ACCESS_SPEC_ID);

        // standard services
        paramAdv.addService(IModuleDefinitions.discoveryClassID, IModuleDefinitions.refDiscoverySpecID);
        paramAdv.addService(IModuleDefinitions.rendezvousClassID, IModuleDefinitions.refRendezvousSpecID);
        paramAdv.addService(IModuleDefinitions.pipeClassID, IModuleDefinitions.refPipeSpecID);
        paramAdv.addService(IModuleDefinitions.peerinfoClassID, IModuleDefinitions.refPeerinfoSpecID);

        paramAdv.addService(IModuleDefinitions.contentClassID, ContentServiceImpl.MODULE_SPEC_ID);

        // Insert the newParamAdv in implAdv
        XMLElement<?> paramElement = (XMLElement<?>) paramAdv.getDocument(MimeMediaType.XMLUTF8);
        implAdv.setParam(paramElement);

        return implAdv;

    }

}