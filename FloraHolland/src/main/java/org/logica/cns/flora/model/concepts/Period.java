/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.model.concepts;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import java.util.Calendar;

/**
 *
 * @author eduard
 */
public class Period implements Concept {
    private Calendar from, to;

    @Slot(mandatory=true)
    public Calendar getFrom() {
        return from;
    }

    public void setFrom(Calendar from) {
        this.from = from;
    }

    @Slot(mandatory=true)
    public Calendar getTo() {
        return to;
    }

    public void setTo(Calendar to) {
        this.to = to;
    }
    
}
