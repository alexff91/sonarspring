package com.uzielasto.app.scala


import java.io.File
import javax.imageio.ImageIO
import java.awt.image._
import com.uzielasto.app.{VideoImage, ImageAdjaster, ElastoGo}
import java.util
import com.uzielasto.app.javasurf.src.main.java.org.javasurf.base._
import scala.{Int, Float}
import java.awt._
import scala.List
import java.util.logging.{Level, Logger}
import java.util.Date
import java.util.concurrent.TimeUnit


/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr
 * Date: 18.11.12
 * Time: 0:20
 * All rights recieved.(c)
 */
object Quantizer extends App {
  private val log: Logger = Logger.getLogger("Quantizer")

  def SurfingImages(original: BufferedImage, toChange: BufferedImage, xx: Int, yy: Int, w: Int, h: Int) = {

  }

  def scaleImage(javaImage: BufferedImage, sizeW: Double, sizeH: Double) = {
    //    val scaledImage: Image = javaImage.getScaledInstance((javaImage.getWidth * sizeW).asInstanceOf[Int], (javaImage.getHeight * sizeH).asInstanceOf[Int], 1)
    //    // convert the scaled image back to a buffered image
    //    val img: BufferedImage = new BufferedImage((javaImage.getWidth * sizeW).asInstanceOf[Int], (javaImage.getHeight * sizeH).asInstanceOf[Int], BufferedImage.TYPE_3BYTE_BGR)
    //    img.getGraphics.drawImage(scaledImage, 0, 0, null)
    //    img
    javaImage
  }


  /*
 * Make mask on image
 *
 * */
  def quantoImageTest(original: BufferedImage, toChange: BufferedImage, xx: Int, yy: Int, w: Int, h: Int) = {

    val srcFirstImage = original
    val srcSecondImage = toChange

    val dest = toChange
    (xx to xx + w - 1).foreach({
      x =>
        (yy to yy + h - 1).foreach({
          y =>
          //the comarison of two images
            val rgb = srcSecondImage.getRGB(x, y)
            val rgb1 = srcFirstImage.getRGB(x, y)
            //The value of eachcolor in rgb model
            //              val r = (rgb >> 16) & 0xFF
            //              val g = (rgb >> 8) & 0xFF
            //              val b = rgb & 0xFF
            val r = (rgb) & 0xFF;
            val g = (rgb >> 8) & 0xFF;
            val b = (rgb >> 16) & 0xFF;
            val r1 = (rgb1) & 0xFF;
            val g1 = (rgb1 >> 8) & 0xFF;
            val b1 = (rgb1 >> 16) & 0xFF;
            val sumSecondImage = r + g + b
            val sum = r + g + b - (r1 + g1 + b1)
            //the final color
            val c = new Color(r, g, b)
            //making color mapping
            if (sum < 50) {
              if (sumSecondImage < 60) dest.setRGB(x, y, new Color(r + 60, g, b).getRGB())
              if (sum >= 0 && sum < 500 && sumSecondImage >= 120) dest.setRGB(x, y, new Color(r, g + 60, b).getRGB())
              if (sum >= 0 && sum < 500 && sumSecondImage < 120 && sumSecondImage >= 60) dest.setRGB(x, y, new Color(r + 60, g + 60, b).getRGB())
              if ((sum < 0 && sumSecondImage > 60)) dest.setRGB(x, y, new Color(r, g, b + 60).getRGB())
              //  if (sum >= 0 && sum < 500) dest.setRGB(x, y, new Color(r, g, b).getRGB())
            } else {
              if (sum >= 500 && sum < 50000) dest.setRGB(x, y, new Color(r + 60, g + 60, b).getRGB())
              if ((sum < 0)) dest.setRGB(x, y, new Color(r, g, b + 60).getRGB())
            }
        })
    })
    //destination image
    dest
  }

