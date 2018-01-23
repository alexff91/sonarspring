package com.idas.app;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 06.03.13
 * Time: 15:38
 * All rights recieved.(c)
 */

import javax.media.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {

    Player player;

    Main() {
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
        String mediaFile = "vfw:Microsoft WDM Image Capture (Win32):0";
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

    public static void main(String[] strArgs){
    Main main = new Main();
    main.show();

    }
}
