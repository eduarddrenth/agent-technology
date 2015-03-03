package org.logica.cns.generic;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.Predicate;
import jade.content.abs.AbsAgentAction;
import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Eduard Drenth: Logica, 15-mrt-2010
 * 
 */
public class CNSMessageHandlerImpl implements CNSMessageHandler {

    private static final Log log = LogFactory.getLog(CNSMessageHandlerImpl.class);

    /**
     * this method figures out what we are dealing with and calls the appropriate method
     * @param msg
     * @param a the agent for who we handle this message
     * @see #handleContent(jade.content.AgentAction)
     * @see #handleContent(jade.content.ContentElementList)
     * @see #handleContent(jade.content.Predicate)
     */
    @Override
    public void handleACLMessage(ACLMessage msg, Agent a) throws CodecException, UngroundedException, OntologyException {
        ContentElement ce = null;
        try {
            ce = a.getContentManager().extractContent(msg);
        } catch (UngroundedException ontologyException) {
            if (log.isWarnEnabled()) {
                log.warn("error, trying abstract content" + msg.getContent(), ontologyException);
            }
            ce = a.getContentManager().extractAbsContent(msg);
        }
        if (ce instanceof AbsIRE) {
            handleContent((AbsIRE) ce,msg);
        } else if (ce instanceof AbsAgentAction) {
            handleContent((AbsAgentAction) ce,msg);
        } else if (ce instanceof AbsPredicate) {
            handleContent( (AbsPredicate) ce,msg);
        } else if (ce instanceof AbsContentElementList) {
            handleContent((AbsContentElementList) ce, msg);
        } else if (ce instanceof Action) {
            handleContent((Action) ce, msg);
        } else if (ce instanceof AgentAction) {
            handleContent((AgentAction) ce, msg);
        } else if (ce instanceof Predicate) {
            handleContent((Predicate) ce, msg);
        } else if (ce instanceof ContentElementList) {
            handleContent((ContentElementList) ce, msg);
        } else {
            log.warn("content not supported: " + ce.getClass().getName());
        }
    }

    protected void handleContent(AbsIRE absIRE, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void handleContent(Action action, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void handleContent(AgentAction action, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void handleContent(AbsAgentAction action, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void handleContent(AbsPredicate action, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void handleContent(AbsContentElementList action, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * not abstract because this method does some helpfull analyzing of Predicates received
     * @param predicate
     * @param msg
     */
    protected void handleContent(Predicate predicate, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        if (predicate instanceof Result) {
            Result r = (Result) predicate;
            if (r.getAction() instanceof Action) {
                log.warn(getClass().getName() + " not dealing with results: " + r.getItems() + " for " + ((Action)r.getAction()).getAction());
            } else {
                log.warn(getClass().getName() + " not dealing with results: " + r.getItems() + " for " + r.getAction());
            }
        } else if (predicate instanceof Done) {
            Done d = (Done) predicate;
            Concept c = d.getAction();
            if (c instanceof Action) {
                Action a = (Action) c;
                Concept con = a.getAction();
                if (con instanceof CreateAgent) {
                    log.info("Agent Created: " + ((CreateAgent) con).getAgentName());
                } else if (con instanceof KillAgent) {
                    log.info("Agent Killed: " + ((KillAgent) con).getAgent().getName());
                } else {
                    log.warn(getClass().getName() + " does not supported yet: " + con.getClass().getName());
                }
            } else {
                log.warn(c.getClass().getName());
            }
        } else {
            log.warn(getClass().getName() + " does  supported yet: " + predicate.getClass().getName());
        }
    }

    protected void handleContent(ContentElementList list, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
