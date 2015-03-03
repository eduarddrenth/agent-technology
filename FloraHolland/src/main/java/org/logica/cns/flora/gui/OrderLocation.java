/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Order;

/**
 *
 * @author eduard
 */
public class OrderLocation extends OrderEvent {

    public OrderLocation(Order state) {
        super(state);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.LOCATION;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("auction=%s\n", getState().getFrom().getId().getLocalName());
    }
    
}
