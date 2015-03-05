package org.logica.cns_workshop;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSAgentInitializerImpl;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.CNSReceiveMessagesBehavior;
import org.logica.cns_workshop.communication.SmileyOntology;

/**
 *
 * @author Eduard Drenth: Logica, 21-dec-2009 
 *         Wico Mulder  : Logica, 21-dec-2009
 * 
 */
public abstract class WorkshopAgent extends Agent {

    private static final long serialVersionUID = 1L;
    public static final String TYPE = "WORKSHOP";

    @Override
    protected void setup() {
        super.setup();
        CNSAgentInitializer init = new CNSAgentInitializerImpl(this);
        init.addOntology(SmileyOntology.getInstance());
        init.addLanguage(new SLCodec());
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
        fillDescription(init);
        init.setMessageReceiver(new CNSReceiveMessagesBehavior(this, getMessageHandler()));
        init.initializeAgent();
    }

    protected abstract void fillDescription(CNSAgentInitializer init);

    protected abstract CNSMessageHandler getMessageHandler();

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(WorkshopAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




}
