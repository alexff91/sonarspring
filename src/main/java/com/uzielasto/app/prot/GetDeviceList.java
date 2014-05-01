package com.uzielasto.app.prot;

import javax.media.CaptureDeviceInfo;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.format.VideoFormat;
import javax.sound.sampled.AudioFormat;
/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 10.11.12
 * Time: 18:32
 * All rights recieved.(c)
 */
public class GetDeviceList
{


    private static CaptureDeviceInfo captureVideoDevice = null;
    private static CaptureDeviceInfo    captureAudioDevice = null;
    private static VideoFormat captureVideoFormat = null;
    private static AudioFormat          captureAudioFormat = null;


    public static void devicelist()
{
    java.util.Vector deviceListVector = CaptureDeviceManager.getDeviceList();

    if (deviceListVector == null)
    {
        System.out.println("... error: media device list vector is null, program aborted");

    }
    if (deviceListVector.size() == 0)
    {
        System.out.println("... error: media device list vector size is 0, program aborted");

    }


    System.out.println("... list completed.");



}
}
