/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import jade.util.leap.Iterator;
import org.logica.cns.flora.model.concepts.Buyer;
import org.logica.cns.flora.model.concepts.Order;

/**
 *
 * @author eduard
 */
public class BuyerBought extends BuyerEvent {

    public BuyerBought(Buyer state) {
        super(state);
    }

    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.BUYERBOUGHT;
    }
    
    @Override
    public String toString() {
        String orders = "";
        for (Iterator it = getState().getBought().iterator(); it.hasNext();) {
            Order o = (Order) it.next();
            orders += o.getId().getLocalName();
            if (it.hasNext()) {
                orders += ",";
            }
        }
        return super.toString() + String.format("orders=%s\n",
                orders);
    }
        
}
