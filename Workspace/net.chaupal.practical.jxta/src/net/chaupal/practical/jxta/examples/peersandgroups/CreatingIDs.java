/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * Based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.peersandgroups;

import org.junit.Test;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;

public class CreatingIDs {

    public static final String Name = "Example 200";
    
    @Test
    public void test() {
    	main(null);
    }

    public static void main(String[] args) {

        // Creating a peer group ID and a sub-peer group ID
        PeerGroupID myPeerGroupID_A = IDFactory.newPeerGroupID();
        PeerGroupID myPeerGroupID_B = IDFactory.newPeerGroupID(myPeerGroupID_A);
        PeerGroupID myPeerGroupID_C = IDFactory.newPeerGroupID(myPeerGroupID_B);
        
        // Creating peer IDs
        PeerID myPeer_A = IDFactory.newPeerID(myPeerGroupID_A);
        PeerID myPeer_B = IDFactory.newPeerID(myPeerGroupID_B);
        PeerID myPeer_C = IDFactory.newPeerID(myPeerGroupID_C);
        
        byte[] mySeed = { 0, 1, 2, 3, 4, 5, 6 };
        PeerID myPeer_Seed = IDFactory.newPeerID(myPeerGroupID_A, mySeed);
        
        // Creating a Pipe ID
        PipeID myPipe_A = IDFactory.newPipeID(myPeerGroupID_A);
        
        // Displaying the IDs
        System.out.println("Peer Group A        : " + myPeerGroupID_A.toString());
        System.out.println("Peer of A           : " + myPeer_A.toString());
        System.out.println("Pipe of A           : " + myPipe_A.toString());
        
        System.out.println("Peer of A with seed : " + myPeer_Seed.toString());
        
        System.out.println("Peer Group B of A   : " + myPeerGroupID_B.toString());
        System.out.println("Peer of B           : " + myPeer_B.toString());
        
        System.out.println("Peer Group C of B   : " + myPeerGroupID_C.toString());
        System.out.println("Peer of C           : " + myPeer_C.toString());

    }
        
}
