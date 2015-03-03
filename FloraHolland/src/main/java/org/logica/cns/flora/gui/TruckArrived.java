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
public class TruckArrived extends TruckEvent {

    public TruckArrived(Truck state) {
        super(state);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.TRUCKARRIVED;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format("auction=%s\n", getState().getFrom().getId().getLocalName());
    }
        
}
