package net.chaupal.practical.jxta.examples;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.EndpointAddress;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.AccessPointAdvertisement;
import net.jxta.protocol.RouteAdvertisement;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.junit.Test;

public class CreatingAndSettingSeeds {

    public static final String Name = "Creating and setting seeds";
    public static final File ConfigurationFile = new File("." + System.getProperty("file.separator") + Name);

    @Test
    public void main() {
    	main(null);
    }

    public static void main(String[] args) {

        try {

            // Creation of the network manager
            NetworkManager myNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE,
                    Name, ConfigurationFile.toURI());

            // Retrieving the network configurator
            NetworkConfigurator myNetworkConfigurator = myNetworkManager.getConfigurator();

            // Clear all RendezVous seeds
            myNetworkConfigurator.clearRendezvousSeeds();

            // Creating an endpoint seed and setting it
            URI theSeed = URI.create("tcp://33.44.55.66:9202");
            myNetworkConfigurator.addSeedRendezvous(theSeed);

            // Creating a document read by seeding URIs
            XMLDocument<?> myDoc = (XMLDocument<?>) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XML_DEFAULTENCODING, "jxta:seeds");
            myDoc.addAttribute("ordered", "false");
            myDoc.addAttribute("xmlns:jxta", "http://www.jxta.org");

            // First seed
            RouteAdvertisement myRouteAdv = (RouteAdvertisement) AdvertisementFactory.newAdvertisement(RouteAdvertisement.getAdvertisementType());
            PeerID myRDV = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, "My first RDV".getBytes());

            AccessPointAdvertisement myAPA = (AccessPointAdvertisement)
                    AdvertisementFactory.newAdvertisement(AccessPointAdvertisement.getAdvertisementType());
            myAPA.addEndpointAddress(new EndpointAddress("tcp://18.22.1.69:3333"));

            myRouteAdv.setDestPeerID(myRDV);
            myRouteAdv.setDest(myAPA);

            XMLDocument<?> myRouteAdvDoc = (XMLDocument<?>) myRouteAdv.getDocument(MimeMediaType.XMLUTF8);
            Tools.copyElements(myDoc, myDoc.getRoot(), myRouteAdvDoc.getRoot(), true, false);

            // Second seed
            RouteAdvertisement myRouteAdv2 = (RouteAdvertisement) AdvertisementFactory.newAdvertisement(RouteAdvertisement.getAdvertisementType());
            PeerID myRDV2 = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, "My second RDV".getBytes());

            AccessPointAdvertisement myAPA2 = (AccessPointAdvertisement)
                    AdvertisementFactory.newAdvertisement(AccessPointAdvertisement.getAdvertisementType());
            myAPA2.addEndpointAddress(new EndpointAddress("tcp://171.17.22.4:2876"));

            myRouteAdv2.setDestPeerID(myRDV2);
            myRouteAdv2.setDest(myAPA2);

            XMLDocument<?> myRouteAdvDoc2 = (XMLDocument<?>) myRouteAdv2.getDocument(MimeMediaType.APPLICATION_XML_DEFAULTENCODING);
            Tools.copyElements(myDoc, myDoc.getRoot(), myRouteAdvDoc2.getRoot(), true, false);

            // Third seed
            RouteAdvertisement myRouteAdv3 = (RouteAdvertisement) AdvertisementFactory.newAdvertisement(RouteAdvertisement.getAdvertisementType());
            PeerID myRDV3 = IDFactory.newPeerID(PeerGroupID.defaultNetPeerGroupID, "My third RDV".getBytes());

            AccessPointAdvertisement myAPA3 = (AccessPointAdvertisement)
                    AdvertisementFactory.newAdvertisement(AccessPointAdvertisement.getAdvertisementType());
            myAPA3.addEndpointAddress(new EndpointAddress("tcp://240.28.16.57:4210"));

            myRouteAdv3.setDestPeerID(myRDV3);
            myRouteAdv3.setDest(myAPA3);

            XMLDocument<?> myRouteAdvDoc3 = (XMLDocument<?>) myRouteAdv3.getDocument(MimeMediaType.XMLUTF8);
            Tools.copyElements(myDoc, myDoc.getRoot(), myRouteAdvDoc3.getRoot(), true, false);

            // Printing the result
            myDoc.sendToStream(System.out);

        } catch (IOException ioe) {
            // Raised when access to local file and directories caused an error
            ioe.printStackTrace();
        }

    }

}