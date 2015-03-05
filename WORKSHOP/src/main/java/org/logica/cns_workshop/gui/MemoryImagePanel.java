package org.logica.cns_workshop.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class MemoryImagePanel extends JPanel {

    private MemoryImage bitmap;

    public MemoryImagePanel(MemoryImage bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * replaces the JPanel paint method; and draws the
     * pixel array via a MemoryImageSource to the screen
     */
    @Override
    public void paintComponent(Graphics g) {
        if (g != null) {
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();
            g2.drawImage(bitmap.img, 0, 0, w, h, 0, 0, bitmap.bitmapwidth, bitmap.bitmapheight, null);
        }
    }
}
