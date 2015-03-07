package org.logica.cns_workshop.communication;

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
    
}
