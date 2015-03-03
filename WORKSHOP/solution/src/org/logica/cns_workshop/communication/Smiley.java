package org.logica.cns_workshop.communication;

import org.logica.cns_workshop.gui.MemoryImage;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public class Smiley extends Located {

    private int color;
    private boolean foundDoor = false;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean getFoundDoor() {
        return foundDoor;
    }

    public void setFoundDoor(boolean foundDoor) {
        this.foundDoor = foundDoor;
    }

    public static void drawSmiley(int x, int y, int color, MemoryImage image) {
//        image.setPixel(x - 3, y + 4, color);
        image.setPixel(x - 2, y + 4, color);
        image.setPixel(x - 1, y + 4, color);
        image.setPixel(x, y + 5, color);
        image.setPixel(x + 1, y + 5, color);
        image.setPixel(x + 2, y + 4, color);
        image.setPixel(x + 3, y + 4, color);
//        image.setPixel(x + 4, y + 4, color);

//        image.setPixel(x - 3, y - 3, color);
        image.setPixel(x - 2, y - 3, color);
        image.setPixel(x - 1, y - 3, color);
        image.setPixel(x, y - 4, color);
        image.setPixel(x + 1, y - 4, color);
        image.setPixel(x + 2, y - 3, color);
        image.setPixel(x + 3, y - 3, color);
//        image.setPixel(x + 4, y - 3, color);

        image.setPixel(x - 3, y + 3, color);
        image.setPixel(x - 3, y + 2, color);
        image.setPixel(x - 3, y + 1, color);
        image.setPixel(x - 3, y, color);
        image.setPixel(x - 3, y - 1, color);
        image.setPixel(x - 3, y - 2, color);

        image.setPixel(x + 4, y + 3, color);
        image.setPixel(x + 4, y + 2, color);
        image.setPixel(x + 4, y + 1, color);
        image.setPixel(x + 4, y, color);
        image.setPixel(x + 4, y - 1, color);
        image.setPixel(x + 4, y - 2, color);

        image.setPixel(x - 1, y - 1, MemoryImage.grey);
        image.setPixel(x + 2, y - 1, MemoryImage.grey);

        image.setPixel(x - 1, y + 1, MemoryImage.red);
        image.setPixel(x + 2, y + 1, MemoryImage.red);

        image.setPixel(x - 1, y + 2, MemoryImage.red);
        image.setPixel(x, y + 2, MemoryImage.red);
        image.setPixel(x + 1, y + 2, MemoryImage.red);
        image.setPixel(x + 2, y + 2, MemoryImage.red);
    }
    
    

}
