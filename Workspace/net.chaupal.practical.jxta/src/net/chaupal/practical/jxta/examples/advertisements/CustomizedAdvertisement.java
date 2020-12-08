/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.advertisements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.Element;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;

public class CustomizedAdvertisement extends Advertisement {
    
    public static final String Name = "Example_500";

    // Advertisement elements, tags and indexables
    public final static String AdvertisementType = "jxta:CustomizedAdvertisement";
    
    private ID advertisementID = ID.nullID;

    private String theName = "";
    private int theAge = -1;
    
    private final static String IDTag = "MyIDTag";
    private final static String NameTag = "MyNameTag";
    private final static String AgeTag = "MyAgeTag";
    
    private final static String[] IndexableFields = { IDTag, NameTag };

    public CustomizedAdvertisement() {
        
        // Accepting default values

    }

    public CustomizedAdvertisement(Element<?> root) {
        
        // Retrieving the elements
        TextElement<?> myTextElement = (TextElement<?>) root;

        Enumeration<?> theElements = myTextElement.getChildren();
        
        while (theElements.hasMoreElements()) {
            
            TextElement<?> theElement = (TextElement<?>) theElements.nextElement();
            
            ProcessElement(theElement);
            
        }        

    }
    
    public void ProcessElement(TextElement<?> theElement) {
        
        String theElementName = theElement.getName();
        String theTextValue = theElement.getTextValue();
        
        if (theElementName.compareTo(IDTag)==0) {
            
            try {
                
                URI readID = new URI(theTextValue);
                advertisementID = IDFactory.fromURI(readID);
                return;
                
            } catch (URISyntaxException ex) {
                
                // Issue with ID format
                ex.printStackTrace();
                
            } catch (ClassCastException ex) {
                
                // Issue with ID type
                ex.printStackTrace();
                
            }
            
        }
        
        if (theElementName.compareTo(NameTag)==0) {
            
            theName = theTextValue;
            return;
            
        }
        
        if (theElementName.compareTo(AgeTag)==0) {
            
            theAge = Integer.parseInt(theTextValue);
            return;
            
        }
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public Document getDocument(MimeMediaType TheMimeMediaType) {
        
        // Creating document
        StructuredDocument theResult = StructuredDocumentFactory.newStructuredDocument(
                TheMimeMediaType, AdvertisementType);
        
        // Adding elements
        Element<?> myTempElement;
        
        myTempElement = theResult.createElement(NameTag, theName);
        theResult.appendChild(myTempElement);
        
        myTempElement = theResult.createElement(AgeTag, Integer.toString(theAge));
        theResult.appendChild(myTempElement);
        
        return theResult;
        
    }

    public void setID(ID TheID) {
        advertisementID = TheID;
    }

    @Override
    public ID getID() {
        return advertisementID;
    }

    @Override
    public String[] getIndexFields() {
        return IndexableFields;
    }

    public void setName(String InName) {
        theName = InName;
    }

    public void setAge(int InAge) {
        theAge = InAge;
    }
    
    public String getName() {
        return theName;
    }

    public int getAge() {
        return theAge;
    }
    
    @Override
    public CustomizedAdvertisement clone() throws CloneNotSupportedException {
        
        CustomizedAdvertisement Result = (CustomizedAdvertisement) super.clone();

        Result.advertisementID = this.advertisementID;
        Result.theName = this.theName;
        Result.theAge = this.theAge;
        
        return Result;
        
    }
    
    @Override
    public String getAdvType() {
        
        return CustomizedAdvertisement.class.getName();
        
    }
    
    public static String getAdvertisementType() {
        return AdvertisementType;
    }    
    
    public static class Instantiator implements AdvertisementFactory.Instantiator {

        public String getAdvertisementType() {
            return CustomizedAdvertisement.getAdvertisementType();
        }

        public Advertisement newInstance() {
            return new CustomizedAdvertisement();
        }

        public Advertisement newInstance(net.jxta.document.Element<?> root) {
            return new CustomizedAdvertisement(root);
        }
        
    }

}
