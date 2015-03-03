/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Buyer;
import org.logica.cns.flora.model.concepts.Location;

/**
 *
 * @author eduard
 */
public abstract class BuyerEvent extends AbsFloraEvent<Buyer> {

    public BuyerEvent(Buyer state) {
        super(state);
    }
    
    @Override
    public OBJECTTYPE getObjectType() {
        return OBJECTTYPE.BUYER;
    }
        
    @Override
    public Location getLocation() {
        return getState().getSlaLocation().getLocation();
    }
}
