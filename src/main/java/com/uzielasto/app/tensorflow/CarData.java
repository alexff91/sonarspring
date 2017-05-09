package com.uzielasto.app.tensorflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.awt.image.BufferedImage;

/**
 * Created by nikita on 09/05/2017.
 */
public class CarData {
  private BufferedImage carImage;
  private double centerPointX;
  private double centerPointY;

  public CarData(final BufferedImage carImage, final double centerPointX,
      final double centerPointY) {
    this.carImage = carImage;
    this.centerPointX = centerPointX;
    this.centerPointY = centerPointY;
  }

  public BufferedImage getCarImage() {
    return carImage;
  }

  public void setCarImage(final BufferedImage carImage) {
    this.carImage = carImage;
  }

  public double getCenterPointX() {
    return centerPointX;
  }

  public void setCenterPointX(final double centerPointX) {
    this.centerPointX = centerPointX;
  }

  public double getCenterPointY() {
    return centerPointY;
  }

  public void setCenterPointY(final double centerPointY) {
    this.centerPointY = centerPointY;
  }
}