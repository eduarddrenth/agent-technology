/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.truck;

import jade.core.behaviours.TickerBehaviour;
import java.util.Calendar;
import java.util.Random;
import org.logica.cns.flora.Main;
import org.logica.cns.flora.gui.TruckLeft;
import org.logica.cns.flora.gui.TruckMove;
import org.logica.cns.flora.model.concepts.Location;

/**
 *
 * @author eduard
 */
public class Driving extends TickerBehaviour {

    TruckAgent ta;
    private double driven;
    private double dx, dy;
    private Location l;
    TruckMove moveEvent;
    private long period;
    Random r = new Random();

    public Driving(TruckAgent a, long period) {
        super(a, period);
        ta = a;
        this.moveEvent = new TruckMove(ta.getData());
        this.period = period;
    }

    /**
     * determine dx and dy per move and eta based on current speed and location
     */
    private void initMove() {
        // bepaal totale dx/dy
        double x = Math.abs(l.getLatitude() - ta.getData().getTo().getLocation().getLatitude());
        double y = Math.abs(l.getLongitude() - ta.getData().getTo().getLocation().getLongitude());
        // bepaal het aantal stappen
        double steps = getDistance() / ta.getData().getSpeed();
        // bepaal nu de nodige dx dy
        dx = x / steps;
        dy = x / steps;
        if (l.getLatitude() > ta.getData().getTo().getLocation().getLatitude()) {
            dx = -dx;
        }
        if (l.getLongitude() > ta.getData().getTo().getLocation().getLongitude()) {
            dy = -dy;
        }
        Calendar c = Calendar.getInstance();
        int i = (int) (steps * period * 1000);
        c.add(Calendar.MILLISECOND, i / 1000);
        ta.getData().setEta(c);
        ta.log.debug(String.format("dist %e, speed %e, steps %e, dx %e, dy %e", getDistance(), ta.getData().getSpeed(), steps, dx, dy));
    }
    
    private double getDistance() {
        return l.getDistance(ta.getData().getTo().getLocation());
    }

    @Override
    public void onStart() {
        super.onStart();
        ta.gone();
        l = ta.getData().getFrom().getLocation();
        ta.notify("driving from " + ta.getData().getFrom().getId().getLocalName() + " to " + ta.getData().getTo().getId().getLocalName());
        TruckLeft tl = new TruckLeft(ta.getData());
        ta.notify(tl);
    }

    @Override
    protected void onTick() {
        int n = Main.showRandomInteger(1, 5, r);
        if (ta.getData().getSpeed()==0.1 && getTickCount()>0 && getTickCount()%n==0) {
            double d = r.nextDouble()/4;
            if (d > 0.02) {
                ta.getData().setSpeed(d);
                ta.log.debug(ta.getAID().getLocalName() + "speed changed to " + ta.getData().getSpeed());
            }
        }
        initMove();
        driven += ta.getData().getSpeed();
        l.setLatitude(l.getLatitude() + dx);
        l.setLongitude(l.getLongitude() + dy);
        moveEvent.getState().setLocation(l);
        moveEvent.getState().setToGo(getDistance());
        ta.notify(moveEvent);
        if (driven >= getDistance()) {
            stop();
        }
    }

    @Override
    public int onEnd() {
        return TruckState.DONE;
    }
}
