/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.buyer;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.Main;
import org.logica.cns.flora.gui.BuyerBought;
import org.logica.cns.flora.gui.BuyerLocation;
import org.logica.cns.flora.model.FloraOntology;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Buyer;
import org.logica.cns.flora.model.predicates.BuyOrder;
import org.logica.cns.flora.order.OrderAgent;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author eduard
 */
public class BuyerAgent extends FloraAgent<Buyer> {
    public static final int END = Integer.parseInt(JadeHelper.getProperty("BUYEND"));
    public static final int START = Integer.parseInt(JadeHelper.getProperty("BUYSTART"));
    
    Random r = new Random();
    private List<AID> asked = new ArrayList<AID>();

    @Override
    protected void setup() {
        super.setup();
        
        Auction auction = parseArguments();
        
        Buyer b = new Buyer();
        b.setId(getAID());
        b.setSlaLocation(auction);
        setData(b);
        
        BuyerLocation bl = new BuyerLocation(b);
        notify(bl);
        
        addBehaviour(new WakerBehaviour(this, Main.showRandomInteger(START, END, r)) {
            
            

            @Override
            protected void onWake() {
                myAgent.addBehaviour(new AchieveREInitiator(myAgent, new ACLMessage(ACLMessage.REQUEST)){

                    @Override
                    protected void handleAllResultNotifications(Vector resultNotifications) {
                        if (resultNotifications.size() > 0) {
                            BuyerBought bb = new BuyerBought(BuyerAgent.this.getData());
                            
                            try {
                                for (Object object : resultNotifications) {
                                        ACLMessage msg = (ACLMessage) object;
                                        BuyOrder bo = (BuyOrder) myAgent.getContentManager().extractContent(msg);
                                        BuyerAgent.this.getData().getBought().add(bo.getOrder());
                                }
                            } catch (CodecException ex) {
                                throw new CNSException("not bought", ex);
                            } catch (UngroundedException ex) {
                                throw new CNSException("not bought", ex);
                            } catch (OntologyException ex) {
                                throw new CNSException("not bought", ex);
                            }
                            BuyerAgent.this.notify(bb);
                        }
                    }

                    @Override
                    protected Vector prepareRequests(ACLMessage request) {
                        try {
                            ServiceDescription[] criteria = new ServiceDescription[] {
                                CNSHelper.createServiceDescription(OrderAgent.class.getSimpleName(), OrderAgent.class.getSimpleName()),
                                CNSHelper.createServiceDescription(OrderAgent.BUYME, OrderAgent.BUYME)
                            };
                            AID[] or = CNSHelper.findAgents(myAgent, criteria);
                            if (or == null || or.length == 0) {
                                BuyerAgent.this.notify("no orders found to buy");
                                return new Vector();
                            } else {
                                Vector v = new Vector();
                                BuyerAgent.this.notify("trying to buy orders " + Arrays.asList(or));
                                int i = 0;
                                for (AID aid : or) {
                                    if (i > 3) {
                                        break;
                                    }
                                    if (asked.contains(aid)) {
                                        continue;
                                    }
                                    ACLMessage buymsg = createMsg(
                                            aid,
                                            ACLMessage.REQUEST,
                                            FIPANames.InteractionProtocol.FIPA_REQUEST);
                                    buymsg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
                                    BuyOrder buy = new BuyOrder();
                                    buy.setSlaLocation(BuyerAgent.this.getData().getSlaLocation());
                                    myAgent.getContentManager().fillContent(buymsg, buy);
                                    v.add(buymsg);
                                    asked.add(aid);
                                    i++;
                                }
                                return v;
                            }
                        } catch (CodecException ex) {
                            ex.printStackTrace();
                            System.exit(1);
                            throw new CNSException("unable to buy orders", ex);
                        } catch (OntologyException ex) {
                            throw new CNSException("unable to buy orders", ex);
                        } catch (FIPAException ex) {
                            throw new CNSException("unable to buy orders", ex);
                        }
                    }
                    
                });
            }

            @Override
            public int onEnd() {
                reset(Main.showRandomInteger(START, END, r));
                myAgent.addBehaviour(this);
                return super.onEnd();
            }
            
            
            
        });
    }
    private ACLMessage createMsg(AID receivers, int perfomative, String protocol) {
        return createMsg(new AID[] {receivers}, perfomative, protocol);
    }
    private ACLMessage createMsg(AID[] receivers, int perfomative, String protocol) {
        ACLMessage msg = CNSHelper.createMessage(
                receivers,
                FloraOntology.NAME,
                FIPANames.ContentLanguage.FIPA_SL,
                perfomative);
        msg.setProtocol(protocol);
        return msg;
    }

}
