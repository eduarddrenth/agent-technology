package org.logica.cns_workshop.gui;

import java.awt.*;
import java.awt.image.*;

public class MemoryImage {

    int bitmapwidth;
    int bitmapheight;
    BufferedImage img;
    /**
     * static handy members: black,white,yellow etc.
     */
    public static final int black = getColorFromRGB(0, 0, 0);
    public static final int white = getColorFromRGB(255, 255, 255);
    public static final int grey = getColorFromRGB(180, 180, 180);
    public static final int light_grey = getColorFromRGB(220, 220, 220);
    public static final int yellow = getColorFromRGB(241, 237, 132);
    public static final int red = getColorFromRGB(255, 0, 0);
    public static final int green = getColorFromRGB(0, 255, 0);
    public static final int blue = getColorFromRGB(0, 0, 255);
    public static final int cyan = getColorFromRGB(128, 128, 255);

    public void cls(int value) {
        Graphics g = img.createGraphics();
        g.setColor(new Color(value));
        g.drawRect(0, 0, bitmapwidth, bitmapheight);
        g.fillRect(0, 0, bitmapwidth, bitmapheight);
        g.dispose();
    }

    public void drawImage(Image im, int x, int y, Graphics g) {
        g.drawImage(im, x, y, null);
    }

    public MemoryImage(int bitmapwidth, int bitmapheight) {
        this.bitmapwidth = bitmapwidth;
        this.bitmapheight = bitmapheight;
        img = new BufferedImage(bitmapwidth, bitmapheight, BufferedImage.TYPE_INT_RGB);
    }

    Graphics getGraphics() {
        return img.createGraphics();
    }

    /**
     * gets the color (32bit int value) from a reg green and blue value. (0-255)
     */
    public static int getColorFromRGB(int r, int g, int b) {
        return ((0xff << 24) | (r << 16) | (g << 8) | b);
    }

    public void setPixel(int x, int y, int value) {
        if (x > 0 && x < bitmapwidth && y > 0 && y < bitmapheight) {
            img.setRGB(x, y, value);
        }
    }
}