  //this method will compute average of a;; images in a list
  def quantoImageAveraged(videoIm: VideoImage, framesJava: util.LinkedList[BufferedImage], toChange: BufferedImage, xx: Int, yy: Int, w: Int, h: Int, sq: Int) = {
    //converting java list to scala list
    import scala.collection.JavaConverters._
    var frames = framesJava.asScala.toList
    val scaleFactor = 1
    val srcSecondImage = scaleImage(toChange, scaleFactor, scaleFactor)
    frames = frames.map(scaleImage(_, scaleFactor, scaleFactor))
    val dest = scaleImage(toChange, scaleFactor, scaleFactor)
    println("Time of all")
    val currentTime = new Date();
    (xx * scaleFactor to (xx + w - 1) * scaleFactor by sq).foreach({
      x =>
        (yy * scaleFactor to (yy + h - 1) * scaleFactor by sq).foreach({
          y =>
            try {
              //TODO  parallel there
              val rgb = srcSecondImage.getRGB(x, y)
              val rgb1 = frames.foldLeft(List[Int]())((b, a) => if (a != null) b ::: List(a.getRGB(x, y)) else b ::: List())
              //                          val r = (rgb >> 16) & 0xFF
              //                          val g = (rgb >> 8) & 0xFF
              //                          val b = rgb & 0xFF
              val r = (rgb) & 0xFF;
              val g = (rgb >> 8) & 0xFF;
              val b = (rgb >> 16) & 0xFF;
              val sumSecondImage = r + g + b
              val r1 = rgb1.foldLeft(List[Int]())((red, a) => (a & 0xFF) :: red)
              val g1 = rgb1.foldLeft(List[Int]())((blu, a) => ((a >> 8) & 0xFF) :: blu)
              val b1 = rgb1.foldLeft(List[Int]())((blu, a) => ((a >> 16) & 0xFF) :: blu)

              val sum = (0 to r1.size - 1).par.foldLeft(List[Int]())((a, ind) => Math.abs(((r + g + b) - (r1(ind) + g1(ind) + b1(ind)))) :: a)
              //  println(sum)
              val c = new Color(r, g, b)
              //computing the average for all frames
              val average = sum.sum / sum.size
              //  println("" + average + "Firest occurance" + videoIm.getXCoord + "" + xx)


              if (videoIm.showStatTitle && videoIm.getXCoord <= xx + sq &&
                videoIm.getXCoord >= xx - sq &&
                videoIm.getYCoord <= yy + sq &&
                videoIm.getYCoord >= yy - sq) {
                println("TITTTTLE")
                videoIm.title = average + "";
              }
              //println(average)
              /*      OLD TODO
              *  if ((b + 60) < 256 && (g + 60) < 256 && (r + 60) < 256) {
                if (average < 20) {
                  if (average < 6) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r + 60, g, b).getRGB()), 0, 0)
                  if (average >= 10 && average < 20) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g + 60, b).getRGB()), 0, 0)
                  if (average >= 6 && average < 10) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r + 60, g + 60, b).getRGB()), 0, 0)
                } else {
                  if (average >= 20 && average < 30) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g + 60, b + 60).getRGB()), 0, 0)
                  if ((average >= 30)) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g, b + 60).getRGB()), 0, 0)
                }
              }*/
              if ((b + 60) < 256 && (g + 60) < 256 && (r + 60) < 256) {
                if (average < 60) {
                  if (average < 20) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r , g, b+ 60).getRGB()), 0, 0)
                  if (average >= 40 && average < 60) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g + 60, b).getRGB()), 0, 0)
                  if (average >= 20 && average < 40) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r , g + 60, b+ 60).getRGB()), 0, 0)
                } else {
                  if (average >= 60 && average < 80) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r+60, g + 60, b).getRGB()), 0, 0)
                  if ((average >= 80)) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r+60, g, b ).getRGB()), 0, 0)
                }
              }
              else {
                if (average < 40) {
                  if (average < 6) {
                    val blue: Color = new Color(70, 70, 255)
                    dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(blue.getRGB()), 0, 0)
                  }
                  if (average >= 10 && average < 40) {
                    val green: Color = new Color(70, 255, 70)
                    dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(green.getRGB()), 0, 0)
                  }
                  if (average >= 6 && average < 10) {
                    val cyan: Color = new Color(70, 255, 255)
                    dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(cyan.getRGB()), 0, 0)
                  }
                } else {
                  if (average >= 40 && average < 50) {
                    val yellow: Color = new Color(255, 255, 70)
                    dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(yellow.getRGB()), 0, 0)
                  }
                  if ((average >= 50)) {
                    val red: Color = new Color(255, 70, 70)
                    dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(red.getRGB()), 0, 0)

                  }
                }
              }
            } catch {
              case e: Exception => log.log(Level.SEVERE, e.getLocalizedMessage)
            }
        })
    })
    val old = new Date()
    println("Time Of computing" + getDateDiff(currentTime, old, TimeUnit.MILLISECONDS))


    scaleImage(dest, 1 / scaleFactor, 1 / scaleFactor)
  }

  def getDateDiff(date1: Date, date2: Date, timeUnit: TimeUnit) = {
    val diffInMillies = date2.getTime() - date1.getTime();
    timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
  }

  def getAveragecolor() {
    //                if (average < 50) {
    //                  if (sumSecondImage < 60) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r + 60, g, b).getRGB()), 0, 0)
    //                  if (average >= 0 && average < 500 && sumSecondImage >= 120) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g + 60, b).getRGB()), 0, 0)
    //                  if (average >= 0 && average < 500 && sumSecondImage < 120 && sumSecondImage >= 60) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r + 60, g + 60, b).getRGB()), 0, 0)
    //                  if ((average < 0 && sumSecondImage > 60)) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g, b + 60).getRGB()), 0, 0)
    //                  //  if (average >= 0 && average < 500) dest.setRGB(x, y, new Color(r, g, b).getRGB())
    //                } else {
    //                  if (average >= 500 && average < 50000) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r + 60, g + 60, b).getRGB()), 0, 0)
    //                  if ((average < 0)) dest.setRGB(x, y, sq, sq, Array.fill(sq * sq)(new Color(r, g, b + 60).getRGB()), 0, 0)
    //                }
    val image = ImageIO.read(new File("uzi_proj\\my-app\\screens\\1354179641351screen.png"))
    val image2 = ImageIO.read(new File("uzi_proj\\my-app\\screens\\1354179644435screen.png"))
    quantoImageTest(image, image2, 180, 70, 300, 300)
    val grayImg = ElastoGo.getGrayScale(image)
    //    val xyaxis = AppGo.featureMatcher(image, image2, 180, 70, 500, 500)
    ImageIO.write(image2, "png", new File("temp\\test.png"));
    ImageIO.write(image, "png", new File("temp\\test1.png"));
    val testiic = applySurf(grayImg, true)
    ImageIO.write(testiic, "png", new File("temp\\testSurfff.png"));
    //    ImageIO.write(testiic, "png", new File("temp\\changeIt.png"));
    //    taker.display()
    //    val grim1 = ImageAdjaster.getGray(image)
    val grim2 = ImageAdjaster.getGray(image2)
    //    ImageIO.write(ImageAdjaster.substract(grim1, grim2), "png", new File("temp\\test1.png"));
  }


  def integerImageTest(original: BufferedImage, toChange: BufferedImage, xx: Int, yy: Int, w: Int, h: Int) {

    val srcFirstImage = original
    val srcSecondImage = toChange
    val w = srcSecondImage.getWidth()
    val h = srcSecondImage.getHeight()
    //compute for squaresnot pixels
    val dest = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR)
    (0 to w - 1 by 10).foreach({
      x =>
        (0 to h - 1 by 10).par.foreach({
          y =>
            val rgb = srcSecondImage.getRGB(x, y)
            val rgb1 = srcFirstImage.getRGB(x, y)

            //              val r = (rgb >> 16) & 0xFF
            //              val g = (rgb >> 8) & 0xFF
            //              val b = rgb & 0xFF
            val r = (rgb) & 0xFF;
            val g = (rgb >> 8) & 0xFF;
            val b = (rgb >> 16) & 0xFF;
            val sum = rgb - rgb1
            // println(sum)
            val c = new Color(r, g, b)
            if (x > xx && y > yy && x < w - w && y < h - h) {
              if (sum >= 50000) dest.setRGB(x, y, new Color(r, g, b + 33).getRGB())
              if (sum >= 500 && sum < 50000) dest.setRGB(x, y, new Color(r, g + 30, b).getRGB())
              if ((sum < 0)) dest.setRGB(x, y, new Color(r, g, b + 33).getRGB())
              if (sum >= 0 && sum < 500) dest.setRGB(x, y, new Color(r, g, b).getRGB())
            }
            else {
              dest.setRGB(x, y, new Color(r, g, b).getRGB())
            }
            val rand = new scala.util.Random()

            val bol = ((x - 380) * (x - 380) + (y - 188) * (y - 188)) <= (70 * 70)
            if (x > 343 && y > 155 && x < 414 && y < 212) {
              if ((((x - 378) * (x - 378) + (y - 183) * (y - 183)) <= (rand.nextInt(40) * rand.nextInt(40)))) {
                dest.setRGB(x, y, new Color(r + rand.nextInt(80), g, b).getRGB())
                println(x + "   " + y + "   " + ((x - 378) * (x - 378) + (y - 183) * (y - 183)) + "    " + (rand.nextInt(30) * rand.nextInt(30)))
              }
            }
        })
    })

    ImageIO.write(dest, "png", new File("temp\\\\changeIt.png"))


  }

  def histogramEqualization(img: BufferedImage) = {
    val p = (img.getHeight * img.getWidth).toDouble
    val pixels = Array.fill(256)(0)
    var i = 0
    while (i < img.getWidth) {
      var j = 0
      while (j < img.getHeight) {
        val r = img.getRaster().getPixel(i, j, Array.fill(1)(0))
        pixels(r(0)) += 1
        j += 1
      }
      i += 1
    }
    //ri = (1/256) * byte no
    val lookupArray = (Array[Byte]() /: (0 to 255)) {
      (array, i) => {
        val sum = (0.0 /: (0 to i)) {
          (s, k) => {
            s + ((pixels(k)) / p.toDouble)
          }
        }
        array.:+(((1 - sum) * 255).toByte)
      }
    }
    val l = new ByteLookupTable(0, lookupArray)
    val op = new LookupOp(l, null)
    op.filter(img, null)
  }


  var firstOutImg: BufferedImage = null
  var threshold: Float = 1200
  var balanceValue: Float = 0.9F
  var octaves: Int = 5
  var lastInterestPoints: util.ArrayList[InterestPoint] = null

  def applySurf(img: BufferedImage, cleanSubsequent: Boolean = false) = {
    def drawDescriptors(pointsToDraw: util.ArrayList[InterestPoint]) {
      var i: Int = 0
      while (i < pointsToDraw.size) {
        {
          val IP: InterestPoint = pointsToDraw.get(i).asInstanceOf[InterestPoint]
          IP.drawDescriptor(new java.awt.Color(255, 0, 0))
          i += 1
        }
      }
    }
    def drawInterestPoints(pointsToDraw: util.ArrayList[InterestPoint]) {
      var i: Int = 0
      while (i < pointsToDraw.size) {
        val IP: InterestPoint = pointsToDraw.get(i)
        IP.drawPosition(1, new java.awt.Color(0, 255, 0))
        i += 1;
      }
    }
    def cleanupSubsequentPoints(pointsToMatch: util.ArrayList[InterestPoint]) = {
      lastInterestPoints match {
        case null =>
          lastInterestPoints = pointsToMatch
          pointsToMatch
        case lastPoints =>
          val nextLastPoints = pointsToMatch.clone()
          var i = 0
          while (i < lastInterestPoints.size()) {
            var index = pointsToMatch.indexOf(lastInterestPoints.get(i))
            if (index > -1) {
              pointsToMatch.remove(index)
            }
            i += 1
          }
          lastInterestPoints = nextLastPoints.asInstanceOf[util.ArrayList[InterestPoint]]
          pointsToMatch
      }
    }
    val outImg: BufferedImage = if (firstOutImg == null) {
      firstOutImg = new BufferedImage(img.getWidth, img.getHeight, img.getType)
      firstOutImg
    } else {
      firstOutImg.getGraphics.clearRect(0, 0, img.getWidth, img.getHeight)
      firstOutImg
    }
    val mySURF: ISURFfactory = SURF.createInstance(img, balanceValue, threshold, octaves, img)
    val detector: IDetector = mySURF.createDetector
    var interestPoints = detector.generateInterestPoints
    if (cleanSubsequent) {
      drawInterestPoints(cleanupSubsequentPoints(interestPoints))
    } else {
      drawInterestPoints(interestPoints)
    }
    img
  }

  def pointMatching(prev: util.ArrayList[InterestPoint], now: util.ArrayList[InterestPoint], g2: Graphics2D, x: Int, y: Int): util.List[(Integer, Integer, Integer, Integer)] = {
    // for (int i=0; i < 64; i++) { (Descriptor(image1[i])-Descriptor(image2[i]) += DIST }
    import scala.collection.JavaConverters._
    val prevSc = prev.asScala.toList
    val nowSc = now.asScala.toList

    def on[T, R <% Ordered[R]](f: T => R) = new Ordering[T] {
      def compare(x: T, y: T) = f(x) compare f(y)
    }
    def constantSlice(l: List[(Float, Int, Int)], start: Int, end: Int): List[(Float, Int, Int)] =
      l.drop(start).take(end - start)

    val descriptors = for {j <- (0 until now.size()).par
                           descNow = nowSc(j).getDescriptorOfTheInterestPoint
                           i <- 0 until prev.size
                           descPrev = prevSc(i).getDescriptorOfTheInterestPoint


    } yield {
      (((for {z <- 0 until descPrev.size} yield {
        math.abs(descPrev(z) - descNow(z))
      }).sum), j, i) //descNow.foldLeft(0.0f)((a, b) => math.abs(descPrev(z) - b) + a), j, i)
    }
    //descriptors.foreach(x => println(x))
    var acc: scala.collection.mutable.MutableList[Int] = scala.collection.mutable.MutableList()
    def foo(a: (Float, Int, Int)) = {
      acc.+=(a._3)
      a
    }

    val pen: BasicStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3, null, 0)
    g2.setStroke(pen)
    g2.setColor(new java.awt.Color(255, 31, 0))
    val matched = for {

      i <- prev.size until descriptors.size by prev.size
      parSeq = constantSlice(descriptors.toList, i - prev.size, i).par.filterNot(f => f._1 > 0.5f || (math.sqrt(math.pow(prevSc(f._3).getX -
        nowSc(f._2).getX, 2) + math.pow(prevSc(f._3).getY -
        nowSc(f._2).getY, 2)) > 5))
      minimum = if (parSeq.nonEmpty) parSeq minBy {
        minx => math.sqrt(math.pow(prevSc(minx._3).getX -
          nowSc(minx._2).getX, 2) + math.pow(prevSc(minx._3).getY -
          nowSc(minx._2).getY, 2)) + minx._1 * 10

      }
      else (9999.9f, 0, 0)

      a = foo(minimum)
      //_._1
      line = (new Integer(prevSc(minimum._3).getX.toInt), new Integer(prevSc(minimum._3).getY.toInt),
        new Integer(nowSc(minimum._2).getX.toInt), new Integer(nowSc(minimum._2).getY.toInt))
      gg = if ((Math.abs(prevSc(minimum._3).getX - nowSc(minimum._2).getX) < 5
        && Math.abs(prevSc(minimum._3).getY - nowSc(minimum._2).getY) < 5) && minimum._1 < 0.5f) {
        g2.draw(ImageAdjaster.createArrowShape(new Point(line._1 + x, line._2 + y), new Point(line._3 + x, line._4 + y)))
      }

    }
    yield {
      if ((Math.abs(prevSc(minimum._3).getX - nowSc(minimum._2).getX) < 5
        && Math.abs(prevSc(minimum._3).getY - nowSc(minimum._2).getY) < 5) && minimum._1 < 0.5f) line
      else (new Integer(0), new Integer(0), new Integer(0), new Integer(0))
    }

    if (prev.size() > 0) matched.toList.asJava
    else null

  }

}
