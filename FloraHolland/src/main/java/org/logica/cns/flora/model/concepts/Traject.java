/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eduard
 */
public class Traject extends AgentState {
    private Auction from, to;
    private long duration;
    private Map<Period, Long> durations = new HashMap<Period, Long>();

    @Slot(mandatory=true)
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Slot(mandatory=true)
    public Auction getFrom() {
        return from;
    }

    public void setFrom(Auction from) {
        this.from = from;
    }

    @Slot(mandatory=true)
    public Auction getTo() {
        return to;
    }

    public void setTo(Auction to) {
        this.to = to;
    }

    @SuppressSlot
    public int followUpChance() {
        // todo
        return 0;
    }

    @SuppressSlot
    public long getDuration(Period p) {
        return durations.get(p);
    }

    @SuppressSlot
    public List<Order> getDemand(Period p) {
        return null;
    }
    
    @SuppressSlot
    public double getDistance() {
        return getFrom().getLocation().getDistance(getTo().getLocation());
    }
    
    @SuppressSlot
    public double getKilometerDistance() {
        return getFrom().getLocation().getKilometerDistance(getTo().getLocation());
    }

}
