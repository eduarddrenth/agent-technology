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
public class OrderTooLate extends OrderEvent {

    public OrderTooLate(Order state) {
        super(state);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.ORDERTOOLATE;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format("buyer=%s\n", getState().getBuyer().getLocalName());
    }
}
