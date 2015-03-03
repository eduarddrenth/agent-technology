package com.logica.amc.base;

import jade.content.Predicate;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSAgentInitializerImpl;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.CNSMessageHandlerImpl;
import org.logica.cns.generic.CNSReceiveMessagesBehavior;

/**
 *
 * @author Eduard Drenth: Logica, 17-nov-2009
 * 
 */
public abstract class AMCAgent extends Agent {


    protected void fillOntology(CNSAgentInitializer init) {
        
    }

    protected class AMCHandler extends CNSMessageHandlerImpl {
        @Override
        protected void handleContent(Predicate predicate, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
            if (predicate instanceof StatusIs) {
                handleStatus((StatusIs) predicate);
            } else {
                super.handleContent(predicate, msg);
            }
        }
    }

    protected  CNSMessageHandler getMessageHandler() {
            return new AMCHandler();
    }

    @Override
    protected void setup() {
        super.setup();
        context = CNSContext.fromArgs((String[]) ((getArguments() == null) ? new String[]{} : getArguments()));
        CNSAgentInitializer init = new CNSAgentInitializerImpl(this);
        init.addOntology(AMCOntology.getInstance());
        fillOntology(init);
        init.addLanguage(new SLCodec());
        init.addServiceDescription(CNSHelper.createServiceDescription(AMCTYPE, AMCTYPE));
        fillDescription(init);
        init.setMessageReceiver(new CNSReceiveMessagesBehavior(this, getMessageHandler()));
        init.initializeAgent();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(AMCAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void fillDescription(CNSAgentInitializer init);

    public static final String AMCTYPE = "AMC monitoring";


    protected abstract void handleStatus(StatusIs status);

}
