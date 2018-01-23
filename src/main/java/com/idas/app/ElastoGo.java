package com.idas.app;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 24.11.12
 * Time: 18:16
 * All rights recieved.(c)
 */

//import com.xuggle.mediatool.IMediaReader;
//import com.xuggle.mediatool.MediaListenerAdapter;
//import com.xuggle.mediatool.ToolFactory;
//import com.xuggle.mediatool.event.IVideoPictureEvent;
//import com.xuggle.xuggler.*;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.enums.Exposure;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Takes a FFMPEG device driver name (ex: "video4linux2"), and a device name (ex: /dev/video0), and
 * displays video from that device.  For example, a web camera. <p> For example, to play the default
 * camera on these operating systems: <ul>
 * <li>Microsoft Windows:<pre>java -cp %XUGGLE_HOME%\share\java\jars\xuggle-xuggler.jar
 * com.xuggle.xuggler.demos.DisplayWebcamVideo vfwcap 0</pre></li>
 * <li>Linux:<pre>java -cp $XUGGLE_HOME/share/java/jars/xuggle-xuggler.jar
 * com.xuggle.xuggler.demos.DisplayWebcamVideo video4linux2 /dev/video0</pre></li>
 * </ul> </p>
 */
public class ElastoGo {
    //old image
    public static BufferedImage old;

    //take image after some ms
    public static BufferedImage tics;

    //count that you take image
    public static int counter = 0;

    //delay
    public static long milsec = 0;

    //thread for gui
    static Thread t1;

    //thread for getting source
    static Thread t2;

    //thread for computing
    static Thread t3;

    static boolean deviceOrMovie = false;

    public static BufferedImage screenshot;

    public static String inputVideo = "";

    public static int milsBetwFr = 1;

    public static int sizeOfSqare = 2;

    public static boolean nuller = true;

    public static double sizeH = 1;

    public static double sizeW = 1;

    public static String deviceName = "0";

    public static String driverName = "vfwcap";

    static LinkedList<BufferedImage> framesColored = new LinkedList<BufferedImage>();

    static LinkedList<BufferedImage> additionFramesColored = new LinkedList<BufferedImage>();

    //colormap of selected region
    public static int[] colorMapOfRegion;

    static BufferedImage javaImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
    static RPiCamera piCamera;

    /**
     * The window we'll draw the video on.
     */
    private static VideoImage mScreen = null;

    private static Logger log = Logger.getLogger(ElastoGo.class.getName());
    // private static DSCapture graph;

    public static void main(String[] args) {

        try {
            piCamera = new RPiCamera("/home/pi/Pictures");
        } catch (FailedToRunRaspistillException e) {
            e.printStackTrace();
        }


        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            openJavaWindow();
            t1 = runAsynchronouslyMethod1(new ElastoGo());
            t2 = runAsynchronouslyMethod2(new ElastoGo());
            t3 = runAsynchronouslyMethod3(new ElastoGo());
        } catch (Exception e) {
            JOptionPane
                    .showMessageDialog(mScreen, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable throwable) {
            log.log(Level.SEVERE, "Exception: ",
                    throwable);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static Thread runAsynchronouslyMethod1(final ElastoGo obj) {
        Thread t = new Thread(() -> {
            try {
                createGUI();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        });
        t.start();
        return t;
    }

    static Thread runAsynchronouslyMethod3(final ElastoGo obj) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    updateLoop();
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        t.start();
        return t;
    }

    /**
     * The window we'll draw the video on.
     */
    public static void createGUI()
            throws HeadlessException, NullPointerException, IOException {

        VideoImage.filepath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //                JFileChooser fd = new JFileChooser();
                //                fd.showOpenDialog(new JFrame());
                //              t2.suspend();
                //                File inp = fd.getSelectedFile();
                t2.suspend();
                JFileChooser fd = new JFileChooser();
                fd.showOpenDialog(new JFrame());

                File inp = fd.getSelectedFile();

                if (inp != null) {
                    inputVideo = inp.getAbsolutePath();
                }
                nuller = false;

                t2.resume();
                //System.out.println(inputVideo);

            }
        });

