/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Buyer;

/**
 *
 * @author eduard
 */
public class BuyerLocation extends BuyerEvent {

    public BuyerLocation(Buyer state) {
        super(state);
    }

    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.LOCATION;
    }
    
        
}
