package com.uzielasto.app;


import com.uzielasto.app.scala.Quantizer;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 18.11.12
 * Time: 0:38
 * All rights recieved.(c)
 */
public class AppletMaker {
    private static VideoImage mScreen = null;
    public static BufferedImage old;
    public static BufferedImage tics;
    public static int counter = 0;
    public static long milsec = 50;
    public static int milsBetwFr = 1;
    static Thread t1;
    static Thread t2;
    public static BufferedImage screenshot;
    public static String inputVideo = "1.wmv";


    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            openJavaWindow();
            // mScreen.setSize(1600, 1600);
            t1 = runAsynchronouslyMethod1(new AppletMaker());
            t2 = runAsynchronouslyMethod2(new AppletMaker());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Thread runAsynchronouslyMethod1(final AppletMaker obj) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    obj.createGUI();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        t.start();
        return t;

    }

    static Thread runAsynchronouslyMethod2(final AppletMaker obj) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                obj.runSampling();
            }
        });
        t.start();
        return t;

    }

    public static void runSampling() {
        while (true) {
            try {

                IMediaReader reader = ToolFactory.makeReader(inputVideo);
                reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
                MediaListenerAdapter adapter = new MediaListenerAdapter() {
                    @Override
                    public void onVideoPicture(IVideoPictureEvent event) {
                        try {
                            Thread.sleep(milsec);

                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        BufferedImage img = (BufferedImage) event.getImage();

                        updateJavaWindow(img);

                    }
                };
                reader.addListener(adapter);

                while (reader.readPacket() == null)

                    do {
                    } while (false);
                //  closeJavaWindow();    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The window we'll draw the video on.
     */
    public static void createGUI() throws HeadlessException, NullPointerException, IOException {


        mScreen.filepath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fd = new JFileChooser();
                fd.showOpenDialog(new JFrame());

                File inp = fd.getSelectedFile();
                if (inp != null)
                    inputVideo = inp.getAbsolutePath();
                ;
                System.out.println(inputVideo);


            }
        });


        mScreen.jstart.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void actionPerformed(ActionEvent e) {
                milsec = 50;
                //noinspection deprecation
                t2.resume();
            }
        });
        mScreen.faster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (milsec >= 20) milsec -= 20;

            }
        });
        mScreen.slower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                milsec += 50;

            }
        });
        mScreen.slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                    milsBetwFr = (int)source.getValue();

            }
        });
        mScreen.exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mScreen.jstop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t2.suspend();
            }
        });
        mScreen.screen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File dir = new File("screens");
                    dir.mkdir();
                    ImageIO.write(screenshot, "png", new File("screens\\" + System.currentTimeMillis() + "screen.png"));
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });


    }

    private static void updateJavaWindow(BufferedImage javaImage) {

        Dimension tool = mScreen.getToolkit().getScreenSize();
        if (tool.width < javaImage.getWidth()*2) {
            java.awt.Image scaledImage = javaImage.getScaledInstance(tool.width, (int) (tool.height / 1.5), 1);
            // convert the scaled image back to a buffered image
            BufferedImage img = new BufferedImage(tool.width/2, (int) (tool.height / 1.5), BufferedImage.TYPE_3BYTE_BGR);
            img.getGraphics().drawImage(scaledImage, 0, 0, null);
            javaImage = img;
            }
        BufferedImage result = new BufferedImage(javaImage.getWidth() * 2, javaImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage img1 = result.getSubimage(0, 0, javaImage.getWidth(), javaImage.getHeight());
        int acc = 0;
        img1 = javaImage;
        BufferedImage img2 = result.getSubimage(javaImage.getWidth(), 0, javaImage.getWidth(), javaImage.getHeight());
        img2 = deepCopy(javaImage);
        counter++;
        mScreen.setPreferredSize(new Dimension(javaImage.getWidth()*2,javaImage.getHeight()));

        try {

            if (mScreen.sumxy > 0 && mScreen.sumwh > 0) {
                if (acc != mScreen.sumxy) {
                    BufferedImage im1 = deepCopy(img1);
                    BufferedImage im2 = deepCopy(img2);
                    mScreen.image = im1;
                    if (counter > 0 && mScreen.x < javaImage.getWidth()) {
                        img2 = Quantizer.quantoImageTest(tics, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height);

                       // java.util.List<java.util.List<Integer>> xyaxis = AppGo.featureMatcher(tics, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height);
//                        xAxis.addAll(xyaxis.get(0));
//                        yAxis.addAll(xyaxis.get(1));
                    }
                }


            }
        } catch (Exception e) {
        }
        acc = mScreen.sumxy;
        old = (BufferedImage) img1;
        if (counter % milsBetwFr == 0) {

            tics = old;

        }

        Graphics g = result.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img2.getWidth(), 0, null);
        g.dispose();
       mScreen.setImage(result);
       screenshot = result;

    }

    /**
     * Opens a Swing window on screen.
     */
    private static VideoImage openJavaWindow() throws IOException {
        JFrame.setDefaultLookAndFeelDecorated(true);
        mScreen = new VideoImage();

        return mScreen;
    }
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    /**
     * Forces the swing thread to terminate; I'm sure there is a right
     * way to do this in swing, but this works too.
     */
    private static void closeJavaWindow() {
        System.exit(0);
    }
}
