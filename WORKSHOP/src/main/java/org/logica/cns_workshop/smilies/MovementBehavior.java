package org.logica.cns_workshop.smilies;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.awt.Point;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns_workshop.Util;
import org.logica.cns_workshop.gui.GUI;
import org.logica.cns_workshop.communication.LocatedAt;
import org.logica.cns_workshop.communication.MovedTo;
import org.logica.cns_workshop.communication.ReachedDoor;
import org.logica.cns_workshop.communication.SmileyOntology;

public class MovementBehavior extends TickerBehaviour {

    private static final Log log = LogFactory.getLog(MovementBehavior.class);
    private static final long serialVersionUID = 1L;
    private SmileyAgent smiley;
    private int dx;
    private int dy;
    private int maxx = 300;
    private int maxy = 300;
    private int reachedDoor = 5;
    private boolean registered = false;
    private MovedTo n = new MovedTo();

    public MovementBehavior(SmileyAgent smiley, long period) {
        super((Agent) smiley, period);
        maxx = Integer.parseInt(JadeHelper.getProperty(GUI.W));
        maxy = Integer.parseInt(JadeHelper.getProperty(GUI.H));
        this.smiley = smiley;
        smiley.p.x = (int) (Math.random() * maxx);
        smiley.p.y = (int) (Math.random() * maxy);
        smiley.smile.setX(smiley.p.x);
        smiley.smile.setY(smiley.p.y);
        initMove();
        n.setSubject(smiley.smile);
    }

    private void initMove() {
        dx = (int) (Math.random() * 2) + 1;
        dy = (int) (Math.random() * 2) + 1;
        if (Math.random() > 0.5) {
            dx = -dx;
        }
        if (Math.random() > 0.5) {
            dy = -dy;
        }
    }

    @Override
    protected void onTick() {
        if (getTickCount() % 600 == 0) {
            initMove();
        }
        try {
            if (!registered) {
                register();
            }
            move();
        } catch (CodecException codecException) {
            log.error("unable to register or move", codecException);
        } catch (OntologyException ontologyException) {
            log.error("unable to register or move", ontologyException);
        }
    }

    private void register() throws CodecException, OntologyException {
        ACLMessage mesg = CNSHelper.createInformMessage(((SmileyAgent) myAgent).roomAID, SmileyOntology.ONTOLOGY_NAME);
        LocatedAt l = new LocatedAt();
        l.setSubject(((SmileyAgent) myAgent).smile);
        l.setSender(((SmileyAgent) myAgent).smile);
        CNSHelper.sendMessage(smiley, mesg, l);
        registered = true;
    }

    private void move() throws CodecException, OntologyException {

        if (doMove(n)) {
            smiley.p.x += n.getXmove();
            smiley.p.y += n.getYmove();
            smiley.smile.setX(smiley.p.x);
            smiley.smile.setY(smiley.p.y);
            ACLMessage mesg = CNSHelper.createInformMessage(smiley.roomAID, SmileyOntology.ONTOLOGY_NAME);
            CNSHelper.sendMessage(smiley, mesg, n);
        } else {
            log.info("reached door " + myAgent.getAID());
            smiley.doorReached = true;
            ReachedDoor r = new ReachedDoor();
            r.setSubject(smiley.smile);
            ACLMessage mesg = CNSHelper.createInformMessage(smiley.roomAID, SmileyOntology.ONTOLOGY_NAME);
            CNSHelper.sendMessage(smiley, mesg, r);
            try {
                DFService.deregister(myAgent);
            } catch (FIPAException ex) {
                log.warn("unable to deregister: " + myAgent.getName(), ex);
            }
            stop();
        }
    }

    private boolean doMove(MovedTo mt) {
        Point door = smiley.door, p = smiley.p;
        if (door != null && Util.distance(p.x,p.y, door.x,door.y) <= reachedDoor) {
            return false;
        }
        int x = dx;
        int y = dy;
        // do we see a door?
        if (door != null) {
            // bepaal totale dx/dy
            x = door.x - p.x;
            y = door.y - p.y;
            // bepaal aantal stappen in de x en y richting om de deur te bereiken
            float numSteps = (Math.abs(x / dx) + Math.abs(y / dy)) / 2;
            // bepaal nu de nodige dx dy
            x = Math.round(x / numSteps);
            y = Math.round(y / numSteps);
        } else {
            if (x + p.x > maxx) {
                dx = x = -x;
            } else if (x + p.x < 0) {
                dx = x = -x;
            }
            if (y + p.y > maxy) {
                dy = y = -y;
            } else if (y + p.y < 0) {
                dy = y = -y;
            }
        }

        mt.setXmove(x);
        mt.setYmove(y);
        return true;
    }
}
