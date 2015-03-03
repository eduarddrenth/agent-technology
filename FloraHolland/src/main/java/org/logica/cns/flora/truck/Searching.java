/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.truck;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
import org.logica.cns.flora.FloraAgent.LOCATION_TYPE;
import org.logica.cns.flora.auction.AuctionAgent;
import org.logica.cns.flora.model.FloraOntology;
import org.logica.cns.flora.model.concepts.Order;
import org.logica.cns.flora.model.predicates.GiveFollowUpChance;
import org.logica.cns.flora.model.predicates.GiveSize;
import org.logica.cns.flora.order.OrderAgent;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.CNSHelper;

/**
 *
 * @author eduard
 */
public class Searching extends FSMBehaviour {

    public static final int ORDERS_CHOSEN = 1;
    public static final int ROUTE_FOUND = 2;
    public static final int FAILURE = 3;
    public static final String CHOOSE_TRAJECT = "CHOOSE_TRAJECT";
    public static final String CHOOSE_ORDERS = "CHOOSE_ORDERS";
    public static final String CLAIM_ORDERS = "CLAIM_ORDERS";
    private AID destination = null;
    private ArrayList<Order> orders = new ArrayList<Order>();
    
    TruckAgent ta;

    /**
     * Searching where to go with which orders. We implement this as a statemachine with 3 phases: choosing a route, choosing orders, claiming orders.
     * <ul>
     * <li>Choosing a route is asking the auctions for the probability to get a follow-up mission. The auction with the highest probability is chosen.</li>
     * <li>Choosing orders is searching orders for the chosen </li>
     * <li></li>
     * </ul>
     * @param a
     * @throws FIPAException 
     */
    public Searching(TruckAgent a) {
        super(a);
        ta = a;

        registerFirstState(new ChooseTraject(myAgent, new ACLMessage(ACLMessage.REQUEST)), CHOOSE_TRAJECT);
        registerState(new ChooseOrder(myAgent, new ACLMessage(ACLMessage.REQUEST)), CHOOSE_ORDERS);
        registerState(new ClaimOrder(myAgent, new ACLMessage(ACLMessage.REQUEST)), CLAIM_ORDERS);
        registerLastState(new Finished(), TruckState.FINISHED);

        registerTransition(CHOOSE_TRAJECT, CHOOSE_ORDERS, ROUTE_FOUND);
        registerTransition(CHOOSE_TRAJECT, CHOOSE_TRAJECT, FAILURE);
        registerDefaultTransition(CHOOSE_ORDERS, CLAIM_ORDERS);
        registerDefaultTransition(CLAIM_ORDERS, TruckState.FINISHED);
    }

    class Finished extends OneShotBehaviour {

        @Override
        public void action() {
        }

    }
    
    @Override
    protected void handleInconsistentFSM(String current, int event) {
        if (!TruckState.FINISHED.equals(current)) {
            super.handleInconsistentFSM(current, event);
        }
    }

    ACLMessage createMsg(AID receivers, int perfomative, String protocol) {
        return createMsg(new AID[] {receivers}, perfomative, protocol);
    }
    ACLMessage createMsg(AID[] receivers, int perfomative, String protocol) {
        ACLMessage msg = CNSHelper.createMessage(
                receivers,
                FloraOntology.NAME,
                FIPANames.ContentLanguage.FIPA_SL,
                perfomative);
        msg.setProtocol(protocol);
        return msg;
    }

    @Override
    public int onEnd() {
        if (orders.size() == 0 || destination == null) {
            return TruckState.NOTHING_FOUND;
        }
        return TruckState.DONE;
    }

    class ChooseTraject extends AchieveREInitiator {

        private int numSend = 0;

        public ChooseTraject(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        public void reset() {
            super.reset(new ACLMessage(ACLMessage.REQUEST));
        }

        
        @Override
        protected Vector prepareRequests(ACLMessage request) {
            try {
                destination = null;
                AID[] auctions = CNSHelper.findAgents(myAgent, AuctionAgent.class.getSimpleName());
                numSend = 0;
                Vector v = new Vector(auctions.length);
                for (int i = 0; i < auctions.length; i++) {
                    if (auctions[i].equals(((TruckAgent)myAgent).getData().getFrom().getId())) {
                        continue;
                    }
                    numSend++;
                    ACLMessage chooseTraject = createMsg(
                            auctions[i],
                            ACLMessage.QUERY_REF,
                            FIPANames.InteractionProtocol.FIPA_QUERY);
                    chooseTraject.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                    myAgent.getContentManager().fillContent(chooseTraject, new GiveFollowUpChance());
                    v.add(chooseTraject);
                }
                return v;
            } catch (OntologyException ex) {
                throw new CNSException("unable to prepare message", ex);
            } catch (CodecException ex) {
                throw new CNSException("unable to prepare message", ex);
            } catch (FIPAException ex) {
                throw new CNSException("unable to prepare message", ex);
            }
        }

        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            if (resultNotifications.size() < numSend) {
                ta.log.debug("resp " + resultNotifications.size() + " " + numSend);
            } else {
                int chance = 0;
                for (Object msg : resultNotifications) {
                    ACLMessage message = (ACLMessage) msg;
                    ta.log.debug("chance " + message.getContent());
                    if (Integer.parseInt(message.getContent()) > chance) {
                        chance = Integer.parseInt(message.getContent());
                        destination = message.getSender();
                    }
                }
                if (destination!=null) {
                    ta.notify("most likely follow-up for destination: " + destination.getLocalName());
                }
            }
        }

