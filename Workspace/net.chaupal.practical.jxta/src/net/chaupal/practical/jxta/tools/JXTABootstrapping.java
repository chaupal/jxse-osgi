/*
 *
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 */

package net.chaupal.practical.jxta.tools;

import net.jxta.exception.PeerGroupException;
import net.jxta.impl.peergroup.GenericPeerGroup;
import net.jxta.impl.platform.NetworkManager;
import net.jxta.peergroup.IModuleDefinitions;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.core.IJxtaLoader;
import net.jxta.peergroup.core.Module;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.rendezvous.RendezVousService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JXTABootstrapping {

    @SuppressWarnings("unused")
	public static void main(String args[]) throws Throwable {

        try {

            // Removing verbosity
            Logger.getLogger("net.jxta").setLevel(Level.SEVERE);

            // Retrieving the JXTA loader
            IJxtaLoader TheLoader = GenericPeerGroup.getJxtaLoader();

            System.out.println("\nBefore starting the JXTA network:");

            // Trying to retrieve the RendezVous Java class
            try {

                Class<? extends Module> TheRDVClass = TheLoader.findClass(IModuleDefinitions.refRendezvousSpecID);
                System.out.println(TheRDVClass.toString());

            } catch (ClassNotFoundException Ex) {

                System.out.println("Module class not found");

            }

            // Trying to retrieve the default rendezvous implementation advertisement
            ModuleImplAdvertisement TheModuleImplAdv = TheLoader.findModuleImplAdvertisement(IModuleDefinitions.refRendezvousSpecID);

            if (TheModuleImplAdv == null) {
                System.out.println("Advertisement not found");
            } else {
                System.out.println(TheModuleImplAdv.toString());
            }


            // Creation of the network manager
            NetworkManager MyNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    "My Network Manager instance name");

            // Starting JXTA and retrieving the net peer group
            PeerGroup TheNetPeerGroup = MyNetworkManager.startNetwork();

            System.out.println("\nAfter starting the JXTA network:");

            // Trying to retrieve the RendezVous Java class
            try {

                Class<? extends Module> TheRDVClass = TheLoader.findClass(IModuleDefinitions.refRendezvousSpecID);
                System.out.println(TheRDVClass.toString());

            } catch (ClassNotFoundException Ex) {

                System.out.println("Module class not found");

            }

            // Trying to retrieve the default rendezvous implementation advertisement
            TheModuleImplAdv = TheLoader.findModuleImplAdvertisement(IModuleDefinitions.refRendezvousSpecID);

            if (TheModuleImplAdv == null) {
                System.out.println("Advertisement not found");
            } else {
                System.out.println(TheModuleImplAdv.toString());
            }

            // Checking with the rendezvous service
            System.out.println("\nChecking with RendezVous Service:");

            RendezVousService TheStandardRDVService = TheNetPeerGroup.getRendezVousService();

            ModuleImplAdvertisement TheRDVModuleImplAdv = (ModuleImplAdvertisement) TheStandardRDVService.getImplAdvertisement();

            if (TheModuleImplAdv == null) {
                System.out.println("RendezVous advertisement not found");
            } else {
                System.out.println(TheModuleImplAdv.toString());
            }

            // Stopping JXTA
            MyNetworkManager.stopNetwork();

        } catch (IOException Ex) {

            Ex.printStackTrace();

        } catch (PeerGroupException Ex) {

            Ex.printStackTrace();

        }

    }


}
