/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Location;

/**
 *
 * @author eduard
 */
public abstract class AuctionEvent extends AbsFloraEvent<Auction> {

    public AuctionEvent(Auction state) {
        super(state);
    }
    
    
    @Override
    public OBJECTTYPE getObjectType() {
        return OBJECTTYPE.AUCTION;
    }


    @Override
    public Location getLocation() {
        return getState().getLocation();
    }
        
}
