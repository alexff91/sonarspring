package com.uzielasto.app.tensorflow;

import com.uzielasto.app.ImageAdjaster;
import static com.uzielasto.app.ImageAdjaster.toArray;
import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarFinding {

  public static List<CarData> findClusters(List<DoublePoint> carPoints,
      final BufferedImage regionOfInterest, final Graphics2D g2d, final int x, final int y) {
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    List<CarData> datalist = new ArrayList<>();

    DBSCANClusterer<org.apache.commons.math3.ml.clustering.DoublePoint> dbscan
        = new DBSCANClusterer<>(70, 20);

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
      g2d.setColor(Color.ORANGE);
      g2d.drawRect((int) (x + minx), (int) (y + miny), (int) width, (int) height);

      System.out.println(regionOfInterest.getHeight() + " --" + height);

      //System.out.println(maxx+width+"----"+image.getWidth());

      //если вдруг выходим за рамки то упираемся в границу, а не кидаем исключение
      if (maxx + width > regionOfInterest.getWidth()) {
        maxx = regionOfInterest.getWidth() - width - 1;
      }

      BufferedImage croppedCar = regionOfInterest
          .getSubimage((int) minx, (int) miny, (int) width, (int) height);
      double centerPointX = width / 2;
      double centerPointY = height / 2;

      datalist.add(new CarData(croppedCar, centerPointX, centerPointY));
    }

    return datalist;
  }
}