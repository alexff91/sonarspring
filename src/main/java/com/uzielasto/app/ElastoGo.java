package com.uzielasto.app;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 24.11.12
 * Time: 18:16
 * All rights recieved.(c)
 */

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.*;

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
 * Takes a FFMPEG device driver name (ex: "video4linux2"), and a device name (ex: /dev/video0), and displays video
 * from that device.  For example, a web camera.
 * <p>
 * For example, to play the default camera on these operating systems:
 * <ul>
 * <li>Microsoft Windows:<pre>java -cp %XUGGLE_HOME%\share\java\jars\xuggle-xuggler.jar com.xuggle.xuggler.demos.DisplayWebcamVideo vfwcap 0</pre></li>
 * <li>Linux:<pre>java -cp $XUGGLE_HOME/share/java/jars/xuggle-xuggler.jar com.xuggle.xuggler.demos.DisplayWebcamVideo video4linux2 /dev/video0</pre></li>
 * </ul>
 * </p>
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
    static IContainer container;
    static IStreamCoder videoCoder = null;
    static BufferedImage javaImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
    /**
     * The window we'll draw the video on.
     */
    private static VideoImage mScreen = null;
    private static Logger log = Logger.getLogger(ElastoGo.class.getName());
    // private static DSCapture graph;

    public static void main(String[] args) {

        try {


            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            openJavaWindow();
            // mScreen.setSize(1600, 1600);

            t1 = runAsynchronouslyMethod1(new ElastoGo());
            t2 = runAsynchronouslyMethod2(new ElastoGo());
            t3 = runAsynchronouslyMethod3(new ElastoGo());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mScreen, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } catch (Throwable throwable) {
            log.log(Level.SEVERE, "Exception: ", throwable);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static Thread runAsynchronouslyMethod1(final ElastoGo obj) {
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

    static Thread runAsynchronouslyMethod3(final ElastoGo obj) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    obj.updateLoop();
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
    public static void createGUI() throws HeadlessException, NullPointerException, IOException {


        mScreen.filepath.addActionListener(new ActionListener() {
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

                if (inp != null)
                    inputVideo = inp.getAbsolutePath();
                nuller = false;

                t2.resume();
                //System.out.println(inputVideo);

            }
        });


        mScreen.jstart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                milsec = 50;
                t2.resume();
                t3.resume();
                mScreen.repaint();
            }
        });
        mScreen.faster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (milsec >= 20) milsec -= 20;
                mScreen.repaint();
            }
        });
        mScreen.zoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sizeH *= 1.05;
                sizeW *= 1.05;
                framesColored.clear();
                additionFramesColored.clear();
                mScreen.repaint();
            }
        });
        mScreen.zoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sizeH *= 0.95;
                sizeW *= 0.95;
                framesColored.clear();
                additionFramesColored.clear();
                mScreen.repaint();

            }
        });
        mScreen.selectDev.addActionListener(
                new ActionListener() {
                    @Override                                                        //WTF
                    public void actionPerformed(ActionEvent e) {

                        t2.suspend();
                        t3.suspend();
                        nuller = false;

                        if (deviceOrMovie) {
                            deviceOrMovie = false;

                            mScreen.selectDev.setText("Select device");

                        } else {
                            deviceOrMovie = true;

                            mScreen.selectDev.setText("Select video");
                        }
//
//                        t2 = null;
//                        t2 = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                try {
//                                    new ElastoGo().tain();
//                                } catch (Exception e1) {
//                                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                                }
//                            }
//                        });
//                        try {
//
//                        } catch (Exception ee) {
//                            System.out.print(ee.getMessage());
//                        }
                        t2.resume();
                        t3.resume();
                    }
                }
        );
        mScreen.slower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                milsec += 50;

            }
        });
        mScreen.exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nuller = true;
                System.exit(0);
            }
        });
        mScreen.jstop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                t2.suspend();
                t3.suspend();
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
//        mScreen.listOfButtons.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JComboBox cb = (JComboBox) e.getSource();
//                String str = (String) cb.getSelectedItem();
//                if(str!=null)
//                deviceName = str;
//                else deviceName = "0";
//
//            }
//        });
        mScreen.slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                milsBetwFr = (int) source.getValue();
                mScreen.repaint();
            }
        });
        mScreen.octavesSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();

                ImageAdjaster.octaves = (int) source.getValue();
            }
        });
        mScreen.treshSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();

                ImageAdjaster.threshold = (int) source.getValue();
            }
        });
        mScreen.pixelSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                sizeOfSqare = (int) source.getValue();
                mScreen.repaint();
            }
        });
    }

    static Thread runAsynchronouslyMethod2(final ElastoGo obj) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    obj.tain();
                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        try {
            t.start();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return t;

    }

    /**
     * Takes a FFMPEG webcam driver name, and a device name, opens the
     * webcam, and displays its video in a Swing window.
     * <p>
     * Examples of device formats are:
     * </p>
     * <table border="1">
     * <thead>
     * <tr>
     * <td>OS</td>
     * <td>Driver Name</td>
     * <td>Sample Device Name</td>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td>Windows</td>
     * <td>vfwcap</td>
     * <td>0</td>
     * </tr>
     * <tr>
     * <td>Linux</td>
     * <td>video4linux2</td>
     * <td>/dev/video0</td>
     * </tr>
     * </tbody>
     * </table>
     * <p/>
     * <p>
     * Webcam support is very limited; you can't query what devices are
     * available, nor can you query what their capabilities are without
     * actually opening the device.  Sorry, but that's how FFMPEG rolls.
     * </p>
     */

    public void tain() {
        while (true) {
//            DeviceManager dm = null;
//            try {
//                dm = DeviceManager.getInstance();
//            } catch (Throwable throwable) {
//                log.log(Level.SEVERE, "Exception: ", throwable); //To change body of catch statement use File | Settings | File Templates.
//            }
//            dm.createInstance();
//            dm.scanDevices();
//            dm.dump();
//            //Print USB device description
//
//            Iterator it = dm.getDeviceList().keySet().iterator();
//            if (it.hasNext()) {
//                String devkey = (String) it.next();
//                UsbDevice usbdev = (UsbDevice) dm.getDeviceList().get(devkey);
//                log.info(usbdev.dump());
//            }
            try {

                if (videoCoder != null) {
                    videoCoder.close();
                    videoCoder = null;
                }
                if (container != null) {

                    container.close();
                    container = null;
                }

                framesColored.clear();
                additionFramesColored.clear();
                if (deviceOrMovie) {
//                    if (graph != null) {
//                        graph.stop();
//                        graph = null;
//                    }
                    nuller = true;
                    container = IContainer.make();

                    //deviceName = mScreen.listOfButtons.get;
                    //String str1 = "vfw:Logitech USB Video Camera:0";
                    //String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";

                    // Let's make sure that we can actually convert video pixel formats.
                    if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
                        throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this to work");

                    // Create a Xuggler container object
                    //IContainer container = IContainer.make();

                    // Tell Xuggler about the device format
                    IContainerFormat format = IContainerFormat.make();

                    //!!
                    if (format.setInputFormat(driverName) < 0)
                        throw new IllegalArgumentException("couldn't open device: " + driverName);

                    // devices, unlike most files, need to have parameters set in order
                    // for Xuggler to know how to configure them, for a webcam, these
                    // parameters make sense

                    IMetaData params = IMetaData.make();

                    params.setValue("framerate", "15/1");
                    params.setValue("video_size", "640x480");

                    // Open up the container
                    int retval = container.open(deviceName, IContainer.Type.READ, format,
                            false, true, params, null);
                    if (retval < 0) {
                        // This little trick converts the non friendly integer return value into
                        // a slightly more friendly object to get a human-readable error name
                        IError error = IError.make(retval);
                        throw new IllegalArgumentException("could not open file: " + deviceName + "; Error: " + error.getDescription());
                    }
                    String containerurl = container.getURL();
                    // query how many streams the call to open found
                    int numStreams = container.getNumStreams();

                    // and iterate through the streams to find the first video stream
                    int videoStreamId = -1;
                    // IStreamCoder videoCoder = null;
                    for (int i = 0; i < numStreams; i++) {
                        // Find the stream object
                        IStream stream = container.getStream(i);
                        // Get the pre-configured decoder that can decode this stream;
                        IStreamCoder coder = stream.getStreamCoder();

                        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                            videoStreamId = i;
                            videoCoder = coder;
                            break;
                        }

                    }
                    if (videoStreamId == -1)
                        throw new RuntimeException("could not find video stream in container: " + deviceName);

            /*
            * Now we have found the video stream in this file.  Let's open up our decoder so it can
            * do work.
            */
                    if (videoCoder.open() < 0)
                        throw new RuntimeException("could not open video decoder for container: " + deviceName);

                    IVideoResampler resampler = null;
                    if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                        // if this stream is not in BGR24, we're going to need to
                        // convert it.  The VideoResampler does that for us.
                        resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                                videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
                        if (resampler == null)
                            throw new RuntimeException("could not create color space resampler for: " + deviceName);
                    }
            /*
            * And once we have that, we draw a window on screen
            */
                    //openJavaWindow();

            /*
            * Now, we start walking through the container looking at each packet.
            */
                    IPacket packet = IPacket.make();

                    while (container.readNextPacket(packet) >= 0 && nuller) {

                /*
                * Now we have a packet, let's see if it belongs to our video stream
                */
//                    if (nuller&& videoCoder!= null) {
//                        videoCoder.close();
//                        videoCoder = null;
//                        container.close();
//                        container = null;
//                    }

                        if (packet.getStreamIndex() == videoStreamId) {
                            IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                                    videoCoder.getWidth(), videoCoder.getHeight());

                            int offset = 0;
                            while (offset < packet.getSize()) {
                        /*
                        * Now, we decode the video, checking for any errors.
                        *
                        */
                                int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
                                if (bytesDecoded < 0)
                                    throw new RuntimeException("got error decoding video in: " + deviceName);
                                offset += bytesDecoded;

                        /*
                        * Some decoders will consume data in a packet, but will not be able to construct
                        * a full video picture yet.  Therefore you should always check if you
                        * got a complete picture from the decoder
                        */
                                if (picture.isComplete()) {
                                    IVideoPicture newPic = picture;
                            /*
                            * If the resampler is not null, that means we didn't get the video in BGR24 format and
                            * need to convert it into BGR24 format.
                            */
                                    if (resampler != null) {
                                        // we must resample
                                        newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                                        if (resampler.resample(newPic, picture) < 0)
                                            throw new RuntimeException("could not resample video from: " + deviceName);
                                    }
                                    if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
                                        throw new RuntimeException("could not decode video as BGR 24 bit data in: " + deviceName);

                                    // Convert the BGR24 to an Java buffered image
                                    javaImage = updateSize(Utils.videoPictureToImage(newPic));
                                    // and display it on the Java Swing window
                                    //updateJavaWindow(javaImage);
                                }
                            }
                        } else {
                    /*
                    * This packet isn't part of our video stream, so we just silently drop it.
                    */
                            do {
                            } while (false);
                        }
                    }


                    if (videoCoder != null) {
                        videoCoder.close();
                        videoCoder = null;

                    }
                    if (container != null) {
                        container.close();
                        container = null;
                    }
//                    DSFilterInfo[][] dsi = DSCapture.queryDevices();

                    /** this sample only uses video **/
//                    int ind = Integer.parseInt(deviceName.substring(0, 1));
//                    graph = new DSCapture(DSFiltergraph.DD7, dsi[0][ind], false, DSFilterInfo.doNotRender(), this);
//                    while (deviceOrMovie) do {
//                    } while (false);


                } else {
                    //for just video


                    try {
                        nuller = true;
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
                                javaImage = updateSize((BufferedImage) event.getImage());

                                //updateJavaWindow(img);

                            }
                        };
                        reader.addListener(adapter);

                        while (reader.readPacket() == null && nuller)

                            do {
                            } while (false);
                        //  closeJavaWindow();    }
                    } catch (Exception e) {
//                        e.printStackTrace();
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
//        if (wt < javaImage.getWidth() * 2 + 300) {
//            java.awt.Image scaledImage = javaImage.getScaledInstance((int) (wt / 2 - 130), (int) (ht / 1.5 - 20), 1);
//            // convert the scaled image back to a buffered image
//            BufferedImage img = new BufferedImage((int) (wt / 2 - 130), (int) (ht / 1.5 - 20), BufferedImage.TYPE_3BYTE_BGR);
//            img.getGraphics().drawImage(scaledImage, 0, 0, null);
//            javaImage = img;
//        }

            //it's takes our images to array
//            if (ImageAdjaster.quantoIm) {
//                if (framesColored.size() > 30)
//                    framesColored = new LinkedList<BufferedImage>(framesColored.subList(15, 30));
//                //if (framesColored.size() > 30) framesColored.clear();
//                if (counter % 5 == 0) additionFramesColored.add(tics);
//                if (additionFramesColored.size() > 50)
//                    additionFramesColored = new LinkedList<BufferedImage>(additionFramesColored.subList(20, 40));
//                //framesColored.addAll(additionFramesColored);
//                framesColored.add(tics);
//            }
            if (ImageAdjaster.quantoIm) {
                if (framesColored.size() > 30)
                    framesColored = new LinkedList<BufferedImage>(framesColored.subList(15, 30));
                //if (framesColored.size() > 30) framesColored.clear();
                if (counter % 5 == 0) additionFramesColored.add(tics);
                if (additionFramesColored.size() > 50)
                    additionFramesColored = new LinkedList<BufferedImage>(additionFramesColored.subList(20, 40));
                //framesColored.addAll(additionFramesColored);
                framesColored.add(tics);
            }
            BufferedImage result = new BufferedImage(javaImage.getWidth() * 2, javaImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            //left image
            BufferedImage leftImg = result.getSubimage(0, 0, javaImage.getWidth(), javaImage.getHeight());
            int acc = 0;
            leftImg = javaImage;
            //right image
            BufferedImage rightImg = result.getSubimage(javaImage.getWidth(), 0, javaImage.getWidth(), javaImage.getHeight());
            rightImg = AppletMaker.deepCopy(javaImage);
            counter++;
            mScreen.setPreferredSize(new Dimension(javaImage.getWidth() * 2, javaImage.getHeight()));
            //if (counter % 2 == 0)

            // System.out.println(framesColored.size());
            try {

                if (mScreen.sumxy > 0 && mScreen.sumwh > 0) {
                    if (acc != mScreen.sumxy) {
                        BufferedImage im1 = AppletMaker.deepCopy(leftImg);
                        BufferedImage im2 = AppletMaker.deepCopy(rightImg);
                        mScreen.image = im1;
                        if (counter > 0 && mScreen.x < javaImage.getWidth()) {
                            //color it!

                            //rightImg = Quantizer.quantoImageTest(tics, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height);

                            // rightImg = Quantizer.quantoImageAveraged(framesColored, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height, sizeOfSqare);
                            //rightImg = Quantizer.applySurf(rightImg,false);

                            if (counter % milsBetwFr * 10 == 0 || colorMapOfRegion.length != mScreen.width * mScreen.height) {
                                colorMapOfRegion = new int[mScreen.width * mScreen.height];

                                rightImg = ImageAdjaster.surfImage(mScreen,framesColored, sizeOfSqare, im1, mScreen.x, mScreen.y, mScreen.width, mScreen.height, rightImg);
//                                rightImg.getRGB(mScreen.x, mScreen.y, mScreen.width, mScreen.height, colorMapOfRegion, 0, mScreen.width);
                                //  System.out.print(colorMapOfRegion);
                                //java.util.List<java.util.List<Integer>> xyaxis = AppGo.featureMatcher(tics, im2, mScreen.x, mScreen.y, mScreen.width, mScreen.height);
//                      xAxis.addAll(xyaxis.get(0));
//                      yAxis.addAll(xyaxis.get(1)); )
                            } else {
                                rightImg.setRGB(mScreen.x, mScreen.y, mScreen.width, mScreen.height, colorMapOfRegion, 0, mScreen.width);
                            }
                        }
                    }


                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception: ", e);
            }
            acc = mScreen.sumxy;

            old = (BufferedImage) leftImg;
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
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        return img;
    }

    public static BufferedImage getRGBScale(BufferedImage inputImage) {
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();

        return img;
    }

    /**
     * Forces the swing thread to terminate; I'm sure there is a right
     * way to do this in swing, but this works too.
     */
    private static void closeJavaWindow() {
        System.exit(0);
    }

    private static BufferedImage updateSize(BufferedImage image) {
        if (sizeH != 1) {
            java.awt.Image scaledImage = image.getScaledInstance((int) (image.getWidth() * sizeW), (int) (image.getHeight() * sizeH), 1);
            // convert the scaled image back to a buffered image
            BufferedImage img = new BufferedImage((int) (image.getWidth() * sizeW), (int) (image.getHeight() * sizeH), BufferedImage.TYPE_3BYTE_BGR);
            img.getGraphics().drawImage(scaledImage, 0, 0, null);
            return img;
        } else return image;
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

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        updateJavaWindow(graph.getImage());
//        //To change body of implemented methods use File | Settings | File Templates.
//        switch (DSJUtils.getEventType(evt)) {
//            default: ;
//        }
//    }
}