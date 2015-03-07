package org.logica.cns_workshop.communication;

import jade.content.Predicate;

public class ReachedDoor implements Predicate {

    private static final long serialVersionUID = 1L;
    private Located subject;

    public Located getSubject() {
        return subject;
    }

    public void setSubject(Located subject) {
        this.subject = subject;
    }
    
}
