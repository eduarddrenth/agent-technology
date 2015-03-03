/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.model.predicates;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import org.logica.cns.flora.model.concepts.Order;

/**
 *
 * @author eduard
 */
public class OrderArrived implements Predicate {
    private Order order;

    @Slot(mandatory=true)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
    
}