        VideoImage.jstart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                milsec = 50;
                t2.resume();
                t3.resume();
                mScreen.repaint();
            }
        });
        VideoImage.faster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (milsec >= 20) {
                    milsec -= 20;
                }
                mScreen.repaint();
            }
        });
        VideoImage.zoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sizeH *= 1.05;
                sizeW *= 1.05;
                framesColored.clear();
                additionFramesColored.clear();
                mScreen.repaint();
            }
        });
        VideoImage.zoomOut.addActionListener(e -> {
            sizeH *= 0.95;
            sizeW *= 0.95;
            framesColored.clear();
            additionFramesColored.clear();
            mScreen.repaint();
        });
        VideoImage.selectDev.addActionListener(
                new ActionListener() {
                    @Override                                                        //WTF
                    public void actionPerformed(ActionEvent e) {

                        t2.suspend();
                        t3.suspend();
                        nuller = false;

                        if (deviceOrMovie) {
                            deviceOrMovie = false;

                            VideoImage.selectDev.setText("Select device");
                        } else {
                            deviceOrMovie = true;

                            VideoImage.selectDev.setText("Select video");
                        }
                        t2.resume();
                        t3.resume();
                    }
                }
        );
        VideoImage.slower.addActionListener(e -> milsec += 50);
        VideoImage.exit.addActionListener(e -> {
            nuller = true;
            System.exit(0);
        });
        VideoImage.jstop.addActionListener(e -> {
            t2.suspend();
            t3.suspend();
        });
        VideoImage.screen.addActionListener(e -> {
            try {
                File dir = new File("screens");
                dir.mkdir();
                ImageIO.write(screenshot, "png",
                        new File("screens\\" + System.currentTimeMillis() + "screen.png"));
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        });
        VideoImage.slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                milsBetwFr = source.getValue();
                mScreen.repaint();
            }
        });
        VideoImage.octavesSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();

                ImageAdjaster.octaves = source.getValue();
            }
        });
        VideoImage.treshSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();

                ImageAdjaster.threshold = source.getValue();
            }
        });
        VideoImage.pixelSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                sizeOfSqare = source.getValue();
                mScreen.repaint();
            }
        });
    }

    static Thread runAsynchronouslyMethod2(final ElastoGo obj) {

        Thread t = new Thread(() -> {

            try {
                obj.tain();
            } catch (Exception e) {
                //                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        });
        try {
            t.start();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return t;
    }

    private void tain() {
        while (true) {
            try {

                framesColored.clear();
                additionFramesColored.clear();
                if (deviceOrMovie) {
                    nuller = true;

                    // devices, unlike most files, need to have parameters set in order
                    // for Xuggler to know how to configure them, for a webcam, these
                    // parameters make sense

                    piCamera.setWidth(500).setHeight(500) // Set Camera to produce 500x500 images.
                            .setBrightness(75)                // Adjust Camera's brightness setting.
                            .setExposure(Exposure.AUTO)       // Set Camera's exposure.
                            .setTimeout(2)                    // Set Camera's timeout.
                            .setAddRawBayer(true);            // Add Raw Bayer data to image files created by Camera.


                    while (nuller) {
                        try {
                            Thread.sleep(300);
                            javaImage = piCamera.takeBufferedStill();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mScreen, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void updateJavaWindow() {
        if (javaImage.getHeight() != 1) {

            //first - convert to gray
            javaImage = getGrayScale(javaImage);
            //ok - comeback colormap
            //do i need really in hysto eq?

            javaImage = getRGBScale(javaImage);
            //rescale image if it biig
            Dimension tool = mScreen.getToolkit().getScreenSize();
            int wt = tool.width;
            int ht = tool.height;

            if (ImageAdjaster.quantoIm) {
                if (framesColored.size() > 30) {
                    framesColored = new LinkedList<BufferedImage>(framesColored.subList(15, 30));
                }
                //if (framesColored.size() > 30) framesColored.clear();
                if (counter % 5 == 0) {
                    additionFramesColored.add(tics);
                }
                if (additionFramesColored.size() > 50) {
                    additionFramesColored = new LinkedList<BufferedImage>(
                            additionFramesColored.subList(20, 40));
                }
                //framesColored.addAll(additionFramesColored);
                framesColored.add(tics);
            }
            BufferedImage result = new BufferedImage(javaImage.getWidth() * 2, javaImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            //left image
            BufferedImage leftImg = result.getSubimage(0, 0, javaImage.getWidth(), javaImage.getHeight());
            int acc = 0;
            leftImg = javaImage;
            //right image
            BufferedImage rightImg = result
                    .getSubimage(javaImage.getWidth(), 0, javaImage.getWidth(), javaImage.getHeight());
            rightImg = javaImage;
            counter++;
            mScreen.setPreferredSize(new Dimension(javaImage.getWidth() * 2, javaImage.getHeight()));
            //if (counter % 2 == 0)

            // System.out.println(framesColored.size());
            try {

                if (VideoImage.sumxy > 0 && VideoImage.sumwh > 0) {
                    if (acc != VideoImage.sumxy) {
                        BufferedImage im1 = leftImg;
                        mScreen.image = im1;
                        if (counter > 0 && VideoImage.x < javaImage.getWidth()) {
                            //color it!

                            //rightImg = Quantizer.quantoImageTest(tics, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height);

                            // rightImg = Quantizer.quantoImageAveraged(framesColored, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height, sizeOfSqare);
                            //rightImg = Quantizer.applySurf(rightImg,false);

                            if (counter % milsBetwFr * 10 == 0
                                    || colorMapOfRegion.length != VideoImage.width * VideoImage.height) {
                                colorMapOfRegion = new int[VideoImage.width * VideoImage.height];

                                rightImg = ImageAdjaster
                                        .surfImage(mScreen, framesColored, sizeOfSqare, im1, VideoImage.x, VideoImage.y,
                                                VideoImage.width, VideoImage.height, rightImg);
                            } else {
                                rightImg
                                        .setRGB(VideoImage.x, VideoImage.y, VideoImage.width, VideoImage.height, colorMapOfRegion,
                                                0, VideoImage.width);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception: ", e);
            }
            old = leftImg;
            if (counter % milsBetwFr * 10 == 0) {
                tics = old;
            }

            Graphics g = result.getGraphics();
            g.drawImage(leftImg, 0, 0, null);
            // drawing b2 where b1 ended
            g.drawImage(rightImg, rightImg.getWidth(), 0, null);
            g.dispose();

            mScreen.setImage(result);
            mScreen.tab1Picture.setImage(leftImg);
            mScreen.tab2Picture.setImage(rightImg);

            screenshot = result;
        }
    }

    /**
     * Opens a Swing window on screen.
     */
    private static VideoImage openJavaWindow() {
        mScreen = new VideoImage();
        return mScreen;
    }

    public static BufferedImage getGrayScale(BufferedImage inputImage) {
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        return img;
    }

    public static BufferedImage getRGBScale(BufferedImage inputImage) {
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        return img;
    }

    /**
     * Forces the swing thread to terminate; I'm sure there is a right way to do this in swing, but
     * this works too.
     */
    private static void closeJavaWindow() {
        System.exit(0);
    }

    private static BufferedImage updateSize(BufferedImage image) {
        if (sizeH != 1) {
            java.awt.Image scaledImage = image
                    .getScaledInstance((int) (image.getWidth() * sizeW), (int) (image.getHeight() * sizeH),
                            1);
            // convert the scaled image back to a buffered image
            BufferedImage img = new BufferedImage((int) (image.getWidth() * sizeW),
                    (int) (image.getHeight() * sizeH), BufferedImage.TYPE_3BYTE_BGR);
            img.getGraphics().drawImage(scaledImage, 0, 0, null);
            return img;
        } else {
            return image;
        }
    }

    private static void updateLoop() {
        while (true) {
            try {
                updateJavaWindow();
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }
}