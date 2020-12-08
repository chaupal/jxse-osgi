/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * Based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.peersandgroups;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.document.MimeMediaType;
import net.jxta.document.XMLElement;
import net.jxta.exception.ConfiguratorException;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.impl.content.ContentServiceImpl;
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

import java.io.File;
import java.io.IOException;

public class CreatingCustomPeerGroups {

    public static final String Name = "Example_220";
    public static final PeerID PID = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    public static final String PeerGroupName = "Custom peer group name";
    public static final PeerGroupID CustPeerGroupID = IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, PeerGroupName.getBytes());

    public static void main(String[] args) {

        try {

            // Removing any existing configuration?
            Tools.CheckForExistingConfigurationDeletion(Name, ConfigurationFile);

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving the configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Setting configuration
            //myNetworkConfigurator.setTcpPort(TcpPort);
            myNetworkConfigurator.setTcpEnabled(true);
            myNetworkConfigurator.setTcpIncoming(true);
            myNetworkConfigurator.setTcpOutgoing(true);
            myNetworkConfigurator.setUseMulticast(false);


            // Starting the network
            PeerGroup myNetPeerGroup = myNetworkManager.startNetwork();

            // Creating a child group with PSE
            PeerGroup childPeerGroup = myNetPeerGroup.newGroup(
                    CustPeerGroupID,
                    createAllPurposePeerGroupImplAdv(),
                    PeerGroupName,
                    "Custom peergroup...",
                    false
            );

            if (Module.START_OK != childPeerGroup.startApp(new String[0]))
                System.err.println("Cannot start custom peergroup");

            // Checking membership implementation
            MembershipService childGroupMembership = childPeerGroup.getMembershipService();

            Tools.PopInformationMessage(Name, "Custom group membership implementation:\n"
                    + childGroupMembership.getClass().getSimpleName());

            // Stopping the network
            Tools.PopInformationMessage(Name, "Stop the JXTA network");
            myNetworkManager.stopNetwork();

        } catch (PeerGroupException Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (IOException Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
        } catch (ConfiguratorException e) {
			e.printStackTrace();
		}

    }

    public static ModuleImplAdvertisement createAllPurposePeerGroupImplAdv() {

        ModuleImplAdvertisement implAdv = CompatibilityUtils.createModuleImplAdvertisement(
        		IModuleDefinitions.allPurposePeerGroupSpecID, StdPeerGroup.class.getName(),
                "General Purpose Peer Group");

        // Create the service list for the group.
        StdPeerGroupParamAdv paramAdv = new StdPeerGroupParamAdv();

        // set the services
        paramAdv.addService(IModuleDefinitions.endpointClassID, IModuleDefinitions.refEndpointSpecID);
        paramAdv.addService(IModuleDefinitions.resolverClassID, IModuleDefinitions.refResolverSpecID);
        paramAdv.addService(IModuleDefinitions.membershipClassID, IModuleDefinitions.refMembershipSpecID);
        paramAdv.addService(IModuleDefinitions.accessClassID, IModuleDefinitions.refAccessSpecID);

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