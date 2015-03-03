/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import org.logica.cns.flora.model.concepts.Location;
import org.logica.cns.flora.model.concepts.Order;

/**
 *
 * @author eduard
 */
public abstract class OrderEvent extends AbsFloraEvent<Order> {

    public OrderEvent(Order state) {
        super(state);
    }
    
    
    @Override
    public OBJECTTYPE getObjectType() {
        return OBJECTTYPE.ORDER;
    }
        
    @Override
    public Location getLocation() {
        return getState().getFrom().getLocation();
    }
}
