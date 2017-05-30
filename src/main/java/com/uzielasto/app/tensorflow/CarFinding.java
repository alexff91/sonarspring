package com.uzielasto.app.tensorflow;

import com.uzielasto.app.ImageAdjaster;
import static com.uzielasto.app.ImageAdjaster.toArray;
import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.OPTICS;
import jsat.linear.DenseVector;
import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarFinding {

  public static List<CarData> findClusters(List<DoublePoint> carPoints,
      final BufferedImage regionOfInterest, final Graphics2D g2d, final int x, final int y) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    List<CarData> datalist = new ArrayList<>();

    DBSCANClusterer<org.apache.commons.math3.ml.clustering.DoublePoint> dbscan
        = new DBSCANClusterer<>(35, 20);

    List<org.apache.commons.math3.ml.clustering.Cluster<DoublePoint>> cluster = dbscan
        .cluster(carPoints);

    for (int i = 0; i < cluster.size(); i++) {
      double maxx = 0;
      double maxy = 0;

      double minx = 0;
      double miny = 0;

      System.out.println("Cluster " + i);
      //ищем низ квадрата
      for (DoublePoint db : cluster.get(i).getPoints()) {

        double[] xy = db.getPoint();

        if (xy[0] > maxx) {
          maxx = xy[0];
        }
        if (xy[1] > maxy) {
          maxy = xy[1];
        }
      }

      System.out.println("The lowest right dot of cluster is x: " + maxx + " y: " + maxy);

      minx = maxx;
      miny = maxy;

      //ищем верх квадрата
      for (DoublePoint db : cluster.get(i).getPoints()) {
        double[] xy = db.getPoint();

        if (xy[0] < minx) {
          minx = xy[0];
        }
        if (xy[1] < miny) {
          miny = xy[1];
        }
      }
      System.out.println("The upper left dot of cluster is x: " + minx + " y: " + miny);

      //режем картинку;
      double width = maxx - minx;
      double height = maxy - miny;

      System.out.println(regionOfInterest.getHeight() + " --" + height);

      //System.out.println(maxx+width+"----"+image.getWidth());

      //если вдруг выходим за рамки то упираемся в границу, а не кидаем исключение
      if (maxx + width > regionOfInterest.getWidth()) {
        maxx = regionOfInterest.getWidth() - width - 1;
      }

      int minXScaled = Math.abs((int) minx - 10);
      int minYScaled = Math.abs((int) miny - 10);
      int widthScaled = (int) width + 10;
      int heightScaled = (int) height + 10;
      BufferedImage croppedCar = regionOfInterest
          .getSubimage(minXScaled, minYScaled, widthScaled,
              heightScaled);

      double centerPointX = minx + width / 2;
      double centerPointY = miny + height / 2;

      CarData carData = new CarData(croppedCar, centerPointX, centerPointY);
      datalist.add(carData);
      classifyObject(carData, g2d, x, y, minXScaled, minYScaled,
          widthScaled, heightScaled);
    }

    return datalist;
  }

  private static void classifyObject(CarData carData, Graphics2D g2d, final int x,
      final int y, final int minXScaled, final int minYScaled, final int widthScaled,
      final int heightScaled) {
    byte[] graphDef = readAllBytesOrExit(Paths.get("inception5h", "tensorflow_inception_graph.pb"));
    List<String> labels =
        readAllLinesOrExit(Paths.get("inception5h", "imagenet_comp_graph_label_strings.txt"));

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
        if (classOfObject.contains("car") || classOfObject.contains("van") || classOfObject
            .contains("vagon") || classOfObject.contains("vehicle") || classOfObject.contains
            ("train") || classOfObject.contains("truck") || classOfObject.contains("human") ||
            classOfObject.contains("bicycle") || classOfObject.contains("container")) {
          g2d.setColor(Color.RED);
          g2d.setStroke(new BasicStroke(4));
          g2d.drawString(classOfObject, x + (int) carData.getCenterPointX(),
              y + (int) carData.getCenterPointY());

          g2d.setColor(Color.ORANGE);
          if (widthScaled < 100) {
            g2d.setColor(Color.GREEN);
          } else if (widthScaled >= 100 && widthScaled < 200) {
            g2d.setColor(Color.ORANGE);
          } else if (widthScaled >= 200) {
            g2d.setColor(Color.red);
          }
          g2d.setStroke(new BasicStroke(5));

          g2d.drawRect(x + minXScaled, y + minYScaled, widthScaled, heightScaled);
        } else {
          g2d.setStroke(new BasicStroke(2));
          g2d.setColor(Color.BLUE);
          g2d.drawRect(x + minXScaled, y + minYScaled, widthScaled, heightScaled);
        }
      }
      baos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
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