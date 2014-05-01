package com.uzielasto.app.prot;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 17.11.12
 * Time: 21:57
 * All rights recieved.(c)
 */


import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ScreenRecordingExample {

    private static final double FRAME_RATE = 26;

    private static final int SECONDS_TO_RUN_FOR = 4;

    private static final String outputFilename = "test2.mp4";

    private static Dimension screenBounds;

    public static void main(String[] args) throws IOException {

        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);
        BufferedImage img = ImageIO.read(new File("C:\\Users\\Aleksandr\\Desktop\\videomotion\\1.png"));
        //screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
        screenBounds = new Dimension(img.getWidth() / 2, img.getHeight() / 2);
        File f = new File("C:\\Users\\Aleksandr\\Desktop\\videomotion\\");
        File[] listOfImgs = f.listFiles();
        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        File f2 = new File("temp\\pictures\\out");
        File[] listOfImgs2 = f2.listFiles();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
                screenBounds.width * 2, screenBounds.height);

        long startTime = System.nanoTime();
        int i = 0;
        for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE && i < listOfImgs.length - 1; index++) {
            System.out.println(SECONDS_TO_RUN_FOR * FRAME_RATE);
            // take the screen shot
            //BufferedImage screen = getDesktopScreenshot();
            BufferedImage b1 = ImageIO.read(listOfImgs2[i]);   // available to you already
            BufferedImage b2 = ImageIO.read(listOfImgs[i]);  // available to you already
            BufferedImage result;

// if TYPE_INT_ARGB doesn't work for you, play with the other constants
            result = new BufferedImage(b1.getWidth() + b2.getWidth(), b1.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

            Graphics g = result.getGraphics();
            g.drawImage(b1, 0, 0, null);
// drawing b2 where b1 ended
            g.drawImage(b2, b1.getWidth(), 0, null);
            g.dispose();

// at this point, "result" is ready to go.
            BufferedImage screen = result;
            // convert to the right image type
//            BufferedImage bgrScreen = convertToType(screen,
//                    BufferedImage.TYPE_3BYTE_BGR);
            i++;
            // encode the image to stream #0
            writer.encodeVideo(0, screen, System.nanoTime() - startTime,
                    TimeUnit.NANOSECONDS);

            // sleep for frame rate milliseconds
            try {
                Thread.sleep((long) (1000 / FRAME_RATE));
            } catch (InterruptedException e) {
                // ignore
            }

        }

        // tell the writer to close and write the trailer if  needed
        writer.close();

    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;

    }

    private static BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(screenBounds);
            return robot.createScreenCapture(captureSize);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }

    }

}

