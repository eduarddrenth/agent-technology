/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Location;
import org.logica.cns.flora.model.concepts.Truck;

/**
 *
 * @author eduard
 */
public abstract class TruckEvent extends AbsFloraEvent<Truck> {

    public TruckEvent(Truck state) {
        super(state);
    }
    
    @Override
    public OBJECTTYPE getObjectType() {
        return OBJECTTYPE.TRUCK;
    }
        
    @Override
    public Location getLocation() {
        return getState().getLocation();
    }
}
