package com.uzielasto.app.prot;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 10.11.12
 * Time: 18:55
 * All rights recieved.(c)
 */
public class AppLauncher {

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\Aleksandr\\Desktop\\uzi_proj\\my-app\\temp\\pictures\\160000test.png"));
        BufferedImage image2 = ImageIO.read(new File("C:\\Users\\Aleksandr\\Desktop\\uzi_proj\\my-app\\temp\\pictures\\200000test.png"));
        //180, 70, 50, 40
        int h = image.getHeight();
        int w = image.getWidth();
        System.out.println(h + "    " + w);
//        Vector<Feature> features = mpi.cbg.fly.SIFT.getFeatures(image.getSubimage(171, 83, 490, 465));
//        Vector<Feature> features2 = mpi.cbg.fly.SIFT.getFeatures(image2.getSubimage(171, 83, 490, 465));
//        Graphics2D g = image.getSubimage(171, 83, 490, 465).createGraphics();
//        for (Feature f : features) {
//            drawFeature(g, f.location[0], f.location[1], f.scale, f.orientation);
//        }
//        Graphics2D g2 = image2.getSubimage(171, 83, 490, 465).createGraphics();
//        for (Feature f : features2) {
//            drawFeature(g2, f.location[0], f.location[1], f.scale, f.orientation);
//        }
//        BufferedImage buffGraph = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
//        Vector<PointMatch> pm = SIFT.createMatches(features.subList(0, features.size()), features2.subList(0, features2.size()), 10.0f, new mpi.cbg.fly.TRModel2D(), 20f);
//        ImageIO.write(image, "PNG", new File("yourImageName.PNG"));
//        for (PointMatch f : pm) {
//            drawPointMatching(g2, f);
//        }
//        float scale = 1.0f;
//        int fdsize = 4;
//        for (Feature f : features) {
//            //System.out.println( f.location[ 0 ] + " " + f.location[ 1 ] + " " + f.scale + " " + f.orientation );
//            drawSquare(g2, new double[]{f.location[0] / scale, f.location[1] / scale},
//                    fdsize * 4.0 * (double) f.scale / scale, (double) f.orientation);
//        }
        final IMediaReader reader = ToolFactory.makeReader("test.mp4");
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        JFrame frame = new JFrame("RectangleDraw");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        // frame.getContentPane().add((new RectangleDraw()), BorderLayout.WEST);
        //final RectangleDraw mn = new RectangleDraw();

        frame.getContentPane().add(new JBackgroundPanel(), BorderLayout.CENTER);
      //  frame.getContentPane().add(mn, BorderLayout.EAST);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        MediaListenerAdapter adapter = new MediaListenerAdapter() {
            @Override
            public void onVideoPicture(IVideoPictureEvent event) {
            //    mn.setImage((BufferedImage) event.getImage());
            }
        };
        reader.addListener(adapter);

        while (reader.readPacket() == null)
            do {
            } while (false);
    }
    //вычисление интегjhрального представления изображения

    public static void drawFeature(Graphics c, float x, float y, double scale,
                                   double orientation) {
        // line too small...
        scale *= 6.;

        double sin = Math.sin(orientation);
        double cos = Math.cos(orientation);
        c.setPaintMode();
        c.setColor(Color.GREEN);
        c.drawLine((int) x, (int) y, (int) (x - (sin - cos) * scale),
                (int) (y + (sin + cos) * scale));
        c.setColor(Color.ORANGE);

        c.drawOval((int) x, (int) y, 3, 3);
    }

    static void drawSquare(Graphics ip, double[] o, double scale, double orient) {
        scale /= 2;

        double sin = Math.sin(orient);
        double cos = Math.cos(orient);

        int[] x = new int[6];
        int[] y = new int[6];


        x[0] = (int) (o[0] + (sin - cos) * scale);
        y[0] = (int) (o[1] - (sin + cos) * scale);

        x[1] = (int) o[0];
        y[1] = (int) o[1];

        x[2] = (int) (o[0] + (sin + cos) * scale);
        y[2] = (int) (o[1] + (sin - cos) * scale);
        x[3] = (int) (o[0] - (sin - cos) * scale);
        y[3] = (int) (o[1] + (sin + cos) * scale);
        x[4] = (int) (o[0] - (sin + cos) * scale);
        y[4] = (int) (o[1] - (sin - cos) * scale);
        x[5] = x[0];
        y[5] = y[0];

        ip.drawPolygon(new Polygon(x, y, x.length));
    }

//    public static void drawPointMatching(Graphics c, PointMatch pm) {
//        Point p1 = pm.getP1();
//        Point p2 = pm.getP2();
//        int x = (int) (p1.getL())[0];
//        int y = (int) (p1.getW())[0];
//
//        c.setPaintMode();
//        c.setColor(Color.orange);
//        System.out.println(pm.getDistance());
//
//        //  c.drawOval( x, y, 3, 3);
//    }
}

