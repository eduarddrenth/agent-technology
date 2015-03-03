package org.logica.cns.generic;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Eduard Drenth: Logica, 12-jan-2010
 * 
 */
public class RegisterBehavior extends TickerBehaviour {

    private static final Log log = LogFactory.getLog(RegisterBehavior.class);
    public static final long TIMEOUT = 2000L;
    public static final int MAX_TRIES = 50;
    public static final Pattern ALREADY_REGISTERED = Pattern.compile(" already-registered\\)");
    private DFAgentDescription dfd = null;

    RegisterBehavior(Agent a, long period, DFAgentDescription dfd) {
        super(a, period);
        this.dfd = dfd;
        onTick();
    }

    /**
     * tries to register waiting for {@link #TIMEOUT} milliseconds, tries {@link #MAX_TRIES} times. Logs an error when registration fails
     */
    @Override
    protected void onTick() {
        try {
            register(myAgent.getDefaultDF(), dfd, TIMEOUT);
            stop();
        } catch (FIPAException ex) {
            if (ex.getMessage().equals("Missing reply")) {
                log.warn("no reply, trying again to register (" + (MAX_TRIES - getTickCount()) + " attempts left)" + myAgent.getName(), ex);
            } else {
                log.error("unable to register " + myAgent.getName(), ex);
                stop();
            }
        }
        if (getTickCount() >= MAX_TRIES) {
            stop();
        }
    }

    private void register(AID dfName, DFAgentDescription dfd, long timeout) throws FIPAException {

        ACLMessage request = DFService.createRequestMessage(myAgent, dfName, FIPAManagementVocabulary.REGISTER, dfd, null);
        ACLMessage reply = DFService.doFipaRequestClient(myAgent, request, timeout);
        if (reply == null) {
            throw new FIPAException("Missing reply");
        } else if (ALREADY_REGISTERED.matcher(reply.getContent()).find()) {
            log.warn("already registered: " + myAgent.getName());
        }
    }
}
