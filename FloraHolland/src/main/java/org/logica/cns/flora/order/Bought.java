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
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import org.logica.cns.flora.gui.OrderLeft;
import org.logica.cns.flora.gui.OrderTooLate;
import org.logica.cns.flora.model.predicates.ClaimOrder;
import org.logica.cns.flora.model.predicates.GiveSize;
import org.logica.cns.flora.model.predicates.OrderArrived;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author eduard
 */
class Bought extends AchieveREResponder {
    
    OrderAgent oa;

    @Override
    public void onStart() {
        super.onStart();
        if (!oa.getData().isTooLate()) {
            oa.addBehaviour(new WakerBehaviour(myAgent, Long.parseLong(JadeHelper.getProperty("ORDERTIMEOUT"))) {

                @Override
                protected void onWake() {
                    if (!oa.getData().isTooLate()&&oa.getData().getTransporter()==null) {
                        oa.getData().setTooLate(true);
                        OrderTooLate otl = new OrderTooLate(oa.getData());
                        oa.notify(otl);
                    }
                }

            });
        }
    }

    public Bought(Agent a, MessageTemplate mt) {
        super(a, mt);
        oa=(OrderAgent) a;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        try {
            ContentElement ce = myAgent.getContentManager().extractContent(request);
            ACLMessage resp = request.createReply();
            if (ce instanceof GiveSize) {
                resp.setPerformative(ACLMessage.INFORM);
                ((GiveSize) ce).setOrder(((OrderAgent)myAgent).getData());
                myAgent.getContentManager().fillContent(resp, ce);
            } else if (ce instanceof ClaimOrder) {
                resp.setPerformative(ACLMessage.INFORM);
                ((OrderAgent)myAgent).getData().setTransporter(request.getSender());
                ((ClaimOrder) ce).setOrder(((OrderAgent)myAgent).getData());
                myAgent.getContentManager().fillContent(resp, ce);
                OrderLeft left = new OrderLeft(oa.getData());
                oa.notify(left);
                oa.gone();
            } else if (ce instanceof OrderArrived) {
                org.logica.cns.flora.gui.OrderArrived oar = new org.logica.cns.flora.gui.OrderArrived(((OrderArrived)ce).getOrder());
                oa.notify(oar);
                oa.arrive(oar.getState().getTo());
            }
            return resp;
        } catch (CodecException ex) {
            throw new CNSException("uanble to parse request", ex);
        } catch (UngroundedException ex) {
            throw new CNSException("uanble to parse request", ex);
        } catch (OntologyException ex) {
            ex.printStackTrace();
            throw new CNSException("uanble to parse request", ex);
        }
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        try {
            ACLMessage resp = request.createReply();
            ContentElement ce = myAgent.getContentManager().extractContent(request);
            resp.setPerformative(ACLMessage.REFUSE);
            if (!oa.getData().isTooLate()) {
                if (ce instanceof ClaimOrder) {
                    resp.setPerformative((((OrderAgent)myAgent).getData().getTransporter()!=null) ? ACLMessage.REFUSE : ACLMessage.AGREE);
                } else if (ce instanceof GiveSize) {
                    resp.setPerformative((((OrderAgent)myAgent).getData().getTransporter()!=null) ? ACLMessage.REFUSE : ACLMessage.AGREE);
                } else if (ce instanceof OrderArrived) {
                    resp.setPerformative((((OrderAgent)myAgent).getData().getTransporter()!=null) ? ACLMessage.AGREE : ACLMessage.REFUSE);
                }
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
