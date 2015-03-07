package org.logica.cns_workshop.gui;

import java.awt.*;
import java.awt.image.*;

public class MemoryImage {

   int bitmapwidth;
   int bitmapheight;
   final BufferedImage img;
   final Graphics2D g2;

   public void cls(int value) {
      g2.setColor(new Color(value));
      g2.fillRect(0, 0, bitmapwidth, bitmapheight);
//      g2.dispose();
   }

   public MemoryImage(int bitmapwidth, int bitmapheight) {
      this.bitmapwidth = bitmapwidth;
      this.bitmapheight = bitmapheight;
      img = new BufferedImage(bitmapwidth, bitmapheight, BufferedImage.TYPE_INT_RGB);
      g2 = img.createGraphics();
      g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
   }

   public void drawSmiley(int x, int y, Color color) {
      g2.setColor(color);
      g2.drawOval(x, y, 10, 10);
      g2.setColor(Color.blue);
      g2.fillOval(x + 3, y + 3, 2, 2);
      g2.fillOval(x + 7, y + 3, 2, 2);
      g2.setColor(Color.red);
      g2.drawLine(x + 3, y + 7, x + 7, y + 7);
   }

   public void drawDoor(int x, int y, Color color) {
      g2.setColor(color);
      g2.drawRect(x, y, 10, 10);
   }
}
