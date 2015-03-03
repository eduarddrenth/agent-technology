package org.logica.cns_workshop.smilies;

import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.Predicate;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.awt.Point;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSAgentInitializerImpl;

import org.logica.cns_workshop.WorkshopAgent;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.CNSMessageHandlerImpl;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns_workshop.Util;
import org.logica.cns_workshop.gui.MemoryImage;
import org.logica.cns_workshop.communication.SmileyOntology;
import org.logica.cns_workshop.communication.Door;
import org.logica.cns_workshop.communication.LocatedAt;
import org.logica.cns_workshop.communication.Room;
import org.logica.cns_workshop.communication.Smiley;
import org.logica.cns_workshop.communication.YourNeighbour;

/**
 * 
 * @author Eduard Drenth: Logica, 21-dec-2009
 * 
 */
public class SmileyAgent extends WorkshopAgent {

    public static final String WITHINRANGE = "withinrange";
    public static final String TYPE = "SMILEY";
    private static final Log log = LogFactory.getLog(SmileyAgent.class);
    private static final long serialVersionUID = 1L;
    Point p = new Point();
    Point door = null;
    Smiley smile = new Smiley();
    LocatedAt doorLoc = new LocatedAt();
    AID roomAID, doorAID;
    boolean doorReached = false;
    private int withinRange;
    private SmileyHandler handler = null;

    @Override
    protected void setup() {
        super.setup();
            /*
             * TODO 1: zorg dat de room en de door gecached worden, gebruik CNSHelper.find*
             */

            smile.setAID(getAID());
            smile.setColor(MemoryImage.black);
            doorLoc.setSender(smile);

            withinRange = (int) Math.pow(Integer.parseInt(JadeHelper.getProperty(WITHINRANGE)), 2);

            addBehaviour(new StartMovingBehavior(SmileyAgent.this));
    }

    class SmileyHandler extends CNSMessageHandlerImpl {

        @Override
        protected void handleContent(ContentElementList list, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
            AID a = null;
            ACLMessage smsg = CNSHelper.createInformMessage(a, SmileyOntology.ONTOLOGY_NAME);
            boolean doSend = false;
            // hier krijgt een smiley die een deur heeft gevonden een lijstje buren binnen om naar te schreeuwen
            //
            // loop over de buren
            for (ContentElement c : list.toArray()) {
                if (c instanceof YourNeighbour) {
                    YourNeighbour ynb = (YourNeighbour) c;
                    if (Util.distance(p.x, p.y, ynb.getSender().getX(), ynb.getSender().getY()) < withinRange) {
                        doorLoc.setSubject(ynb.getDoor());
                        // we weten hier ook via ynb.getFoundDoor() of we zelf de deur gevonden hebben
                        ((Door)doorLoc.getSubject()).setFoundMySelf(ynb.getFoundDoor());
                        smsg.addReceiver(ynb.getSender().getAID());
                        doSend = true;
                    }
                }
            }
            if (doSend) {
                /*
                 * TODO 3: vul de inhoud van smsg, gebruik de doorLoc variabele en getContentManager()
                 *         verstuur smsg
                 */
            }
        }

        @Override
        protected void handleContent(Predicate predicate, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
            if (predicate instanceof LocatedAt) {
                if (!msg.getSender().equals(getAID())) {
                    LocatedAt l = (LocatedAt) predicate;
                    if (l.getSubject() instanceof Door && door == null) {
                        if ((l.getSender() instanceof Room && Util.distance(p.x, p.y, l.getSubject().getX(), l.getSubject().getY()) < withinRange)
                                || l.getSender() instanceof Smiley) {
                            log.info("door found: " + l.getSubject().getAID().getName() + ", send by: " + l.getSender().getAID().getName());
                            door = new Point(l.getSubject().getX(), l.getSubject().getY());
                            if (l.getSender() instanceof Room) {
                                smile.setColor(MemoryImage.green);
                                smile.setFoundDoor(true);
                            } else if (((Door)l.getSubject()).getFoundMySelf()) {
                                smile.setColor(MemoryImage.blue);
                            } else {
                                smile.setColor(MemoryImage.red);
                            }
                        }
                    }
                }
            } else if (predicate instanceof YourNeighbour) {
                YourNeighbour ynb = (YourNeighbour) predicate;
                if (Util.distance(p.x, p.y, ynb.getSender().getX(), ynb.getSender().getY()) < withinRange) {
                        doorLoc.setSubject(ynb.getDoor());
                    ((Door)doorLoc.getSubject()).setFoundMySelf(ynb.getFoundDoor());
                    log.info(getAID().getName() + " informing neighbours, door found myself: " + ((Door)doorLoc.getSubject()).getFoundMySelf());
                    ACLMessage smsg = CNSHelper.createInformMessage(ynb.getSender().getAID(), SmileyOntology.ONTOLOGY_NAME);
                    CNSHelper.sendMessage(SmileyAgent.this, msg, doorLoc);
                }
            } else {
                super.handleContent(predicate, msg);
            }
        }
    }

    @Override
    protected synchronized CNSMessageHandler getMessageHandler() {
        if (handler == null) {
            handler = new SmileyHandler();
        }
        return handler;
    }

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
    }

    private class StartMovingBehavior extends TickerBehaviour {

        public StartMovingBehavior(Agent a) {
            super(a, 5000);
        }

        @Override
        protected void onTick() {
            addBehaviour(new MovementBehavior((SmileyAgent) myAgent, 50));
            stop();
        }
    }
}
