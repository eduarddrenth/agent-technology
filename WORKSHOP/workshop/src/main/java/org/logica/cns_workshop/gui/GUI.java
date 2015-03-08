package org.logica.cns_workshop.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.logica.cns.generic.JadeHelper;
import org.logica.cns_workshop.communication.Door;

public class GUI extends JFrame {

   public static final String H = "H";
   public static final String W = "W";
   private static final long serialVersionUID = 1L;
   private int w = 300;
   private int h = 300;
   private static GUI me = new GUI();

   public static GUI getInstance() {
      if (!me.isVisible()) {
         me.setVisible(true);
      }
      return me;
   }
   
   private final Graphics2D g2;
   private final BufferedImage img;

   private GUI() {
      w = Integer.parseInt(JadeHelper.getProperty(W));
      h = Integer.parseInt(JadeHelper.getProperty(H));

      addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent arg0) {
            super.windowClosing(arg0);
            setVisible(false);
         }
      });

      setSize(w, h);
      setVisible(true);
      img = new BufferedImage(w*3, h*3, BufferedImage.TYPE_INT_RGB);
      g2 = img.createGraphics();
      g2.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.scale(3, 3);
   }

   public void cls(Color value) {
      g2.setColor(value);
      g2.fillRect(0, 0, w*2, h*2);
   }

   @Override
   public void paint(Graphics g) {
      g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
   }

   public void updateGui() {
      SwingUtilities.invokeLater(new Runnable() {

         public void run() {
            repaint();
         }
      });
   }

   public void drawSmiley(int x, int y, Color color, boolean altruistic) {
      if (altruistic) {
         g2.setStroke(new BasicStroke(2));
         g2.setColor(Color.orange);
         g2.drawOval(x+2, y-4, 6, 3);
      }
      g2.setColor(color);
      g2.setStroke(new BasicStroke(3));
      g2.drawOval(x, y, 10, 10);
      g2.setColor(Color.yellow);
      g2.fillOval(x, y, 10, 10);
      g2.setColor(Color.blue);
      g2.fillOval(x + 2, y + 3, 2, 2);
      g2.fillOval(x + 6, y + 3, 2, 2);
      g2.setColor(Color.red);
      g2.setStroke(new BasicStroke(1));
      g2.drawArc(x + 2, y + 5, 6, 3,180,180);
   }

   public void drawDoor(int x, int y, Color color) {
      g2.setColor(color);
      g2.drawRect(x, y, 10, 10);
   }

   public void drawDoor(Door door) {
      drawDoor(door.getX(), door.getY(), Color.red);
   }
}
