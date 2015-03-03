package org.logica.cns.generic;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Superclass of the agents of the CNS project. <br>
 * @todo In the future no superclass agent should be forced, this will make use of the CNS library in combination with other libraries easier
 * @deprecated 
 * @author wmulder
 * @author Eduard Drenth
 * 
 */
public abstract class CNSAgent extends Agent {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(CNSAgent.class);
    /**
     * String denoting the type of the services this agent offers, each agent should offer just 1 type of services.
     * All agents should register their services using the correct type
     */
    public static final String TYPE = "CNS";


    /**
     * calls {@link CNSAgentInitializer#initializeAgent(org.logica.cns.generic.CNSAgent) }.
     * @throws IllegalStateException if {@link CNSContext} is not set
     */
    @Override
    protected final void setup() {
        super.setup();
        CNSAgentInitializer init = new CNSAgentInitializerImpl(this);

        init.addLanguage(new SLCodec());
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE, getContentManager().getOntologyNames(), getContentManager().getLanguageNames()));
        
        init.setMessageReceiver(new CNSReceiveMessagesBehavior(this, new CNSMessageHandlerImpl()));

        /*
         * end for compatibility
         */
        init.initializeAgent();
    }

    /**
     * deregister agent from df
     */
    @Override
    public void takeDown() {
        // When an agent terminates it is a good practice to de-register
        // published
        // services.
        super.takeDown();
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            log.warn("unable to deregister " + getName(), fe);
        }
        // Printout a dismissal message
        log.info(getLocalName() + " terminating.");
    }

}
