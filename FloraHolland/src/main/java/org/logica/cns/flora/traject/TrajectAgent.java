/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.traject;

import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Traject;
import org.logica.cns.generic.CNSException;

/**
 *
 * @author eduard
 */
public class TrajectAgent extends FloraAgent<Traject> {

    @Override
    protected void setup() {
        super.setup();
        Auction[] auctions = parseTrajectArguments();
        Traject t = new Traject();
        t.setId(getAID());
        t.setFrom(auctions[0]);
        t.setTo(auctions[1]);
        setData(t);
        log.debug(getLocalName() + " " + getData().getKilometerDistance());
    }

    private Auction[] parseTrajectArguments() throws CNSException {
        if (null == getArguments() || getArguments().length < 2 || !(getArguments()[0] instanceof Auction) || !(getArguments()[1] instanceof Auction)) {
            throw new CNSException("A Traject needs two auctions as arguments");
        }
        return new Auction[] { (Auction) getArguments()[0], (Auction) getArguments()[1] };
    }
}
