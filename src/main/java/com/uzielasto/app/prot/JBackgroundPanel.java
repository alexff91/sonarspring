package com.uzielasto.app.prot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 26.11.12
 * Time: 14:02
 * All rights recieved.(c)
 */
public class JBackgroundPanel extends JPanel {
    private BufferedImage img;

    public JBackgroundPanel() {
        // load the background image
        try {
            img = ImageIO.read(new File("soft.jpg"));
            this.setSize(30,100);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    // paint the background image and scale it to fill the entire space
    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
}
}