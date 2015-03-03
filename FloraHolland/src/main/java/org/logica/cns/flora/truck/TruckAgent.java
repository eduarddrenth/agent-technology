/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.truck;

import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.gui.TruckArrived;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Truck;

/**
 *
 * @author eduard
 */
public class TruckAgent extends FloraAgent<Truck> {
    
    @Override
    protected void setup() {
        super.setup();
        Auction auction = parseArguments();

        Truck t = new Truck();
        t.setId(getAID());
        t.setFrom(auction);
        setData(t);

        publishLocation(auction, LOCATION_TYPE.AT);
        publishLocation(auction, LOCATION_TYPE.FROM);
        
        addBehaviour(new TruckState(this));
    }

    public void gone() {
        publishGone(getData().getFrom(), LOCATION_TYPE.AT);
    }

    public void arrive(Auction auction) {
        notify("reached " + auction.getId().getLocalName());
        publishLocation(auction, LOCATION_TYPE.FROM);
        publishLocation(auction, LOCATION_TYPE.AT);
        getData().setFrom(auction);
        getData().setTo(null);
        TruckArrived tar = new TruckArrived(getData());
        notify(tar);
    }

}
