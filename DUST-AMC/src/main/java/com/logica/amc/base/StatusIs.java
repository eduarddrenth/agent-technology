package com.logica.amc.base;

import jade.content.Predicate;

/**
 *
 * @author Eduard Drenth: Logica, 28-jan-2010
 * 
 */
public class StatusIs implements Predicate {

    private StatusNotification status;

    public StatusNotification getStatus() {
        return status;
    }

    public void setStatus(StatusNotification status) {
        this.status = status;
    }
    

}
