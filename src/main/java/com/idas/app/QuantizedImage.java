package com.idas.app;

import java.awt.image.BufferedImage;

/**
 * Created by Александр on 30.05.2017.
 */
public class QuantizedImage {
  BufferedImage image;
  int[] greenArcValues;
  int[] orangeArcValues;
  int[] redArcValues;

  public QuantizedImage(final BufferedImage image, final int[] greenArcValues,
      final int[] orangeArcValues,
      final int[] redArcValues) {
    this.image = image;
    this.greenArcValues = greenArcValues;
    this.orangeArcValues = orangeArcValues;
    this.redArcValues = redArcValues;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(final BufferedImage image) {
    this.image = image;
  }

  public int[] getGreenArcValues() {
    return greenArcValues;
  }

  public void setGreenArcValues(final int[] greenArcValues) {
    this.greenArcValues = greenArcValues;
  }

  public int[] getOrangeArcValues() {
    return orangeArcValues;
  }

  public void setOrangeArcValues(final int[] orangeArcValues) {
    this.orangeArcValues = orangeArcValues;
  }

  public int[] getRedArcValues() {
    return redArcValues;
  }

  public void setRedArcValues(final int[] redArcValues) {
    this.redArcValues = redArcValues;
  }
}
