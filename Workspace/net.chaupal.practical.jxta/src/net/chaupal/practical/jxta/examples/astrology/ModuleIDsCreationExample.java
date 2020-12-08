/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *  
 */

package net.chaupal.practical.jxta.examples.astrology;

import org.junit.Test;

import net.jxta.id.IDFactory;
import net.jxta.impl.id.UUID.ModuleClassID;
import net.jxta.peergroup.core.ModuleSpecID;

public class ModuleIDsCreationExample {
    
    public static final String Name = "Example_700";
    
    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {
        
        // Creating a new random module class ID
        ModuleClassID moduleClassID = (ModuleClassID) IDFactory.newModuleClassID();
        
        // Creating a new random module specification ID
        ModuleSpecID moduleSpecID = IDFactory.newModuleSpecID(moduleClassID);
        
        // Printing IDs
        System.out.println(moduleClassID.toURI().toString() + "\n");
        System.out.println(moduleSpecID.toURI().toString() + "\n");

    }
        
}
