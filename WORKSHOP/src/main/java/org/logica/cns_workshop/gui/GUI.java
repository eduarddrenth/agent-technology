package org.logica.cns_workshop.gui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
   private MemoryImage image = null;
   private MemoryImagePanel panel = null;
   private static GUI me = new GUI();

   public static GUI getInstance() {
      if (!me.isVisible()) {
         me.setVisible(true);
      }
      return me;
   }

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

      image = new MemoryImage(w, h);
      panel = new MemoryImagePanel(image);
      image.cls(Color.yellow.getRGB());
      getContentPane().add(panel);

   }

   public void clearImage() {
      image.cls(Color.lightGray.getRGB());
   }

   public void updateGui() {
      SwingUtilities.invokeLater(new Runnable() {

         public void run() {
            panel.repaint();
         }
      });
   }

   public void drawSmiley(int x, int y, int c) {
      image.drawSmiley(x, y, Color.decode(String.valueOf(c)));
   }

   public void drawDoor(Door door) {
      image.drawDoor(door.getX(), door.getY(), Color.red);
   }
}
