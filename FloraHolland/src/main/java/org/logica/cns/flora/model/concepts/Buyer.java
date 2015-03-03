/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.onto.annotations.Slot;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

/**
 *
 * @author eduard
 */
public class Buyer extends AgentState {
    
    private Auction slaLocation;
    private List bought = new ArrayList();

    @Slot(mandatory=true)
    public Auction getSlaLocation() {
        return slaLocation;
    }

    public void setSlaLocation(Auction slaLocation) {
        this.slaLocation = slaLocation;
    }

    public List getBought() {
        return bought;
    }

    public void setBought(List bought) {
        this.bought = bought;
    }
    
    

}
