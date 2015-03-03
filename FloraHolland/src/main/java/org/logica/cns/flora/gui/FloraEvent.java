/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.AgentState;
import org.logica.cns.flora.model.concepts.Location;


/**
 *
 * @author eduard
 */
public interface FloraEvent<S extends AgentState> {
    
    public enum EVENTTYPE {
        BUYERBOUGHT, ORDERBOUGHT, ORDERLEFT, ORDERTOOLATE, TRUCKLEFT, ORDERARRIVED, TRUCKARRIVED, TRUCKMOVE, LOCATION, GENERIC
    }
    
    public enum OBJECTTYPE {
        TRUCK, ORDER, AUCTION, BUYER
    }
    
    /**
     * indicates the sort of event to be displayed
     * @return
     */
    EVENTTYPE getEventType();
    
    S getState();
    
    /**
     * What is the nature of this object
     * @return 
     */
    OBJECTTYPE getObjectType();
    
    Location getLocation();
    
    int getSequence();
    void setSequence(int sequence);
        
}
