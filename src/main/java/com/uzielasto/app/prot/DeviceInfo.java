package com.uzielasto.app.prot;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 10.11.12
 * Time: 18:51
 * All rights recieved.(c)
 */

import javax.media.Format;
import javax.media.control.FormatControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;
import java.awt.*;


public class DeviceInfo
{

    public static Format formatMatches (Format format, Format supported[] )
    {
        if (supported == null)
            return null;
        for (int i = 0;  i < supported.length;  i++)
            if (supported[i].matches(format))
                return supported[i];
        return null;
    }


    public static boolean setFormat(DataSource dataSource, Format format)
    {
        boolean formatApplied = false;

        FormatControl formatControls[] = null;
        formatControls = ((CaptureDevice) dataSource).getFormatControls();
        for (int x = 0; x < formatControls.length; x++)
        {
            if (formatControls[x] == null)
                continue;

            Format supportedFormats[] = formatControls[x].getSupportedFormats();
            if (supportedFormats == null)
                continue;

            if (DeviceInfo.formatMatches(format, supportedFormats) != null)
            {
                formatControls[x].setFormat(format);
                formatApplied = true;
            }
        }

        return formatApplied;
    }


    public static boolean isVideo(Format format)
    {
        return (format instanceof VideoFormat);
    }


    public static boolean isAudio(Format format)
    {
        return (format instanceof AudioFormat);
    }


    public static String formatToString(Format format)
    {
        if (isVideo(format))
            return videoFormatToString((VideoFormat) format);

        if (isAudio(format))
            return audioFormatToString((AudioFormat) format);

        return ("--- unknown media device format ---");
    }


    public static String videoFormatToString(VideoFormat videoFormat)
    {
        StringBuffer result = new StringBuffer();

        // add width x height (size)
        Dimension d = videoFormat.getSize();
        result.append("size=" + (int) d.getWidth() + "x" + (int) d.getHeight() + ", ");

        /*
          // try to add color depth
          if (videoFormat instanceof IndexedColorFormat)
          {
              IndexedColorFormat f = (IndexedColorFormat) videoFormat;
              result.append("color depth=" + f.getMapSize() + ", ");
          }
          */

        // add encoding
        result.append("encoding=" + videoFormat.getEncoding() + ", ");

        // add max data length
        result.append("maxdatalength=" + videoFormat.getMaxDataLength() + "");

        return result.toString();
    }


    public static String audioFormatToString(AudioFormat audioFormat)
    {
        StringBuffer result = new StringBuffer();

        // short workaround
        result.append(audioFormat.toString().toLowerCase());

        return result.toString();
    }
}