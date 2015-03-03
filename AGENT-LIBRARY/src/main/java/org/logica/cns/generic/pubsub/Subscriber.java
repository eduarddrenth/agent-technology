package org.logica.cns.generic.pubsub;

import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

/**
 * An agent can add this behavior to be informed by another agent when something interesting occurs.
 * @see Publisher
 * @author eduard
 */
public class Subscriber extends SubscriptionInitiator {

    public Subscriber(Agent a, ACLMessage msg, DataStore store) {
        super(a, msg, store);
    }

    public Subscriber(Agent a, ACLMessage msg) {
        super(a, msg);
    }
    
}
