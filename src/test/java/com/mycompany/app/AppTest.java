package com.uzielasto.app;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple AppGo.
 */
public class AppTest

{
    public void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\Aleksandr\\Desktop\\uzi_proj\\my-app\\temp\\pictures\\160000test.png"));
        BufferedImage image2 = ImageIO.read(new File("C:\\Users\\Aleksandr\\Desktop\\uzi_proj\\my-app\\temp\\pictures\\200000test.png"));
        ImageIO.write(image2, "png", new File("temp\\test.png"));
//        FloatArray2D src = mpi.cbg.fly.ImageArrayConverter.ImageToFloatArray2D(image);
//        Vector<Feature> features = mpi.cbg.fly.SIFT.getFeatures(image);
//        Graphics2D g = image.createGraphics();
//        for (Feature f : features) {
//            drawFeature(g, f.location[0], f.location[1], f.scale, f.orientation);
//        }
        BufferedImage buffGraph = new BufferedImage(500,500, BufferedImage.TYPE_INT_RGB);

        ImageIO.write(image, "PNG", new File("yourImageName.PNG"));
    }


    public void drawFeature(Graphics c, float x, float y, double scale,
                            double orientation) {


        // line too small...
        scale *= 6.;

        double sin = Math.sin(orientation);
        double cos = Math.cos(orientation);
        c.setPaintMode();

        c.drawLine((int) x, (int) y, (int) (x - (sin - cos) * scale),
                (int) (y + (sin + cos) * scale));


        c.drawOval((int) x, (int) y, 3, 3);
    }
}
