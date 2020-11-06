package game.engine;

import java.awt.*;

/**
 * Utility class with various helper methods
 */
public class Helpers {

  /** Can't be instantiated */
  private Helpers() {}

  /**
   * Draw a String centered in the middle of a Rectangle.
   *
   * Helper method from:
   *
   * https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
   *
   * @param g The Graphics instance.
   * @param text The String to draw.
   * @param rect The Rectangle to center the text in.
   */
  static public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
    FontMetrics metrics = g.getFontMetrics(font);

    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

    g.setFont(font);
    g.drawString(text, x, y);
  }

  /**
   * Map one range of integers to another
   *
   * From: https://stackoverflow.com/questions/7505991/arduino-map-equivalent-function-in-java
   */
  public static int map(int x, int inMin, int inMax, int outMin, int outMax) {
    return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
  }

  /**
   * Map one range of doubles to another
   *
   * From: https://stackoverflow.com/questions/7505991/arduino-map-equivalent-function-in-java
   */
  public static double map(double x, double inMin, double inMax, double outMin, double outMax) {
    return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
  }

  /**
   * Generate a random number in a given range
   */
  public static double randomRange(double min, double max) {
    return Math.random() * (max - min) + min;
  }
}