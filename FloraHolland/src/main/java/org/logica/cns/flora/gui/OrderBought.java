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
public class OrderBought extends OrderEvent {

    public OrderBought(Order state) {
        super(state);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.ORDERBOUGHT;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format("buyer=%s\n", getState().getBuyer().getLocalName());
    }
}
