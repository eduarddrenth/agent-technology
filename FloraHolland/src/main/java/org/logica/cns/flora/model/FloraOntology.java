/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import org.logica.cns.flora.model.concepts.AgentState;
import org.logica.cns.flora.model.predicates.ClaimOrder;
import org.logica.cns.generic.CNSException;

/**
 *
 * @author eduard
 */
public class FloraOntology extends BeanOntology {
    
    public static final String NAME = "FloraOntology";

    private static final FloraOntology instance = new FloraOntology(NAME);

    private FloraOntology(String name) {
        super(name);
        try {
            add(AgentState.class.getPackage().getName());
            add(ClaimOrder.class.getPackage().getName());

        } catch (BeanOntologyException ex) {
            throw new CNSException("fault in datamodel", ex);
        }
        
    }

    public static FloraOntology getInstance() {
        return instance;
    }
}
