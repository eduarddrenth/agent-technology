/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;
import jade.core.AID;
import java.util.HashMap;
import java.util.Map;
import org.logica.cns.flora.Main;
import org.logica.cns.generic.CNSException;

/**
 *
 * @author eduard
 */
public class Auction extends AgentState {
    
    public static double[] getLocation(String veiling) {
        double[] loc = new double[2];
        if (veiling.equals(Main.AALSMEEER)) {
           loc[0] = 52.2607729;
           loc[1] = 4.7589389;
        } else if (veiling.equals(Main.BLEISWIJK)) {
           loc[0] = 52.0113558;
           loc[1] = 4.5314333;
        } else if (veiling.equals(Main.RHEINMAAS)) {
           loc[0] = 51.4452046;
           loc[1] = 6.2684382;
        } else if (veiling.equals(Main.RIJNSBURG)) {
           loc[0] = 52.190894;
           loc[1] = 4.445287;
        } else if (veiling.equals(Main.EELDE)) {
           loc[0] = 53.1333333;
           loc[1] = 6.5666667;
        } else if (veiling.equals(Main.NAALDWIJK)) {
           loc[0] = 51.993371;
           loc[1] = 4.208307;            
        } else {
            throw new CNSException("no location for: " + veiling);
        }
        return loc;
    }

    private Map<Period, Long> waittimes = new HashMap<Period, Long>();
    
    private Location location;

    @SuppressSlot
    public long getTimeToWait(Period p) {
        return waittimes.get(p);
    }

    @Slot(mandatory=true)
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void setId(AID id) {
        super.setId(id);
        Location l = new Location();
        double[] coords = getLocation(getId().getLocalName());
        l.setLatitude(coords[0]);
        l.setLongitude(coords[1]);
        setLocation(l);
    }

}
