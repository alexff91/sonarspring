package com.uzielasto.app.prot;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 11.01.13
 * Time: 10:11
 * All rights recieved.(c)
 */

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VideoCapture extends JFrame {

    Player player;

    VideoCapture() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                player.stop();
                player.deallocate();
                player.close();
                System.exit(0);
            }
        });
        setSize(640, 480);
        JPanel panel = (JPanel) getContentPane();
        panel.setLayout(new BorderLayout());
        String mediaFile = "usbvideo.inf:Microsoft.NTamd64:USBVideo:6.1.7601.17514:usb\\class_0e";
        try {
            MediaLocator mlr = new MediaLocator(mediaFile);
            player = Manager.createRealizedPlayer(mlr);
            player.setRate(100);
            if (player.getVisualComponent() != null) {
                panel.add("Center", player.getVisualComponent());
            }
            if (player.getControlPanelComponent() != null) {
                panel.add("South", player.getControlPanelComponent());
            }
        } catch (Exception e) {
            System.err.println("Got exception " + e);
        }
    }

    public static void main(String[] args) {
        VideoCapture m = new VideoCapture();
        m.setVisible(true);
    }
}
