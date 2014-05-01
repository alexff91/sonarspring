package com.uzielasto.app.scala

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 17.11.12
 * Time: 13:04
 * All rights recieved.(c)
 */


import com.xuggle.mediatool.{MediaToolAdapter, MediaListenerAdapter, ToolFactory}


import com.xuggle.xuggler.{IContainer, Global}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import com.xuggle.mediatool.event.IVideoPictureEvent

class ImageSetter extends MediaToolAdapter {


  override def onVideoPicture(event: IVideoPictureEvent) {
    // get the graphics for the image

    val g = event.getImage().createGraphics()


    // draw a white background and black timestamp text


    // call parent which will pass the video onto next tool in chain

    super.onVideoPicture(event);
  }
}

class ImageSet extends MediaListenerAdapter {
  val SECONDS_BETWEEN_FRAMES = 0.01

  /**
   * The number of micro-seconds between frames.
   */

  val MICRO_SECONDS_BETWEEN_FRAMES = (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES) / 2

  /** Time of last frame write. */

  var mLastPtsWrite: Long = Global.NO_PTS

  /**
   * The video stream index, used to ensure we display frames from one
   * and only one video stream from the media container.
   */

  private var mVideoStreamIndex = -1


  /** Construct a DecodeAndCaptureFrames which reads and captures
    * frames from a video file.
    *
    * @param filename the name of the media file to read
    */

  def EncodeFrames(filename: String, input: IContainer) {
    // create a media reader for processing video

    val reader = ToolFactory.makeReader(filename)
    val writer = ToolFactory.makeWriter(filename, input)

    // stipulate that we want BufferedImages created in BGR 24bit color space
    //reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
    reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR)

    // note that DecodeAndCaptureFrames is derived from
    // MediaReader.ListenerAdapter and thus may be added as a listener
    // to the MediaReader. DecodeAndCaptureFrames implements
    // onVideoPicture().
    val addTimeStamp = new TimeStampTool()
    reader.addListener(this)

    reader.addListener(addTimeStamp);
    // read out the contents of the media file, note that nothing else
    // happens here.  action happens in the onVideoPicture() method
    // which is called when complete video pictures are extracted from
    // the media source

    while (reader.readPacket() == null)
      do {} while (false);
  }

  /**
   * Called after a video frame has been decoded from a media stream.
   * Optionally a BufferedImage version of the frame may be passed
   * if the calling {@link IMediaReader} instance was configured to
   * create BufferedImages.
   *
   * This method blocks, so return quickly.
   */
  def tain(arg: String) {
    if (arg.length <= 0)
      throw new IllegalArgumentException(
        "must pass in a filename as the first argument")

    // create a new mr. decode and capture frames

    EncodeFrames(arg, IContainer.make())
  }

  override def onVideoPicture(event: IVideoPictureEvent): Unit = {
    try {
      // if the stream index does not match the selected stream index,
      // then have a closer look

      if (event.getStreamIndex() != mVideoStreamIndex) {
        // if the selected video stream id is not yet set, go ahead an
        // select this lucky video stream

        if (-1 == mVideoStreamIndex)
          mVideoStreamIndex = event.getStreamIndex()

        // otherwise return, no need to show frames from this video stream


      }

      // if uninitialized, backdate mLastPtsWrite so we get the very
      // first frame

      if (mLastPtsWrite == Global.NO_PTS)
        mLastPtsWrite = (event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES.toLong)

      // if it's time to write the next frame

      if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {
        // Make a temporary file name

        ImageIO.read(new File("temp\\pictures\\changeIt.png"))

        // indicate file written

        val seconds = event.getTimeStamp().toDouble / Global.DEFAULT_PTS_PER_SECOND

        // update last write time

        mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES.toLong
      }
    }

  }

}

