/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.model.predicates;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Order;

/**
 *
 * @author eduard
 */
public class BuyOrder implements Predicate {
    
    private Auction slaLocation;
    private Order order;

    @Slot(mandatory=true)
    public Auction getSlaLocation() {
        return slaLocation;
    }

    public void setSlaLocation(Auction slaLocation) {
        this.slaLocation = slaLocation;
    }

    @Slot(position=1)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
    
}
