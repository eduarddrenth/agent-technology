package org.logica.cns_workshop.room;

import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.AgentsFoundHandler;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.CNSMessageHandlerImpl;
import org.logica.cns_workshop.WorkshopAgent;
import org.logica.cns_workshop.communication.Door;
import org.logica.cns_workshop.communication.LocatedAt;
import org.logica.cns_workshop.communication.Smiley;
import org.logica.cns_workshop.communication.SmileyOntology;
import org.logica.cns_workshop.communication.SmileyVocabulary;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public class DoorAgent extends WorkshopAgent {

    private static final Log log = LogFactory.getLog(DoorAgent.class);
    public static final String TYPE = "door";
    public static final String X = "doorx";
    public static final String Y = "doory";
    private Door door = new Door();
    private DoorHandler handler = null;

    /*
     * TODO 2: zorg dat deze agent zich met zijn TYPE registreert, gebruik een fill* uit de super
     */


    @Override
    protected void setup() {
        super.setup();
        /*
         * TODO 1: initialiseer het "Door" objectje (x, y en id) m.b.v. JadeHelper (properties) en de super
         */
        CNSHelper.executeWhenFound(this, new AgentsFoundHandler() {

            @Override
            public void handleFound(Agent a, DFAgentDescription[] dads) {
                try {
                    ACLMessage mesg = CNSHelper.createInformMessage(CNSHelper.findAgent(a, RoomAgent.TYPE), SmileyOntology.ONTOLOGY_NAME);
                    LocatedAt l = new LocatedAt();
                    l.setSubject(door);
                    l.setSender(door);
                    CNSHelper.sendMessage(a, mesg, l);
                } catch (FIPAException fIPAException) {
                    log.error("unable to pass location", fIPAException);
                } catch (CodecException codecException) {
                    log.error("unable to pass location", codecException);
                } catch (OntologyException ontologyException) {
                    log.error("unable to pass location", ontologyException);
                }
            }

            @Override
            public void handleFIPAException(FIPAException ex) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, RoomAgent.TYPE);
    }

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
    }

    class DoorHandler extends CNSMessageHandlerImpl {

        @Override
        protected void handleContent(AbsIRE absIRE, ACLMessage msg) throws OntologyException, CodecException {

            // The initiator created a query-ref of type iota (asked for a multiple result)
            if (absIRE.getTypeName().equals(SLVocabulary.ALL)) {

                // the IRE has two parts, a variable and a condition on it (a proposition or predicate.)

                AbsPredicate p = (AbsPredicate) absIRE.getProposition();

                if (p.getTypeName().equals(SmileyVocabulary.ASK_WHEREISDOOR)) {

                    AbsConcept smiley = (AbsConcept) p.getAbsObject(SmileyVocabulary.SMILEY);

                    Smiley smile = (Smiley) SmileyOntology.getInstance().toObject(smiley);

                    ACLMessage reply = msg.createReply();

                    // we sturen gewoon een inform, moet eigenlijk anders....
                    LocatedAt l = new LocatedAt();
                    l.setSubject(door);
                    l.setSender(door);
                    reply.setPerformative(ACLMessage.INFORM);
                    getContentManager().fillContent(reply, l);
                    send(reply);

                }

            }
        }
    }

    @Override
    protected synchronized CNSMessageHandler getMessageHandler() {
        if (handler == null) {
            handler = new DoorHandler();
        }
        return handler;
    }


    public Door getDoor() {
        return door;
    }
}
