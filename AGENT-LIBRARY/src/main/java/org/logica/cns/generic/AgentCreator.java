/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.generic;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

/**
 *
 * @author eduard
 */
public class AgentCreator extends SimpleAchieveREInitiator {

    private Exception e = null;
    private String created = null;
    private boolean agreed = false, refused = false;

    public AgentCreator(Agent a, ACLMessage msg) {
        this(a, msg, null);
    }

    public AgentCreator(Agent a, ACLMessage msg, DataStore store) {
        super(a, msg, store);
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        refused=true;
        super.handleRefuse(refuse);
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        agreed=true;
        super.handleAgree(agree);
    }

    @Override
    protected final void handleInform(ACLMessage inform) {
        try {
            Done d = (Done) myAgent.getContentManager().extractContent(inform);
            Action a = (Action) d.getAction();
            CreateAgent ca = (CreateAgent) a.getAction();
            created = ca.getAgentName();
        } catch (CodecException ex) {
            e=ex;
        } catch (OntologyException ex) {
            e=ex;
        }
    }
    
    /**
     * returns the name of the agent when created, null when not created yet. Throws a {@link CNSException} when creation was refused.
     * @return
     * @throws jade.content.lang.Codec.CodecException when decoding the inform response failed
     * @throws OntologyException when decoding the inform response failed
     */
    public final String getAgentName() throws CodecException, OntologyException {
        if (created!=null) {
            return created;
        } else {
            if (agreed) {
                // wait for inform
                return null;
            } else  if (refused) {
                // refused
                throw new CNSException("refused creation");
            } else if (e!=null) {
                // trouble handling inform
                if (e instanceof CodecException) {
                    throw (CodecException)e;
                } else if (e instanceof OntologyException) {
                    throw (OntologyException)e;
                } else if (e instanceof UngroundedException) {
                    throw (UngroundedException)e;
                }
            } else {
                // not agreed, not refused, no exception...wait longer
                return null;
            }
            return created;
        }        
    }
}
