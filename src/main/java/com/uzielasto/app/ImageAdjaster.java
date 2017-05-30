/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uzielasto.app;

import com.uzielasto.app.javasurf.src.main.java.org.javasurf.base.*;
import com.uzielasto.app.scala.Quantizer;
import com.uzielasto.app.tensorflow.CarData;
import com.uzielasto.app.tensorflow.CarFinding;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import scala.Tuple4;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageAdjaster {

  public static boolean invertFlag = true;

  static float threshold = 500;

  static float balanceValue = (float) 0.81;

  static int octaves = 7;

  public static boolean histogramEq = false;

  public static boolean quantoIm = false;

  public static boolean points = false;

  public static boolean none = false;

  private static final Logger log = Logger.getLogger(ImageAdjaster.class.getName());

  static ArrayList interest_points;

  public void initParams() {
    float threshold = 30;
    float balanceValue = (float) 1;
    int octaves = 8;

    BufferedImage img = null;

    try {
      File file = new File("\\uzi_proj\\my-app\\screens\\1359719961053screen.png");
      img = ImageIO.read(file);
      BufferedImage parent = img;
      ISURFfactory mySURF = SURF.createInstance(img, balanceValue, threshold, octaves, img);
      IDetector detector = mySURF.createDetector();
      interest_points = detector.generateInterestPoints();
      IDescriptor descriptor = mySURF.createDescriptor(interest_points);
      descriptor.generateAllDescriptors();
    } catch (Exception ex) {

      ex.printStackTrace();
    }
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(1);
    JLabel label = new JLabel(new ImageIcon(img));
    frame.getContentPane().add(label, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
  }

  public static ArrayList<InterestPoint> prevDescriptors;

  public static BufferedImage savedImageToCompare;

  public static BufferedImage curImageToCompare;

  public static BufferedImage surfImage(VideoImage mScreen, LinkedList<BufferedImage> li, int sq,
      BufferedImage img, int x, int y, int w, int h, BufferedImage parent) {
    long startTime = System.currentTimeMillis();

    BufferedImage findImage = img.getSubimage(x, y, Math.abs(w), Math.abs(h));
    if (invertFlag) {
      findImage = HistogramEq.invertImage(findImage);
    }

    if (histogramEq) {
      findImage = HistogramEq.histogramEqualization(findImage);
    }

    //        if (histogramEq) findImage = HistogramEq.histogramEqualization(findImage);

    //strange surf
    //        curImageToCompare = findImage;
    //        if(savedImageToCompare!=null) {
    //        BufferedImage image = findImage;
    //        Graphics2D g2 = (Graphics2D) img.getGraphics();
    //        Surf board = new Surf(image);
    //        Map<SURFInterestPoint, SURFInterestPoint> matchingPoints = board.getMatchingPoints(board, false);
    //        image = img;
    //        Surf board2 = new Surf(image);
    //        matchingPoints = board.getMatchingPoints(board2, false);
    //        ImageAdjaster show = new ImageAdjaster(curImageToCompare,savedImageToCompare);
    //        show.drawConnectingPoints(g2,x,y);
    //            savedImageToCompare = findImage;
    //        }
    //        else {savedImageToCompare = findImage;}
    //        ArrayList interest_points;

    // g2.drawRect(x,y,w,h);
    if (points) {
      findImage = ElastoGo.getGrayScale(findImage);
      img = ElastoGo.getGrayScale(img);

      ISURFfactory mySURF = SURF.createInstance(findImage, balanceValue, threshold, octaves, img);
      IDetector detector = mySURF.createDetector();
      interest_points = detector.generateInterestPoints();

      //      IDescriptor descriptor = mySURF.createDescriptor(interest_points);
      //      descriptor.generateAllDescriptors();
      img = ElastoGo.getRGBScale(img);
      BufferedImage imgAcc = AppletMaker.deepCopy(img);
      // img = Quantizer.quantoImageAveraged(li, img, x, y, w, h, sq);
      Graphics2D g2 = (Graphics2D) img.getGraphics();
      if (quantoIm) {
        QuantizedImage quantizedImage = Quantizer.quantoImageAveraged(mScreen, li, img, x, y, w,
            h, sq);
        img = quantizedImage.getImage();
        g2 = (Graphics2D) img.getGraphics();
        Font font = new Font("Serif", Font.PLAIN, 30);
        g2.setFont(font);
        if (quantizedImage.getRedArcValues()[4] >= 20) {
          g2.setColor(new Color(255, 19, 0));
          g2.drawString("Emergency! Collision prediction", w / 3, h / 3);
        } else if (quantizedImage.getRedArcValues()[4] >= 10) {
          g2.setColor(new Color(255, 187, 41));
          g2.drawString("Objects in a safety zone", w / 3, h / 3);
        } else if (quantizedImage.getOrangeArcValues()[4] >= 20) {
          g2.setColor(new Color(255, 239, 9));
          g2.drawString("Collision ahead prediction", w / 3, h / 3);
        } else if (quantizedImage.getOrangeArcValues()[4] >= 10) {
          g2.setColor(new Color(159, 255, 0));
          g2.drawString("Possible collision ahead", w / 3, h / 3);
        } else if (quantizedImage.getGreenArcValues()[4] >= 20) {
          g2.setColor(new Color(255, 239, 9));
          g2.drawString("Collision ahead prediction", w / 3, h / 3);
        } else if (quantizedImage.getGreenArcValues()[4] >= 10) {
          g2.setColor(new Color(159, 255, 0));
          g2.drawString("Possible collision ahead", w / 3, h / 3);
        }
      }
      // if (invertFlag) img = HistogramEq.invertImage(img);

      g2.setColor(new Color(255, 249, 6));
      drawInterestPointsG(g2, x, y, w, h, interest_points, img);

      //  drawDescriptorsG(g2,x,y,interest_points);
      //  draw lines in point matched
      try {
        if (prevDescriptors == null || interest_points.size() > 5500) {
          prevDescriptors = interest_points;
          threshold += 50;
        } else {

          java.util.List<Tuple4<Integer, Integer, Integer, Integer>> lines = null;
          //          lines = Quantizer
          //              .pointMatching(prevDescriptors, interest_points, g2, x, y);
          if (lines != null) {
            for (Tuple4<Integer, Integer, Integer, Integer> line : lines) {
              // System.out.println((line._1()+x)+" "+(line._2()+y) + "    "+ (line._3()+x)+"  "+(line._4()+y));
              float[] dashl = {5, 5};
              BasicStroke pen = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3,
                  null, 0);
              g2.setStroke(pen);
              g2.setColor(new Color(255, 31, 0));

              if (!line.equals(new Tuple4<>(0, 0, 0, 0))) {
                g2.draw(createArrowShape(new Point(line._1() + x, line._2() + y),
                    new Point(line._3() + x, line._4() + y)));
              }
              //if(Math.abs(line._1()-line._3())<30&&Math.abs(line._2()-line._4())<30)g2.drawLine(line._1()+x,line._2()+y,line._3()+x,line._4()+y);
            }
          }
          prevDescriptors.clear();
          prevDescriptors = interest_points;
        }
        SURF.setItNull();
      } catch (Exception e) {
        prevDescriptors = null;
        log.log(Level.SEVERE, "Exception: ", e);
      }
    } else {
      if (quantoIm) {
        QuantizedImage quantizedImage = Quantizer.quantoImageAveraged(mScreen, li, img, x, y, w,
            h, sq);
        img = quantizedImage.getImage();

        Graphics2D g2 = (Graphics2D) img.getGraphics();
        Font font = new Font("Serif", Font.PLAIN, 30);
        g2.setFont(font);
        if (quantizedImage.getRedArcValues()[4] >= 10) {
          g2.setColor(new Color(255, 19, 0));
          g2.drawString("Emergency! Collision prediction", w / 3, h / 3);
        } else if (quantizedImage.getRedArcValues()[4] >= 0) {
          g2.setColor(new Color(255, 187, 41));
          g2.drawString("Emergency! Possible collision", w / 3, h / 3);
        } else if (quantizedImage.getOrangeArcValues()[4] >= 10) {
          g2.setColor(new Color(255, 239, 9));
          g2.drawString("Emergency! Collision ahead prediction", w / 3, h / 3);
        } else if (quantizedImage.getOrangeArcValues()[4] >= 0) {
          g2.setColor(new Color(159, 255, 0));
          g2.drawString("Emergency! Possible collision ahead", w / 3, h / 3);
        } else if (quantizedImage.getGreenArcValues()[4] >= 10) {
          g2.setColor(new Color(255, 239, 9));
          g2.drawString("Emergency! Collision ahead prediction", w / 3, h / 3);
        } else if (quantizedImage.getGreenArcValues()[4] >= 0) {
          g2.setColor(new Color(159, 255, 0));
          g2.drawString("Emergency! Possible collision ahead", w / 3, h / 3);
        }
      }
    }
    //        File out =new File("C:\\Users\\Aleksandr\\Desktop\\uzi_proj\\my-app\\surf1.png");
    //        try {
    //            ImageIO.write(newImg, "PNG", out);
    //        } catch (IOException ex) {
    //            Logger.getLogger(ImageAdjaster.class.getName()).log(Level.SEVERE, null, ex);
    //        }

    //        JFrame frame = new JFrame();
    //        frame.setDefaultCloseOperation(1);
    //        JLabel label = new JLabel(new ImageIcon(newImg));
    //        frame.getContentPane().add(label, BorderLayout.CENTER);
    //        frame.pack();
    //        frame.setVisible(true);
    // ... do something ...
    return img;
  }

  static void drawDescriptorsG(Graphics2D g2d, int x, int y,
      ArrayList<InterestPoint> interest_points) {

    //  System.out.println("Drawing Descriptors...");

    for (int i = 0; i < interest_points.size(); i++) {

      InterestPoint IP = (InterestPoint) interest_points.get(i);
      IP.drawDescriptorWithG(new Color(197, 81, 146), g2d, x, y);
    }
  }

  public static void drawInterestPointsG(Graphics2D g2d, int x, int y, int width, int height,
      ArrayList<InterestPoint> interest_points, BufferedImage img) {

    //  System.out.println("Drawing Interest Points...");
    List<DoublePoint> points = new ArrayList<DoublePoint>();
    Arc2D.Double greenArc = new Arc2D.Double(x, y + height * 1 / 4, width,
        height + height * 1 / 2, 0, 180,
        Arc2D.OPEN);
    Arc2D.Double orangeArc = new Arc2D.Double(x, y + height * 2 / 4, width,
        height, 0, 180,
        Arc2D.OPEN);
    Arc2D.Double redArc = new Arc2D.Double(x, y + height * 3 / 4, width,
        height - height * 1 / 2, 0, 180,
        Arc2D.OPEN);

    for (InterestPoint interestPoint : interest_points) {
      double[] d = new double[2];
      d[0] = interestPoint.getX();
      d[1] = interestPoint.getY();
      if (redArc.contains(d[0], d[1]) || orangeArc.contains(d[0], d[1]) || greenArc
          .contains(d[0], d[1])) {
        points.add(new DoublePoint(d));
      }
    }

    CarFinding.findClusters(points, img, g2d, x, y);

    //        DBSCANClusterer<DoublePoint> dbscan = new DBSCANClusterer<>(10, 10);
    //
    //        List<org.apache.commons.math3.ml.clustering.Cluster<DoublePoint>> cluster = dbscan.cluster(points);
    //        for (org.apache.commons.math3.ml.clustering.Cluster<DoublePoint> c : cluster) {
    //            Random rnd = new Random();
    //            drawVoronoi(g2d, c,x,y);
    //            Color color = new Color(rnd.nextInt());
    //            for (DoublePoint point : c.getPoints()) {
    //                double[] xyPoint = point.getPoint();
    //                if ((int) xyPoint[0] < w && (int) xyPoint[1] < h*2) {
    //                    InterestPoint ip = new InterestPoint((float) xyPoint[0], (float) xyPoint[1], 1, img);
    //                    ip.drawPositionWithG(3, color, g2d, x, y);
    //                }
    //            }
    //        }

    //SIMPLE DRAW

    for (int i = 0; i < interest_points.size(); i++) {

      InterestPoint IP = (InterestPoint) interest_points.get(i);

      if ((int) IP.getX() < width && (int) IP.getY() < height) {
        IP.drawPositionWithG(3, new Color(92, 255, 59), g2d, x, y);
      }
    }
  }

  //    static void drawDescriptors() {
  //
  //        //  System.out.println("Drawing Descriptors...");
  //
  //        for (int i = 0; i < interest_points.size(); i++) {
  //
  //
  //            InterestPoint IP = (InterestPoint) interest_points.get(i);
  //
  //            IP.drawDescriptor(new Color(197, 81, 146));
  //
  //        }
  //
  //    }
  //
  //
  //    static void drawInterestPoints() {
  //
  //        //  System.out.println("Drawing Interest Points...");
  //
  //        for (int i = 0; i < interest_points.size(); i++) {
  //
  //            InterestPoint IP = (InterestPoint) interest_points.get(i);
  //
  //            IP.drawPosition(5, new Color(92, 255, 59));
  //
  //        }
  //
  //    }

  public static void drawVoronoi(Graphics2D g2, Cluster<DoublePoint> points, int x, int y) {
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Polygon hull = new Polygon();

    ArrayList<Point> arrayOfPoints = toArray(points, x, y);
    for (Point p : execute(arrayOfPoints)) {
      hull.addPoint(p.x, p.y);
    }

    if (hull.npoints > 0) {
      g2.setColor(Color.ORANGE);
      g2.drawPolygon(hull);
    }
  }

  public static ArrayList<Point> toArray(Cluster<DoublePoint> points, int x, int y) {
    ArrayList<Point> pointArrayList = new ArrayList<>();
    for (DoublePoint point : points.getPoints()) {
      double[] coord = point.getPoint();
      pointArrayList.add(new Point((int) coord[0] + x, (int) coord[1] + y));
    }
    return pointArrayList;
  }

  public static ArrayList<Point> execute(ArrayList<Point> points) {
    ArrayList<Point> xSorted = (ArrayList<Point>) points.clone();
    Collections.sort(xSorted, new XCompare());

    int n = xSorted.size();

    Point[] lUpper = new Point[n];

    lUpper[0] = xSorted.get(0);
    lUpper[1] = xSorted.get(1);

    int lUpperSize = 2;

    for (int i = 2; i < n; i++) {
      lUpper[lUpperSize] = xSorted.get(i);
      lUpperSize++;

      while (lUpperSize > 2 && !rightTurn(lUpper[lUpperSize - 3], lUpper[lUpperSize - 2],
          lUpper[lUpperSize - 1])) {
        // Remove the middle point of the three last
        lUpper[lUpperSize - 2] = lUpper[lUpperSize - 1];
        lUpperSize--;
      }
    }

    Point[] lLower = new Point[n];

    lLower[0] = xSorted.get(n - 1);
    lLower[1] = xSorted.get(n - 2);

    int lLowerSize = 2;

    for (int i = n - 3; i >= 0; i--) {
      lLower[lLowerSize] = xSorted.get(i);
      lLowerSize++;

      while (lLowerSize > 2 && !rightTurn(lLower[lLowerSize - 3], lLower[lLowerSize - 2],
          lLower[lLowerSize - 1])) {
        // Remove the middle point of the three last
        lLower[lLowerSize - 2] = lLower[lLowerSize - 1];
        lLowerSize--;
      }
    }

    ArrayList<Point> result = new ArrayList<Point>();

    for (int i = 0; i < lUpperSize; i++) {
      result.add(lUpper[i]);
    }

    for (int i = 1; i < lLowerSize - 1; i++) {
      result.add(lLower[i]);
    }

    return result;
  }

  private static boolean rightTurn(Point a, Point b, Point c) {
    return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x) > 0;
  }

  private static class XCompare implements Comparator<Point> {
    @Override
    public int compare(Point o1, Point o2) {
      return (new Integer(o1.x)).compareTo(new Integer(o2.x));
    }
  }

  public static Shape createArrowShape(Point fromPt, Point toPt) {
    Polygon arrowPolygon = new Polygon();
    arrowPolygon.addPoint(-6, 0);
    arrowPolygon.addPoint(3, 0);
    arrowPolygon.addPoint(3, 1);
    arrowPolygon.addPoint(6, 0);
    arrowPolygon.addPoint(3, -1);
    arrowPolygon.addPoint(3, 0);
    arrowPolygon.addPoint(-6, 0);

    Point midPoint = midpoint(fromPt, toPt);

    double rotate = Math.atan2(toPt.y - fromPt.y, toPt.x - fromPt.x);

    AffineTransform transform = new AffineTransform();
    transform.translate(midPoint.x, midPoint.y);
    double ptDistance = fromPt.distance(toPt);
    double scale = ptDistance / 2; // 12 because it's the length of the arrow polygon.
    transform.scale(scale, scale);
    transform.rotate(rotate);

    return transform.createTransformedShape(arrowPolygon);
  }

  public static BufferedImage substract(BufferedImage im1, BufferedImage im2) {
    BufferedImage image = new BufferedImage(im1.getWidth(), im1.getHeight(),
        BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < im1.getWidth() - 1; i++) {
      for (int j = 0; j < im1.getHeight() - 1; j++) {
        int first = im1.getRGB(i, j);
        int sec = im2.getRGB(i, j);
        int res = first - sec;
        image.setRGB(i, j, res);
      }
    }
    return image;
  }

  public static BufferedImage getGray(BufferedImage buf1) {
    BufferedImage image = new BufferedImage(buf1.getWidth(), buf1.getHeight(),
        BufferedImage.TYPE_BYTE_GRAY);
    Graphics g = image.getGraphics();
    g.drawImage(buf1, 0, 0, null);
    g.dispose();
    return image;
  }

  private static Point midpoint(Point p1, Point p2) {
    return new Point((int) ((p1.x + p2.x) / 2.0),
        (int) ((p1.y + p2.y) / 2.0));
  }
}
