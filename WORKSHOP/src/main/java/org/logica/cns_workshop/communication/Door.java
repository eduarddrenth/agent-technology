package org.logica.cns_workshop.communication;

import org.logica.cns_workshop.gui.MemoryImage;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public class Door extends Located {
    private boolean foundMySelf = false;

    public void drawDoor(MemoryImage image) {
        int x = getX(), y = getY();
        image.setPixel(x, y + 4, MemoryImage.red);
        image.setPixel(x + 1, y + 4, MemoryImage.red);
        image.setPixel(x + 2, y + 4, MemoryImage.red);
        image.setPixel(x + 3, y + 4, MemoryImage.red);
        image.setPixel(x + 4, y + 4, MemoryImage.red);
        image.setPixel(x - 1, y + 4, MemoryImage.red);
        image.setPixel(x - 2, y + 4, MemoryImage.red);
        image.setPixel(x - 3, y + 4, MemoryImage.red);
        image.setPixel(x - 4, y + 4, MemoryImage.red);
        image.setPixel(x, y - 4, MemoryImage.red);
        image.setPixel(x + 1, y - 4, MemoryImage.red);
        image.setPixel(x + 2, y - 4, MemoryImage.red);
        image.setPixel(x + 3, y - 4, MemoryImage.red);
        image.setPixel(x + 4, y - 4, MemoryImage.red);
        image.setPixel(x - 1, y - 4, MemoryImage.red);
        image.setPixel(x - 2, y - 4, MemoryImage.red);
        image.setPixel(x - 3, y - 4, MemoryImage.red);
        image.setPixel(x - 4, y - 4, MemoryImage.red);

        image.setPixel(x + 4, y + 3, MemoryImage.red);
        image.setPixel(x + 4, y + 2, MemoryImage.red);
        image.setPixel(x + 4, y + 1, MemoryImage.red);
        image.setPixel(x + 4, y, MemoryImage.red);
        image.setPixel(x + 4, y - 1, MemoryImage.red);
        image.setPixel(x + 4, y - 2, MemoryImage.red);
        image.setPixel(x + 4, y - 3, MemoryImage.red);
        image.setPixel(x - 4, y + 3, MemoryImage.red);
        image.setPixel(x - 4, y + 2, MemoryImage.red);
        image.setPixel(x - 4, y + 1, MemoryImage.red);
        image.setPixel(x - 4, y, MemoryImage.red);
        image.setPixel(x - 4, y - 1, MemoryImage.red);
        image.setPixel(x - 4, y - 2, MemoryImage.red);
        image.setPixel(x - 4, y - 3, MemoryImage.red);
    }

    public boolean getFoundMySelf() {
        return foundMySelf;
    }

    public void setFoundMySelf(boolean foundMySelf) {
        this.foundMySelf = foundMySelf;
    }

}
