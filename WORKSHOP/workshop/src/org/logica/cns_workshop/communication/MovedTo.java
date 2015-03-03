package org.logica.cns_workshop.communication;

import jade.content.Predicate;

public class MovedTo implements Predicate {

    private static final long serialVersionUID = 1L;
    private Located subject;
    private int xmove;
    private int ymove;

    public int getXmove() {
        return xmove;
    }

    public int getYmove() {
        return ymove;
    }

    public void setXmove(int xmove) {
        this.xmove = xmove;
    }

    public void setYmove(int ymove) {
        this.ymove = ymove;
    }

    public Located getSubject() {
        return subject;
    }

    public void setSubject(Located subject) {
        this.subject = subject;
    }
}
