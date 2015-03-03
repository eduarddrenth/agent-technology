/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import java.util.Iterator;
import org.logica.cns.flora.model.concepts.Order;
import org.logica.cns.flora.model.concepts.Truck;

/**
 *
 * @author eduard
 */
public class TruckMove extends TruckEvent {

    public TruckMove(Truck state) {
        super(state);
    }


    @Override
    public EVENTTYPE getEventType() {
        return EVENTTYPE.TRUCKMOVE;
    }

    @Override
    public String toString() {
        String orders = "";
        for (Iterator it = getState().getLoad().iterator(); it.hasNext();) {
            Order o = (Order) it.next();
            orders += o.getId().getLocalName();
            if (it.hasNext()) {
                orders += ",";
            }
        }
        return super.toString() + String.format("distleft=%f\neta=%tT\nspeed=%f\norders=%s\ndestination=%s\n",
                getState().getToGo(),
                getState().getEta(),
                getState().getSpeed(),
                orders,
                getState().getTo().getId().getLocalName());
    }
    
        
}
