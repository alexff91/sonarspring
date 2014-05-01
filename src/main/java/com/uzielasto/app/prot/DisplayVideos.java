package com.uzielasto.app.prot;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 10.11.12
 * Time: 18:27
 * All rights recieved.(c)
 */

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IMetaData;

import javax.swing.*;



public class DisplayVideos {
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
     *
     * @param args Must contain two strings: a FFMPEG driver name and a
     *             device name (which is dependent on the FFMPEG driver).
     */

    public static void main(String[] args) {


        // create a new mr. display webcam video

       // new DisplayVideos("usbvideo.inf:Microsoft.NTamd64:USBVideo:6.1.7601.17514:usb\\class_0e", "Chicony USB 2.0 Camera");
        new DisplayVideos();
    }

    /**
     * Construct a DisplayWebcamVideo which reads and plays a video
     * from an attached webcam.
     *
     * @param driverName the name of the webcan drive
     * @param deviceName the name of the webcan device
     */

    public DisplayVideos() {
        // create a Xuggler container object

        IContainer container = IContainer.make();

        // tell Xuggler about the device format

        IContainerFormat format = IContainerFormat.make();

        IContainer container_test = IContainer.make(format);
        // devices, unlike most files, need to have parameters set in order
        // for Xuggler to know how to configure them, for a webcam, these
        // parameters make sense

        IMetaData params = IMetaData.make();

        params.setValue("framerate", "10/1");
        params.setValue("video_size", "320x240");


        // open the container
        int retval = container.open("test.mp4", IContainer.Type.READ, format,
                true, true, params,null);
//        int retval = container.open(deviceName, IContainer.Type.READ, format,
//                false, true, params, null);

        if (retval < 0) {
            // this little trick converts the non friendly integer return
            // value into a slightly more friendly object to get a
            // human-readable error name

            IError error = IError.make(retval);
            throw new IllegalArgumentException(
                    "could not open file: "  + "; Error: " +
                            error.getDescription());
        }

        // create a media reader to wrap that container

        IMediaReader reader = ToolFactory.makeReader(container);
        //reader.addListener(ToolFactory.makeWriter("2.avi"));
        // Add a media viewer that will display the video, but that exits
        // the JVM when it is destroyed
        reader.addListener(ToolFactory.makeViewer(true, JFrame.EXIT_ON_CLOSE));

        // read out the contents of the media file, note that nothing else
        // happens here.  action happens in the onVideoPicture() method
        // which is called when complete video pictures are extracted from
        // the media source.  Since we're reading from a web cam this
        // loop will never return, but if the window is closed, the JVM is
        // exited.

        while (reader.readPacket() == null )
            do {
            } while (false);

    }

}