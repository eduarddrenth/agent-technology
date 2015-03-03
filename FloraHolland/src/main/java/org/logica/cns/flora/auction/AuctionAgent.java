/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.auction;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import java.util.Random;
import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.gui.AuctionLocation;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.predicates.GiveFollowUpChance;
import org.logica.cns.flora.order.OrderAgent;
import org.logica.cns.flora.truck.TruckAgent;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author eduard
 */
public class AuctionAgent extends FloraAgent<Auction> {

    @Override
    protected void setup() {
        super.setup();
        Auction a = new Auction();
        a.setId(getAID());
        setData(a);
        getContentManager().registerOntology(JADEManagementOntology.getInstance());
        
        AuctionLocation al = new AuctionLocation(a);
        notify(al);
        
        addBehaviour(new AchieveREResponder(this, AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_QUERY)) {
            public static final String RANDOMCHANCE = "RANDOMCHANCE";
            
            private int myOrders = 0;

            @Override
            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
                try {
                    ContentElement ce = getContentManager().extractContent(request);
                    if (ce instanceof GiveFollowUpChance) {
                        ACLMessage resp = request.createReply();
                        resp.setPerformative(ACLMessage.INFORM);
                        resp.setContent(String.valueOf(myOrders));
                        return resp;
                    }
                    return super.prepareResultNotification(request, response);
                } catch (CodecException ex) {
                    throw new CNSException("uanble to parse request", ex);
                } catch (UngroundedException ex) {
                    throw new CNSException("uanble to parse request", ex);
                } catch (OntologyException ex) {
                    throw new CNSException("uanble to parse request", ex);
                } catch (FIPAException ex) {
                    throw new CNSException("uanble to parse request", ex);
                }
            }

            @Override
            protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
                try {
                    ACLMessage resp = request.createReply();
                    ServiceDescription[] criteria = new ServiceDescription[] {
                        CNSHelper.createServiceDescription(OrderAgent.class.getSimpleName(), OrderAgent.class.getSimpleName()),
                        CNSHelper.createServiceDescription(((AuctionAgent)myAgent).getData().getId().getLocalName(), LOCATION_TYPE.FROM.toString())
                    };
                    AID[] or = CNSHelper.findAgents(myAgent, criteria);
                    myOrders = (or!=null)?or.length:0;
                    
                    criteria = new ServiceDescription[] {
                        CNSHelper.createServiceDescription(TruckAgent.class.getSimpleName(), TruckAgent.class.getSimpleName()),
                        CNSHelper.createServiceDescription(((AuctionAgent)myAgent).getData().getId().getLocalName(), LOCATION_TYPE.TO.toString())
                    };
                    
                    AID[] tr = CNSHelper.findAgents(myAgent, criteria);
                    ((AuctionAgent)myAgent).notify("orders at auction " + myAgent.getLocalName() + " " + myOrders);
                    ((AuctionAgent)myAgent).notify("trucks towards auction " + myAgent.getLocalName() + " " + (tr!=null?tr.length:0));
                    
                    /* hier kun je iets slims bedenken met de combinatie van aantal orders ter plekke en aantal trucks onderweg
                     * 
                     * bijvoorbeeld configureerbaar via een setting
                     * 

                     */
                    
                    String random = JadeHelper.getProperty(RANDOMCHANCE);
                    if (random!=null&&Boolean.parseBoolean(random)) {
                        myOrders = new Random().nextInt(100);                        
                    } else {
                        myOrders = myOrders - ((tr!=null)?tr.length:0);
                    }

                    resp.setPerformative(ACLMessage.AGREE);
                    return resp;
                } catch (FIPAException ex) {
                    throw new CNSException("unable to find orders");
                }
            }
                        
        });

    }



}
