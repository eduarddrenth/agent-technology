/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.order;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import org.logica.cns.flora.gui.OrderBought;
import org.logica.cns.flora.model.predicates.BuyOrder;
import org.logica.cns.generic.CNSException;

/**
 *
 * @author eduard
 */
class Waiting extends AchieveREResponder {
    OrderAgent oa;

    public Waiting(Agent a, MessageTemplate mt) {
        super(a, mt);
        oa = (OrderAgent) a;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        try {
            ContentElement ce = myAgent.getContentManager().extractContent(request);
            ACLMessage resp = request.createReply();
            if (ce instanceof BuyOrder) {
                ((OrderAgent)myAgent).buy(((BuyOrder) ce).getSlaLocation());
                resp.setPerformative(ACLMessage.INFORM);
                ((OrderAgent)myAgent).getData().setBuyer(request.getSender());
                ((BuyOrder)ce).setOrder(((OrderAgent)myAgent).getData());
                myAgent.getContentManager().fillContent(resp, ce);
                oa.send(resp);
                
                OrderBought bought = new OrderBought(oa.getData());
                oa.notify(bought);
                
                myAgent.addBehaviour(new Bought(myAgent, MessageTemplate.or(
                    AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_QUERY),
                    AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST))));

                reset(MessageTemplate.MatchOntology(JADEManagementOntology.NAME));
                
            }
            return resp;
        } catch (CodecException ex) {
            throw new CNSException("uanble to parse request", ex);
        } catch (UngroundedException ex) {
            throw new CNSException("uanble to parse request", ex);
        } catch (OntologyException ex) {
            throw new CNSException("uanble to parse request", ex);
        }
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        try {
            ACLMessage resp = request.createReply();
            resp.setPerformative(ACLMessage.REFUSE);
            ContentElement ce = myAgent.getContentManager().extractContent(request);
            if (ce instanceof BuyOrder) {
                resp.setPerformative((((OrderAgent)myAgent).getData().getBuyer()!=null) ? ACLMessage.REFUSE : ACLMessage.AGREE);
            }
            return resp;
        } catch (CodecException ex) {
            throw new CNSException("uanble to parse request", ex);
        } catch (UngroundedException ex) {
            throw new CNSException("uanble to parse request", ex);
        } catch (OntologyException ex) {
            throw new CNSException("uanble to parse request", ex);
        }
    }
    
}
