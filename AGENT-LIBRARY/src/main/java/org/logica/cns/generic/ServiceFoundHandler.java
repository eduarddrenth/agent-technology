package org.logica.cns.generic;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

/**
 * implementors can be used in {@link CNSHelper#executeWhenFound(jade.core.Agent, org.logica.cns.generic.ServiceFoundHandler, jade.domain.FIPAAgentManagement.ServiceDescription[]) }
 * to perform some action when a combination of {@link DFAgentDescription}s is found in the DF (yellow pages).
 * @author Eduard Drenth: Logica, 31-mei-2010
 *
 */
public interface ServiceFoundHandler {

    /**
     * this will be called when the DF found a requested combination of {@link DFAgentDescription}s.
     * @param a the agent that was notified by the DF
     * @param dads The {@link DFAgentDescription}s found in the DF
     */
    public void handleFound(Agent a, DFAgentDescription[] dads);

    /**
     * 
     * @param ex 
     */
    public void handleFIPAException(FIPAException ex);

}
