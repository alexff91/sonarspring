package com.uzielasto.app.scala

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 28.10.12
 * Time: 20:49
 * To change this template use File | Settings | File Templates.
 */

import com.xuggle.mediatool.{MediaToolAdapter, IMediaReader, MediaListenerAdapter, ToolFactory}


import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler._


import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import collection.mutable
import javax.swing.JFrame
import com.uzielasto.app.VideoImage
import java.awt.Color

class TimeStampTool extends MediaToolAdapter {


  override def onVideoPicture(event: IVideoPictureEvent) {
    // get the graphics for the image

    val g = event.getImage().createGraphics();


    //DISPLAAAY
    //display()
    // establish the timestamp and how much space it will take

    val timeStampStr = event.getPicture().getFormattedTimeStamp();
    val bounds = g.getFont().getStringBounds(timeStampStr,
      g.getFontRenderContext());

    // compute the amount to inset the time stamp and translate the
    // image to that position

    val inset = bounds.getHeight() / 2;
    g.translate(inset, event.getImage().getHeight() - inset);

    // draw a white background and black timestamp text


    // call parent which will pass the video onto next tool in chain

    super.onVideoPicture(event);
  }
}

class TakeImage extends MediaListenerAdapter {
  val SECONDS_BETWEEN_FRAMES = 0.00003
  val accumulator = new mutable.Stack[BufferedImage]()
  val Imgaccumulator = new mutable.MutableList[BufferedImage]()
  /**
   * The number of micro-seconds between frames.
   */
  var i = 0;
  val MICRO_SECONDS_BETWEEN_FRAMES = (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES) / 2
  println(MICRO_SECONDS_BETWEEN_FRAMES)
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

  def DecodeAndCaptureFrames(filename: String)  {
    // create a media reader for processing video

    val reader = ToolFactory.makeReader(filename)


    // stipulate that we want BufferedImages created in BGR 24bit color space
    //reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
    reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

    // note that DecodeAndCaptureFrames is derived from
    // MediaReader.ListenerAdapter and thus may be added as a listener
    // to the MediaReader. DecodeAndCaptureFrames implements
    // onVideoPicture().
    val addTimeStamp = new TimeStampTool();
    reader.addListener(this);

    reader.addListener(addTimeStamp);
    // read out the contents of the media file, note that nothing else
    // happens here.  action happens in the onVideoPicture() method
    // which is called when complete video pictures are extracted from
    // the media source

    while (reader.readPacket() == null)
      do {}  while (false);
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


        // write out PNG

        val image_to_write = event.getImage()
        var exchangeImg = image_to_write
        if (accumulator.size >= 1) {
          exchangeImg = accumulator.pop
          i = i + 1
          quantoImage(exchangeImg, image_to_write, 170, 75, 50, 35)
        }
        accumulator.push(image_to_write)
        val hello = "temp\\pictures\\out\\" + event.getTimeStamp + "test.png"
        val file = new File(hello)
        ImageIO.write(image_to_write, "png", file)

        // indicate file written

        val seconds = event.getTimeStamp().toDouble / Global.DEFAULT_PTS_PER_SECOND
        println("at elapsed time of %6.3f seconds wroteeeeeee: %s\n", seconds, file)

        // update last write time

        mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES.toLong
      }
    }

  }

  /*
 * Make mask on image
 *
 * */
  def quantoImage(original: BufferedImage, toChange: BufferedImage, verticalBound: Int, horizontalBound: Int, left: Int, right: Int) {

    val srcFirstImage = original
    val srcSecondImage = toChange
    val w = srcSecondImage.getWidth()
    val h = srcSecondImage.getHeight()

    val dest = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR)
    (0 to w - 1).foreach({
      x =>
        (0 to h - 1).par.foreach({
          y =>
            val rgb = srcSecondImage.getRGB(x, y)
            val rgb1 = srcFirstImage.getRGB(x, y)
            val rand = new scala.util.Random()
            val rand1 = new scala.util.Random()
            val rr = new java.util.Random()

            val r = (rgb)&0xFF;
            val g = (rgb>>8)&0xFF;
            val b = (rgb>>16)&0xFF;
            val sum = rgb - rgb1
            // println(sum)
            val c = new Color(r, g, b)
            if (x > verticalBound && y > horizontalBound && x < w - left && y < h - right) {
              if (sum >= 50000) dest.setRGB(x, y, new Color(r+ rand1.nextInt(50)+20, g, b ).getRGB())
              if (sum >= 500 && sum < 50000) dest.setRGB(x, y, new Color(r, g + rand1.nextInt(40)+20, b).getRGB())
              if ((sum < 0)) dest.setRGB(x, y, new Color(r , g, b+ rand1.nextInt(50)+20).getRGB())
              if (sum >= 0 && sum < 500) dest.setRGB(x, y, new Color(r, g, b).getRGB())
            }
            else {
              dest.setRGB(x, y, new Color(r, g, b).getRGB())
            }
            //If (X * X + Y * Y) <= R * R


            val bol = ((x-380) * (x-380) + (y - 188) * (y- 188)) <= (70*70)
            if(x > 343 && y > 155 && x < 414 && y < 212  )
            {
              val rand1 = rand.nextInt(40)
              if( (((x-378) * (x-378) + (y - 183) * (y- 183)) <= (rand1*rand1)))
              {dest.setRGB(x, y, new Color(r+70, g, b).getRGB())
               // println(x + "   " + y + "   "+((x-378) * (x-378) + (y - 183) * (y- 183)) + "    "+(rand.nextInt(30)*rand.nextInt(30)))
              }}
        })
    })
    Imgaccumulator += dest




  }
  def display() {

    val container: IContainer = IContainer.make
    val format: IContainerFormat = IContainerFormat.make

    val params: IMetaData = IMetaData.make

    params.setValue("framerate", "26/1")
    params.setValue("video_size", "720x576")
    val retval: Int = container.open("5.avi", IContainer.Type.READ, format,true, true, params,null)
    if (retval < 0) {
      val error: IError = IError.make(retval)
      throw new IllegalArgumentException("could not open file: " + "; Error: " + error.getDescription)
    }

//    val retval22: Int = container.open("test.mp4", IContainer.Type.READ, format, true, true, params, null)
//    if (retval22 < 0) {
//      val error: IError = IError.make(retval22)
//      throw new IllegalArgumentException("could not open file: " + "; Error: " + error.getDescription)
//    }
    val reader: IMediaReader = ToolFactory.makeReader(container)

    val  mediaViewer = ToolFactory.makeViewer(true,JFrame.EXIT_ON_CLOSE);

    //val writer  = ToolFactory.makeWriter("1.avi",container_test)
    //reader.addListener(writer.encodeVideo(mLastPtsWrite.getRGB, ))
    reader.addListener(mediaViewer)
    openJavaWindow();
    while (reader.readPacket == null) do {
    } while (false)
    closeJavaWindow()
  }
  /**
   * The window we'll draw the video on.
   *
   */
  private var  mScreen:VideoImage = null

  private def updateJavaWindow(javaImage: BufferedImage )=
  {
    mScreen.setImage(javaImage);
  }

  /**
   * Opens a Swing window on screen.
   */
  private def openJavaWindow() =
  {
    mScreen = new VideoImage();
  }

  /**
   * Forces the swing thread to terminate; I'm sure there is a right
   * way to do this in swing, but this works too.
   */
  private def closeJavaWindow()
  {
    System.exit(0);
  }

}
