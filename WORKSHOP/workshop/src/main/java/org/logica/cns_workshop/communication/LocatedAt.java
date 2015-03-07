package org.logica.cns_workshop.communication;

import jade.content.Predicate;

public class LocatedAt implements Predicate {

    private static final long serialVersionUID = 1L;
    private Located sender;
    private Located subject;

    public Located getSender() {
        return subject;
    }

    public void setSender(Located sender) {
        this.subject = sender;
    }

    public Located getSubject() {
        return sender;
    }

    public void setSubject(Located subject) {
        this.sender = subject;
    }
    
}
