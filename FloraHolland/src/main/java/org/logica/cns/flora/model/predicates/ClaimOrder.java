/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.model.predicates;

import jade.content.Predicate;
import org.logica.cns.flora.model.concepts.Order;

/**
 *
 * @author eduard
 */
public class ClaimOrder implements Predicate {
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
    
    
}
