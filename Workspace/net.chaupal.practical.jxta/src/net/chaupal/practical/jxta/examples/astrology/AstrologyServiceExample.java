/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.astrology;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.peergroup.CompatibilityUtils;
import net.jxta.impl.protocol.ModuleImplAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.peergroup.core.ModuleClassID;
import net.jxta.peergroup.core.ModuleSpecID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.service.Service;
import net.jxta.impl.util.JxtaBiDiPipe;
import net.jxta.impl.util.JxtaServerPipe;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;

public class AstrologyServiceExample implements Service, Runnable {

    public static final String Name = "Astrology Service";

    public static final String NameSpace = "AstrologyService";

    public static final String CustomerNameElement = "CustomerName";
    public static final String BirthDateElement = "CustomerBirthDate";
    public static final String BirthLocationElement = "CustomerBirthLocation";

    public static final String PredictionElement = "Prediction";

    public static final String MyModuleClassIDString = "urn:jxta:uuid-F7A712D25D3047B88656FD706AEDE8DB05";
    public static final String MyModuleSpecIDString = "urn:jxta:uuid-F7A712D25D3047B88656FD706AEDE8DBC6A510B2026F4FD59A7DFA4F6712142506";

    public static ModuleClassID moduleClassID = null;
    public static ModuleSpecID moduleSpecID = null;

    static {

        try {
            moduleClassID = ModuleClassID.create(new URI(MyModuleClassIDString));
            moduleSpecID = ModuleSpecID.create(new URI(MyModuleSpecIDString));

            System.out.println("Astrology Service moduleClassID = " + moduleClassID);
            System.out.println("Astrology Service moduleSpecID = " + moduleSpecID);
        } catch (Exception Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
        }

    }

    // Not static
    private PeerGroup peerGroup = null;
    private ID id = null;
    private ModuleImplAdv implementationAdvertisement = null;

    private JxtaServerPipe biDiPipeServer = null;

    public static void main(String[] args) {
        // Dummy main method for compilation
    }

    public AstrologyServiceExample() {

    }

    /**
     * Create a Pipe Advertisement
     *
     * @return PipeAdvertisement
     */
    public static PipeAdvertisement getPipeAdvertisement() {

        PipeAdvertisement pipeAdvertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        PipeID pipeID = IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID, Name.getBytes());

        pipeAdvertisement.setPipeID(pipeID);
        pipeAdvertisement.setType(PipeService.UnicastType);
        pipeAdvertisement.setName("Astrology Service Pipe");
        pipeAdvertisement.setDescription("Created by " + Name);

