/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.gui;

import java.util.Calendar;
import org.logica.cns.flora.model.concepts.AgentState;


/**
 *
 * @author eduard
 */
public abstract class AbsFloraEvent<S extends AgentState> implements FloraEvent<S> {
    private S state;
    private String msg = "";
    private int sequence = 0;

    public AbsFloraEvent(S state) {
        this.state = state;
    }
    
    @Override
    public S getState() {
        return state;
    }

    @Override
    public String toString() {
        return String.format("source=%s\nsequence=%d\nevent=%s\nobject=%s\ntime=%tT\nlatitude=%f\nlongitude=%f\n",
                getState().getId().getLocalName(),
                getSequence(),
                getEventType().toString(),
                getObjectType().toString(),
                Calendar.getInstance(),
                getLocation().getLatitude(),
                getLocation().getLongitude());
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
        
}
