package org.logica.cns_workshop.communication;

import jade.content.Predicate;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public class WhereIsDoor implements Predicate {

    private Smiley smiley;
    private int x, y;

    /**
     * Get the value of smiley
     *
     * @return the value of smiley
     */
    public Smiley getSmiley() {
        return smiley;
    }

    /**
     * Set the value of smiley
     *
     * @param smiley new value of smiley
     */
    public void setSmiley(Smiley smiley) {
        this.smiley = smiley;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