        @Override
        protected void handleOutOfSequence(ACLMessage msg) {
            ta.log.warn("out " + msg.getContent());
        }


        @Override
        public int onEnd() {
            if (destination != null) {
                return ROUTE_FOUND;
            } else {
                ta.log.debug(myAgent.getLocalName() + " again choosing traject");
                again();
                return FAILURE;
            }
        }
    }

    private void again() {
        orders.clear();
        resetChildren();
    }

    class ChooseOrder extends AchieveREInitiator {
        private AID[] or = null;

        @Override
        public void reset() {
            super.reset(new ACLMessage(ACLMessage.REQUEST));
        }

        public ChooseOrder(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            if (resultNotifications.size() > 0) {
                for (Object object : resultNotifications) {
                    ACLMessage msg = (ACLMessage) object;
                    if (orders.size() < 3) {
                        try {
                            ContentElement ce = ta.getContentManager().extractContent(msg);
                            orders.add(((GiveSize)ce).getOrder());
                        } catch (CodecException ex) {
                            throw new CNSException("unable to parse order",ex);
                        } catch (UngroundedException ex) {
                            throw new CNSException("unable to parse order",ex);
                        } catch (OntologyException ex) {
                            throw new CNSException("unable to parse order",ex);
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        @Override
        protected Vector prepareRequests(ACLMessage request) {
            try {
                ServiceDescription[] criteria = new ServiceDescription[] {
                    CNSHelper.createServiceDescription(OrderAgent.class.getSimpleName(), OrderAgent.class.getSimpleName()),
                    CNSHelper.createServiceDescription(destination.getLocalName(), LOCATION_TYPE.TO.toString()),
                    CNSHelper.createServiceDescription(((TruckAgent)myAgent).getData().getFrom().getId().getLocalName(), LOCATION_TYPE.FROM.toString())
                };
                or = CNSHelper.findAgents(myAgent, criteria);
                if (or == null || or.length == 0) {
                    ta.notify("no orders found for " + destination.getLocalName());
                    return new Vector();
                } else {
                    Vector v = new Vector(or.length);
                    ta.notify("asking size for orders " + Arrays.asList(or));
                    for (AID aid : or) {
                        ACLMessage getSize = createMsg(
                                aid,
                                ACLMessage.QUERY_REF,
                                FIPANames.InteractionProtocol.FIPA_QUERY);
                        getSize.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                        myAgent.getContentManager().fillContent(getSize, new GiveSize());
                        v.add(getSize);
                    }
                    return v;
                }
            } catch (OntologyException ex) {
                throw new CNSException("unable to find orders", ex);
            } catch (CodecException ex) {
                throw new CNSException("unable to find orders", ex);
            } catch (FIPAException ex) {
                throw new CNSException("unable to find orders", ex);
            }
        }

        @Override
        protected void handleOutOfSequence(ACLMessage msg) {
            super.handleOutOfSequence(msg);
        }

        
        @Override
        public int onEnd() {
            if (orders.size() > 0 && destination != null) {
                ta.notify("orders chosen: " + orders);
                return ORDERS_CHOSEN;
            } else {
                ta.log.debug(myAgent.getLocalName() + " stop choosing");
                return FAILURE;
            }
        }
    }

    class ClaimOrder extends AchieveREInitiator {

        public ClaimOrder(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        public void reset() {
            super.reset(new ACLMessage(ACLMessage.REQUEST));
        }
        
        @Override
        protected void handleAllResultNotifications(Vector resultNotifications) {
            if (resultNotifications.size() > 0) {
                for (Object object : resultNotifications) {
                    ACLMessage msg = (ACLMessage) object;
                    try {
                        ContentElement ce = ta.getContentManager().extractContent(msg);
                        orders.add(((org.logica.cns.flora.model.predicates.ClaimOrder)ce).getOrder());
                    } catch (CodecException ex) {
                        throw new CNSException("unable to parse order",ex);
                    } catch (UngroundedException ex) {
                        throw new CNSException("unable to parse order",ex);
                    } catch (OntologyException ex) {
                        throw new CNSException("unable to parse order",ex);
                    }
                }
            }
        }

        @Override
        protected Vector prepareRequests(ACLMessage request) {
            try {
                Vector v = new Vector(orders.size());
                ta.notify("claiming orders " + orders);
                for (Order order : orders) {
                    ACLMessage getSize = createMsg(
                            order.getId(),
                            ACLMessage.REQUEST,
                            FIPANames.InteractionProtocol.FIPA_REQUEST);
                    getSize.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                    myAgent.getContentManager().fillContent(getSize, new org.logica.cns.flora.model.predicates.ClaimOrder());
                    v.add(getSize);
                }
                orders.clear();
                return v;
            } catch (OntologyException ex) {
                throw new CNSException("unable to find orders", ex);
            } catch (CodecException ex) {
                throw new CNSException("unable to find orders", ex);
            }
        }

        @Override
        public int onEnd() {
            if (orders.size() > 0) {
                ta.notify("claimed: " + orders);
            } else {
                ta.log.debug(myAgent.getLocalName() + " stop claiming");
            }
            return 0;
        }
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public AID getDestination() {
        return destination;
    }
    
}
