package org.logica.cns.generic;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Eduard Drenth: Logica, 15-mrt-2010
 *
 */
public interface CNSMessageHandler {
    /**
     *
     * @param msg
     * @param a
     * @throws jade.content.lang.Codec.CodecException thrown when the message language (syntax) is not understood
     * @throws UngroundedException thrown when translation from message content to Ontology Objects fails
     * @throws OntologyException thrown when the message grammar is not understood
     */
    void handleACLMessage(ACLMessage msg, Agent a) throws CodecException, UngroundedException, OntologyException;

}
