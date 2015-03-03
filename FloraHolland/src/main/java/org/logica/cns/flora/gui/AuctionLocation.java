/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Auction;

/**
 *
 * @author eduard
 */
public class AuctionLocation extends AuctionEvent {

    public AuctionLocation(Auction auction) {
        super(auction);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.LOCATION;
    }
    
        
}
