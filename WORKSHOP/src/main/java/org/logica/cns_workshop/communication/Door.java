package org.logica.cns_workshop.communication;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public class Door extends Located {
    private boolean foundMySelf = false;

    public boolean getFoundMySelf() {
        return foundMySelf;
    }

    public void setFoundMySelf(boolean foundMySelf) {
        this.foundMySelf = foundMySelf;
    }

}
