package org.logica.cns_workshop.communication;

import jade.content.Predicate;

/**
 *
 * @author Eduard Drenth: Logica, 19-jan-2010
 * 
 */
public class YourNeighbour implements Predicate {

    Located sender;
    Located door;
    boolean foundDoor = false;

    public Located getSender() {
        return sender;
    }

    public void setSender(Located sender) {
        this.sender = sender;
    }

    public Located getDoor() {
        return door;
    }

    public void setDoor(Located door) {
        this.door = door;
    }

    public boolean getFoundDoor() {
        return foundDoor;
    }

    public void setFoundDoor(boolean foundDoor) {
        this.foundDoor = foundDoor;
    }

    

}
