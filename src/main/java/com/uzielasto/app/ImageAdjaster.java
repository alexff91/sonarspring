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

  public static boolean invertFlag = false;

  static float threshold = 190;

  static float balanceValue = (float) 0.81;

  static int octaves = 3;

  public static boolean histogramEq = false;

  public static boolean quantoIm = false;

  public static boolean points = false;

  public static boolean none = false;

  private static final Logger log = Logger.getLogger(ImageAdjaster.class.getName());

  static ArrayList interest_points;

  public void initParams() {
    float threshold = 30;
    float balanceValue = (float) 1;
    int octaves = 5;

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

      IDescriptor descriptor = mySURF.createDescriptor(interest_points);
      descriptor.generateAllDescriptors();
      img = ElastoGo.getRGBScale(img);
      BufferedImage imgAcc = AppletMaker.deepCopy(img);
      // img = Quantizer.quantoImageAveraged(li, img, x, y, w, h, sq);
      if (quantoIm) {
        img = Quantizer.quantoImageAveraged(mScreen, li, img, x, y, w, h, sq);
      }
      // if (invertFlag) img = HistogramEq.invertImage(img);

      Graphics2D g2 = (Graphics2D) img.getGraphics();

      g2.setColor(new Color(255, 249, 6));
      drawInterestPointsG(g2, x, y, w, h, interest_points, img);

      //  drawDescriptorsG(g2,x,y,interest_points);
      //  draw lines in point matched
      try {
        if (prevDescriptors == null || interest_points.size() > 1000) {
          prevDescriptors = interest_points;
          threshold += 50;
        } else {

          java.util.List<Tuple4<Integer, Integer, Integer, Integer>> lines = Quantizer
              .pointMatching(prevDescriptors, interest_points, g2, x, y);
          if (lines != null) {
            for (Tuple4<Integer, Integer, Integer, Integer> line : lines) {
              // System.out.println((line._1()+x)+" "+(line._2()+y) + "    "+ (line._3()+x)+"  "+(line._4()+y));
              float[] dashl = {5, 5};
              BasicStroke pen = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 3,
                  null, 0);
              g2.setStroke(pen);
              g2.setColor(new Color(255, 31, 0));

              if (!line.equals(new Tuple4<Integer, Integer, Integer, Integer>(0, 0, 0, 0))) {
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
        img = Quantizer.quantoImageAveraged(mScreen, li, img, x, y, w, h, sq);
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
    long estimatedTime = System.currentTimeMillis() - startTime;
    System.out.println("Estimated time  - " + estimatedTime);
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

  public static void drawInterestPointsG(Graphics2D g2d, int x, int y, int w, int h,
      ArrayList<InterestPoint> interest_points, BufferedImage img) {

    //  System.out.println("Drawing Interest Points...");
    List<DoublePoint> points = new ArrayList<DoublePoint>();

    for (InterestPoint interestPoint : interest_points) {
      double[] d = new double[2];
      d[0] = interestPoint.getX();
      d[1] = interestPoint.getY();
      points.add(new DoublePoint(d));
    }

    classifyObject(CarFinding.findClusters(points, img, g2d, x, y), g2d,x, y);

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

      if ((int) IP.getX() < w && (int) IP.getY() < h) {
        IP.drawPositionWithG(3, new Color(92, 255, 59), g2d, x, y);
      }
    }
  }

  private static void classifyObject(List<CarData> carDataList, Graphics2D g2d, final int x,
      final int y) {
    byte[] graphDef = readAllBytesOrExit(Paths.get("inception5h", "tensorflow_inception_graph.pb"));
    List<String> labels =
        readAllLinesOrExit(Paths.get("inception5h", "imagenet_comp_graph_label_strings.txt"));

    for (CarData carData : carDataList) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        ImageIO.write(carData.getCarImage(), "jpg", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();

        try (Tensor image = constructAndExecuteGraphToNormalizeImage(imageBytes)) {
          float[] labelProbabilities = executeInceptionGraph(graphDef, image);
          int bestLabelIdx = maxIndex(labelProbabilities);
          String classOfObject = labels.get(bestLabelIdx);
          System.out.println(
              String.format(
                  "BEST MATCH: %s (%.2f%% likely)",
                  classOfObject, labelProbabilities[bestLabelIdx] * 100f));
          g2d.setColor(Color.ORANGE);
          g2d.setStroke(new BasicStroke(3));
          g2d.drawString(classOfObject, x + (int) carData.getCenterPointX(),
             y + (int) carData.getCenterPointY());
        }
        baos.close();
      } catch (IOException e) {
        e.printStackTrace();
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

  private static Tensor constructAndExecuteGraphToNormalizeImage(byte[] imageBytes) {
    try (Graph g = new Graph()) {
      GraphBuilder b = new GraphBuilder(g);
      // Some constants specific to the pre-trained model at:
      // https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
      //
      // - The model was trained with images scaled to 224x224 pixels.
      // - The colors, represented as R, G, B in 1-byte each were converted to
      //   float using (value - Mean)/Scale.
      final int H = 224;
      final int W = 224;
      final float mean = 117f;
      final float scale = 1f;

      // Since the graph is being constructed once per execution here, we can use a constant for the
      // input image. If the graph were to be re-used for multiple input images, a placeholder would
      // have been more appropriate.
      final Output input = b.constant("input", imageBytes);
      final Output output =
          b.div(
              b.sub(
                  b.resizeBilinear(
                      b.expandDims(
                          b.cast(b.decodeJpeg(input, 3), DataType.FLOAT),
                          b.constant("make_batch", 0)),
                      b.constant("size", new int[]{H, W})),
                  b.constant("mean", mean)),
              b.constant("scale", scale));
      try (Session s = new Session(g)) {
        return s.runner().fetch(output.op().name()).run().get(0);
      }
    }
  }

  private static float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
    try (Graph g = new Graph()) {
      g.importGraphDef(graphDef);
      try (Session s = new Session(g);
           Tensor result = s.runner().feed("input", image).fetch("output").run().get(0)) {
        final long[] rshape = result.shape();
        if (result.numDimensions() != 2 || rshape[0] != 1) {
          throw new RuntimeException(
              String.format(
                  "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                  Arrays.toString(rshape)));
        }
        int nlabels = (int) rshape[1];
        return result.copyTo(new float[1][nlabels])[0];
      }
    }
  }

  private static int maxIndex(float[] probabilities) {
    int best = 0;
    for (int i = 1; i < probabilities.length; ++i) {
      if (probabilities[i] > probabilities[best]) {
        best = i;
      }
    }
    return best;
  }

  private static byte[] readAllBytesOrExit(Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      System.err.println("Failed to read [" + path + "]: " + e.getMessage());
      System.exit(1);
    }
    return null;
  }

  private static List<String> readAllLinesOrExit(Path path) {
    try {
      return Files.readAllLines(path, Charset.forName("UTF-8"));
    } catch (IOException e) {
      System.err.println("Failed to read [" + path + "]: " + e.getMessage());
      System.exit(0);
    }
    return null;
  }

  // In the fullness of time, equivalents of the methods of this class should be auto-generated from
  // the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages
  // like Python, C++ and Go.
  static class GraphBuilder {
    GraphBuilder(Graph g) {
      this.g = g;
    }

    Output div(Output x, Output y) {
      return binaryOp("Div", x, y);
    }

    Output sub(Output x, Output y) {
      return binaryOp("Sub", x, y);
    }

    Output resizeBilinear(Output images, Output size) {
      return binaryOp("ResizeBilinear", images, size);
    }

    Output expandDims(Output input, Output dim) {
      return binaryOp("ExpandDims", input, dim);
    }

    Output cast(Output value, DataType dtype) {
      return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
    }

    Output decodeJpeg(Output contents, long channels) {
      return g.opBuilder("DecodeJpeg", "DecodeJpeg")
          .addInput(contents)
          .setAttr("channels", channels)
          .build()
          .output(0);
    }

    Output constant(String name, Object value) {
      try (Tensor t = Tensor.create(value)) {
        return g.opBuilder("Const", name)
            .setAttr("dtype", t.dataType())
            .setAttr("value", t)
            .build()
            .output(0);
      }
    }

    private Output binaryOp(String type, Output in1, Output in2) {
      return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
    }

    private Graph g;
  }
}
