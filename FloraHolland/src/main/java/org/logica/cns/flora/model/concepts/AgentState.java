/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

/**
 *
 * @author eduard
 */
public class AgentState implements Concept {

    private AID id;

    @Slot(mandatory=true)
    public AID getId() {
        return id;
    }

    public void setId(AID id) {
        this.id = id;
    }

}