        return pipeAdvertisement;

    }

    public static ModuleSpecAdvertisement getModuleSpecificationAdvertisement() {

        ModuleSpecAdvertisement result = (ModuleSpecAdvertisement) AdvertisementFactory.newAdvertisement(ModuleSpecAdvertisement.getAdvertisementType());

        result.setCreator("The Astrologers");
        result.setDescription("Astrology Service");
        result.setModuleSpecID(moduleSpecID);
        result.setVersion("1.0");
        result.setPipeAdvertisement(getPipeAdvertisement());

        return result;

    }

    public static ModuleImplAdvertisement getModuleImplementationAdvertisement() {
        String adType = ModuleImplAdvertisement.getAdvertisementType();
        ModuleImplAdvertisement result = (ModuleImplAdvertisement) AdvertisementFactory.newAdvertisement(adType);

        // Setting parameters
        result.setDescription("Astrology Service");
        result.setModuleSpecID(moduleSpecID);
        result.setProvider(Name);
        result.setCode(AstrologyServiceExample.class.getName());

        // Setting compatibility & binding
        result.setCompat(CompatibilityUtils.createDefaultCompatStatement());
        //result.setCompat(StdPeerGroup.STD_COMPAT);
        //result.setCompat(null);

        // Retrieving the location of the .jar file
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile = null;

        Tools.PopInformationMessage(Name, "Retrieving the implementation location of the astrology service");

        int returnedValue = fileChooser.showOpenDialog(null);

        if (returnedValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }

        if (selectedFile == null) {
            Tools.PopWarningMessage(Name, "No file selected");
        } else {
            result.setUri(selectedFile.toURI().toString());
        }

        return result;

    }

    public Service getInterface() {
        return this;
    }

    public Advertisement getImplAdvertisement() {
        return implementationAdvertisement;
    }

    public ID getID() {
        return id;
    }

    /**
     * Initialization
     *
     * @param inPG
     * @param inID
     * @param inAdvertisement
     * @throws PeerGroupException
     */
    public void init(PeerGroup inPG, ID inID, Advertisement inAdvertisement) throws PeerGroupException {
        peerGroup = inPG;
        id = inID;
        implementationAdvertisement = (ModuleImplAdv) inAdvertisement;
    }

    public int startApp(String[] arg0) {

        try {
            biDiPipeServer = new JxtaServerPipe(peerGroup, getPipeAdvertisement(), 5000);

            Thread thread = new Thread(this);
            thread.start();

            Tools.PopInformationMessage(Name, "Start Successful");

            return START_OK;

        } catch (IOException Ex) {
            Tools.PopErrorMessage(Name, Ex.toString());
            Tools.PopInformationMessage(Name, "Start Unsuccessful");

            return START_DISABLED;
        }

    }

    public void run() {

        while (biDiPipeServer != null) {

            try {

                JxtaBiDiPipe biDiPipe = this.biDiPipeServer.accept();

                if (biDiPipe != null) {

                    // Processing customers
                    Thread thread = new Thread(new CustomerHandler(biDiPipe));
                    thread.start();

                }

            } catch (SocketTimeoutException Ex) {

                // We don't care if we get a timeout after 5 seconds
                // We try to accept a connection again

            } catch (IOException Ex) {
                Tools.PopErrorMessage(Name, Ex.toString());
            }

        }

    }

    public void stopApp() {

        // Closing bidipipe server
        if (biDiPipeServer != null) {

            try {

                biDiPipeServer.close();
                biDiPipeServer = null;
                Tools.PopInformationMessage(Name, "Stop Successful");

            } catch (IOException Ex) {

                Tools.PopErrorMessage(Name, Ex.toString());

            }

        }

    }

    private static class CustomerHandler implements Runnable, PipeMsgListener {

        private JxtaBiDiPipe jxtaBiDiPipe = null;

        CustomerHandler(JxtaBiDiPipe inPipe) {
            jxtaBiDiPipe = inPipe;
            jxtaBiDiPipe.setMessageListener(this);
        }

        public static final int computeHoroscopeHash(String inString) {

            int result = 0;

            if (inString != null) {
                for (int i = 0; i < inString.length(); i++) {
                    result = result + (int) inString.charAt(i);
                }
            }

            // Returning result
            return (result % 3);

        }

        public void pipeMsgEvent(PipeMsgEvent event) {

            try {
                long predictionHash = System.currentTimeMillis();

                // Retrieve the message
                Message message = event.getMessage();

                MessageElement messageElement = message.getMessageElement(NameSpace, CustomerNameElement);
                predictionHash = predictionHash + computeHoroscopeHash(messageElement.toString());

                messageElement = message.getMessageElement(NameSpace, BirthDateElement);
                predictionHash = predictionHash + computeHoroscopeHash(messageElement.toString());

                messageElement = message.getMessageElement(NameSpace, BirthLocationElement);
                predictionHash = predictionHash + computeHoroscopeHash(messageElement.toString());

                predictionHash = predictionHash % 3;

                String prediction = "";

                switch ((int) predictionHash) {
                    case 0:
                        prediction = "You will be rich!";
                        break;

                    case 1:
                        prediction = "You will be famous!";
                        break;

                    default:
                        prediction = "You need to make more sacrifices to the Gods!";
                }

                // Sending answer
                message = new Message();
                StringMessageElement stringMessageElement = new StringMessageElement(PredictionElement, prediction, null);
                message.addMessageElement(NameSpace, stringMessageElement);

                jxtaBiDiPipe.sendMessage(message);

                // Closing the connection
                jxtaBiDiPipe.close();

            } catch (IOException Ex) {
                Tools.PopErrorMessage(Name, Ex.toString());
            }

        }

        public void run() {
            // The pipeMsgEvent will be called when necessary
        }

    }

}
