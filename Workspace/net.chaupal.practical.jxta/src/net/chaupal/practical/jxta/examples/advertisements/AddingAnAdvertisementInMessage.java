/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.advertisements;

import net.chaupal.practical.jxta.tools.Tools;
import java.io.IOException;

import org.junit.Test;

import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.TextDocumentMessageElement;

public class AddingAnAdvertisementInMessage {

    public static final String Name = "Example_440";
  
    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {
            
        // Creating a customized advertisement
        CustomizedAdvertisement myAdvertisement = new CustomizedAdvertisement();
        myAdvertisement.setName("John");
        myAdvertisement.setAge(33);

        // Creating the message
        Message myMessage = new Message();

        // Creating the message element and adding it
        TextDocumentMessageElement myTextDocumentMessageElement = new TextDocumentMessageElement(
                "CUSTOMIZED_ADVERTISEMENT", 
                (XMLDocument<?>) myAdvertisement.getDocument(MimeMediaType.XMLUTF8),
                null);

        myMessage.addMessageElement("CUSTOMIZED_ADVERTISEMENT",myTextDocumentMessageElement);

        // Retrieving the advertisement from the message
        MessageElement myMessageElement = myMessage.getMessageElement("CUSTOMIZED_ADVERTISEMENT","CUSTOMIZED_ADVERTISEMENT");

        try {

            XMLDocument<?> theDocument = (XMLDocument<?>) StructuredDocumentFactory.newStructuredDocument(
                myMessageElement.getMimeType(),
                myMessageElement.getStream());

            CustomizedAdvertisement myCustomizedAdvertisement =
                    new CustomizedAdvertisement(theDocument.getRoot());

            // Displaying advertisement
            Tools.PopInformationMessage(Name, myCustomizedAdvertisement.toString());

        } catch (IOException Ex) {

            // Thrown when message element cannot be read.
            Tools.PopErrorMessage(Name, Ex.toString());

        } catch (IllegalArgumentException Ex) {

            // Thrown when the document or advertisement has an invalid
            // structure (illegal values or missing tags...)
            Tools.PopErrorMessage(Name, Ex.toString());

        }

    }

}
