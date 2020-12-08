/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * Based on work from DawningStreams, Inc. 2010
 *
 */
package net.chaupal.practical.jxta.examples.monitoring;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.jxta.platform.NetworkManager;

/**
 *
 */
public class DelayedJxtaNetworkStopper implements Runnable {

    // Attributes

    private final NetworkManager TheNM;
    private String TheMsg = "";
    private String TheTitle = "";

    public DelayedJxtaNetworkStopper(NetworkManager inNM, String inMsg, String inTitle) {

        TheNM = inNM;
        TheMsg = inMsg;
        TheTitle = inTitle;

    }

    public void run() {

        // Creating the dialog box
        JOptionPane pane = new JOptionPane(TheMsg, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(TheTitle);

        // Displaying the message in a non-modal way
        dialog.setModal(false);
        dialog.setVisible(true);

        // Waiting for user to click ok
        Object click = null;

        do {

            click = pane.getValue();

            if ( JOptionPane.UNINITIALIZED_VALUE == click )
                try { Thread.sleep(200); } catch (InterruptedException ex) {}

        } while ( click == JOptionPane.UNINITIALIZED_VALUE );

        // Closing dialog
        dialog.dispose();

        // Stopping the network
        TheNM.stopNetwork();

    }

}
