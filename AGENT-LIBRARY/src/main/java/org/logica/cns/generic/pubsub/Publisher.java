package org.logica.cns.generic.pubsub;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.proto.SubscriptionResponder;
import jade.proto.SubscriptionResponder.Subscription;

public class Publisher implements SubscriptionResponder.SubscriptionManager {

    @Override
    public boolean register(Subscription s) throws RefuseException, NotUnderstoodException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deregister(Subscription s) throws FailureException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
