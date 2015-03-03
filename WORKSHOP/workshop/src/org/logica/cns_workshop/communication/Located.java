package org.logica.cns_workshop.communication;

import jade.content.Concept;
import jade.core.AID;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public abstract class Located implements Concept {
    private AID aid = null;
    private int x=0, y=0;

    public AID getAID() {
        return aid;
    }

    public void setAID(AID aid) {
        this.aid = aid;
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
