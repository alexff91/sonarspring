package com.uzielasto.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 31.01.13
 * Time: 16:30
 * All rights recieved.(c)
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;

    public ImagePanel(String s) {
        try {
            image = ImageIO.read(new File(s));
        } catch (IOException ex) {
           System.out.print(ex.getMessage());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

}
