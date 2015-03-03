/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Truck;

/**
 *
 * @author eduard
 */
public class TruckLeft extends TruckEvent {

    public TruckLeft(Truck state) {
        super(state);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.TRUCKLEFT;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format("auction=%s\n", getState().getFrom().getId().getLocalName());
    }
        
}
