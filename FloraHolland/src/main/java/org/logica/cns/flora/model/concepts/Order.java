/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;
import jade.core.AID;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author eduard
 */
public class Order  extends AgentState {
    private Auction from, to;
    private Size capacity = new Size();
    private Calendar maxArrival = Calendar.getInstance();
    private Status status;
    private AID transporter;
    private long delay, duration;
    private AID buyer;
    private boolean tooLate = false;

    public AID getBuyer() {
        return buyer;
    }

    public void setBuyer(AID buyer) {
        this.buyer = buyer;
    }


    @Slot(mandatory=true)
    public Size getCapacity() {
        return capacity;
    }

    public void setCapacity(Size capacity) {
        this.capacity = capacity;
    }

    @Slot(mandatory=true)
    public Auction getFrom() {
        return from;
    }

    public void setFrom(Auction from) {
        this.from = from;
    }

    @Slot(mandatory=true)
    public Calendar getMaxArrival() {
        return maxArrival;
    }

    public void setMaxArrival(Calendar maxArrival) {
        this.maxArrival = maxArrival;
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

    public AID getTransporter() {
        return transporter;
    }

    public void setTransporter(AID transporter) {
        this.transporter = transporter;
    }

    @SuppressSlot
    public Calendar getMaxDeparture() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(maxArrival.getTimeInMillis() - duration - delay));
        return c;
    }

    @SuppressSlot
    public long getUrgency() {
        return getMaxDeparture().getTimeInMillis() - new Date().getTime();
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isTooLate() {
        return tooLate;
    }

    public void setTooLate(boolean tooLate) {
        this.tooLate = tooLate;
    }

}
