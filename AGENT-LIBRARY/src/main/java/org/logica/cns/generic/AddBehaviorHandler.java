package org.logica.cns.generic;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Adds a behavior when one or more agents are found based on search criteria.
 * @see CNSHelper#executeWhenFound(jade.core.Agent, org.logica.cns.generic.ServiceFoundHandler, jade.domain.FIPAAgentManagement.ServiceDescription[]) 
 * @author Eduard Drenth: Logica, 31-mei-2010
 * 
 */
public class AddBehaviorHandler implements ServiceFoundHandler {

    private static final Log log = LogFactory.getLog(AddBehaviorHandler.class);

    private Behaviour b;

    public AddBehaviorHandler(Behaviour b) {
        this.b = b;
    }

    @Override
    public void handleFound(Agent a, DFAgentDescription[] dads) {
        a.addBehaviour(b);
    }

    @Override
    public void handleFIPAException(FIPAException ex) {
        log.error("", ex);
    }

}
