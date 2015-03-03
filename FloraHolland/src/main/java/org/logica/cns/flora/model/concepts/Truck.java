/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import java.util.Calendar;

/**
 *
 * @author eduard
 */
public class Truck  extends AgentState {

    private Auction from, to;
    private Location location;
    private Size capacity;
    private Calendar eta, start;
    private List load = new ArrayList();
    private Status status;
    private double speed = 0.1;
    private double toGo;
    private int working = 0;

    @Slot(mandatory=true)
    public Size getCapacity() {
        return capacity;
    }

    public void setCapacity(Size capacity) {
        this.capacity = capacity;
    }

    public Calendar getEta() {
        return eta;
    }

    public void setEta(Calendar eta) {
        this.eta = eta;
    }

    @Slot(mandatory=true)
    public Auction getFrom() {
        return from;
    }

    public void setFrom(Auction from) {
        this.from = from;
        setLocation(from.getLocation());
    }

    public List getLoad() {
        return load;
    }

    public void setLoad(List load) {
        this.load = load;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Auction getTo() {
        return to;
    }

    public void setTo(Auction to) {
        this.to = to;
    }

    @SuppressSlot
    public long getDelay() {
        // todo
        return 0;
    }

    @SuppressSlot
    public boolean isWorkingHours() {
        return true;//working++ < 10;
    }

    @Slot(mandatory=true)
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getToGo() {
        return toGo;
    }

    public void setToGo(double toGo) {
        this.toGo = toGo;
    }

}
