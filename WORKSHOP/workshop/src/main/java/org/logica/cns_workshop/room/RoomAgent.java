package org.logica.cns_workshop.room;

import jade.content.ContentElementList;
import jade.content.Predicate;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.CNSMessageHandlerImpl;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns_workshop.Util;
import org.logica.cns_workshop.WorkshopAgent;
import org.logica.cns_workshop.gui.GUI;
import org.logica.cns_workshop.smilies.SmileyAgent;
import org.logica.cns_workshop.communication.Door;
import org.logica.cns_workshop.communication.Located;
import org.logica.cns_workshop.communication.LocatedAt;
import org.logica.cns_workshop.communication.MovedTo;
import org.logica.cns_workshop.communication.ReachedDoor;
import org.logica.cns_workshop.communication.Room;
import org.logica.cns_workshop.communication.Smiley;
import org.logica.cns_workshop.communication.SmileyOntology;
import org.logica.cns_workshop.communication.YourNeighbour;

/**
 *
 * @author Eduard Drenth: Logica, 23-dec-2009
 * 
 */
public class RoomAgent extends WorkshopAgent {

    public static final String TYPE = "room";
    private final HashMap<AID, ColorPoint> smileys = new HashMap<AID, ColorPoint>();
    Door door = null;
    private int withinRange = 0;
    private Room room = new Room();
    private static final Log log = LogFactory.getLog(RoomAgent.class);
    private RoomHandler handler = null;


    @Override
    protected void setup() {
        super.setup();
        withinRange = (int) Math.pow(Integer.parseInt(JadeHelper.getProperty(SmileyAgent.WITHINRANGE)), 2);
        room.setAID(getAID());
        addBehaviour(new UpdateGUIBehavior(RoomAgent.this, 50));
        addBehaviour(new InformSmileysBehavior(RoomAgent.this, 50));
    }


    class RoomHandler extends CNSMessageHandlerImpl {

        @Override
        protected void handleContent(Predicate predicate, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
            if (predicate instanceof MovedTo) {
                MovedTo n = (MovedTo) predicate;
                ColorPoint smiley = initSmiley((Smiley)n.getSubject());
                smiley.x += n.getXmove();
                smiley.y += n.getYmove();
                smiley.color = ((Smiley) n.getSubject()).getColor();
                smiley.foundDoor = ((Smiley) n.getSubject()).getFoundDoor();
                smiley.altruistic = ((Smiley) n.getSubject()).isAltruistic();
            } else if (predicate instanceof LocatedAt) {
                LocatedAt n = (LocatedAt) predicate;
                if (n.getSubject() instanceof Door) {
                    door = (Door) n.getSubject();
                } else if (n.getSubject() instanceof Smiley) {
                    initSmiley((Smiley)n.getSubject());
                }
            } else if (predicate instanceof ReachedDoor) {
                ReachedDoor n = (ReachedDoor) predicate;
                smileys.remove(n.getSubject().getAID());
            } else {
                super.handleContent(predicate, msg);
            }
        }
    }

    @Override
    protected synchronized CNSMessageHandler getMessageHandler() {
        if (handler == null) {
            handler = new RoomHandler();
        }
        return handler;
    }




    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
    }

    private ColorPoint initSmiley(Smiley loc) {
        ColorPoint smiley = smileys.get(loc.getAID());
        if (smiley == null) {
            smiley = new ColorPoint(loc.getX(), loc.getY(), loc.getColor(),loc.isAltruistic());
            smileys.put(loc.getAID(), smiley);
        }
        return smiley;
    }

    private void informSmileys() throws CodecException, OntologyException {
        if (door != null) {
            LocatedAt doorLocByRoom = new LocatedAt();
            doorLocByRoom.setSender(room);
            doorLocByRoom.setSubject(door);



            for (Entry<AID, ColorPoint> close : smileys.entrySet()) {
                boolean doorPublished = false;
                // is de smiley dicht bij de deur?
                if (!close.getValue().foundDoor && Util.distance(door.getX(), door.getY(), close.getValue().x, close.getValue().y) < withinRange) {
                    // dit geeft aan dat de deur gevonden is door de smiley zelf
                    doorPublished = true;
                }
                if (close.getValue().foundDoor || doorPublished) {
                    // als de smiley een deur heeft gevonden of als die net is gepubliceerd, krijgt de smiley een lijstje buren zonder deur
                    ContentElementList neighbours = new ContentElementList();
                    boolean smileySend = false;
                    // opzoek naar buren zonder deur
                    for (Entry<AID, ColorPoint> neighbour : smileys.entrySet()) {
                        // iedereen zonder deur in de buurt behalve ikzelf wordt een buur
                        if (!neighbour.getKey().equals(close.getKey()) && !neighbour.getValue().foundDoor && Util.distance(close.getValue().x, close.getValue().y, neighbour.getValue().x, neighbour.getValue().y) < withinRange) {
                            Smiley sm = new Smiley();
                            // vul de gegevens over de buurman
                            sm.setColor(neighbour.getValue().color);
                            sm.setAID(neighbour.getKey());
                            sm.setX(neighbour.getValue().x);
                            sm.setY(neighbour.getValue().y);
                            YourNeighbour ynb = new YourNeighbour();
                            ynb.setDoor(door);
                            ynb.setFoundDoor(doorPublished);
                            ynb.setSender(sm);
                            neighbours.add(ynb);
                            smileySend = true;
                        }
                    }
                    if (smileySend) {
                        ACLMessage smileyMsg = CNSHelper.createInformMessage(close.getKey(), SmileyOntology.ONTOLOGY_NAME);
                        CNSHelper.sendMessage(this, smileyMsg, neighbours);
                    }
                    if (doorPublished) {
                        ACLMessage roomMsg = CNSHelper.createInformMessage(close.getKey(), SmileyOntology.ONTOLOGY_NAME);
                        getContentManager().fillContent(roomMsg, doorLocByRoom);
                        send(roomMsg);
                    }


                }
            }
        }
    }
    
    private static final Color fromRgb(int rgb) {
       if (rgb == Color.red.getRGB()) {
          return Color.red;
       } else if (rgb == Color.yellow.getRGB()) {
          return Color.yellow;
       } else if (rgb == Color.green.getRGB()) {
          return Color.green;
       } else if (rgb == Color.blue.getRGB()) {
          return Color.blue;
       } else if (rgb == Color.black.getRGB()) {
          return Color.black;
       } else {
          throw new IllegalArgumentException(String.format("rgb %s not supported", rgb));
       }
    }

    private void draw(GUI gui) {
        if (door != null) {
            gui.cls(Color.darkGray);
            for (ColorPoint cp : smileys.values()) {
               gui.drawSmiley(cp.x, cp.y, fromRgb(cp.color),cp.altruistic);
            }
            gui.drawDoor(door);
            gui.updateGui();
        }
    }

    private class InformSmileysBehavior extends TickerBehaviour {

        public InformSmileysBehavior(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            try {
                ((RoomAgent) myAgent).informSmileys();
            } catch (CodecException ex) {
                log.error(ex, ex);
            } catch (OntologyException ex) {
                log.error(ex, ex);
            }
        }
    }

    private class UpdateGUIBehavior extends TickerBehaviour {

        public UpdateGUIBehavior(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            ((RoomAgent) myAgent).draw(GUI.getInstance());
        }
    }

    private class ColorPoint {

        int x, y;
        int color;
        boolean foundDoor = false;
        boolean altruistic;

        public ColorPoint(int x, int y, int color, boolean altruistic) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.altruistic = altruistic;
        }
    }
}
