/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.advertisements;

import org.junit.Test;

import net.chaupal.practical.jxta.tools.Tools;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;

public class RetrievingMessageElements {
    
    public static final String Name = "Example_420";
   
    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {

        // Creating an empty message
        Message myMessage = new Message();
        
        // Creating string element
        StringMessageElement myNameMessageElement = new StringMessageElement(
                "NameElement", "John Arthur", null);
        
        // Creating int element
        StringMessageElement myAddressMessageElement = new StringMessageElement(
                "AddressElement", "42nd Street", null);
                
        // Creating long element
        StringMessageElement myOtherMessageElement = new StringMessageElement(
                "OtherElement", "Enjoys pizzas", null);

        // Adding string message elements
        myMessage.addMessageElement("MyNameSpace", myNameMessageElement);
        myMessage.addMessageElement("MyNameSpace", myAddressMessageElement);
        myMessage.addMessageElement("MyNameSpace", myOtherMessageElement);
        
        // Displaying message
        Tools.DisplayMessageContent(Name, myMessage);
        
        // Retrieving message element
        Tools.PopInformationMessage(Name, "Retrieving message element:\n\n" 
                + myMessage.getMessageElement("MyNameSpace", "AddressElement").toString());
        
    }

}
