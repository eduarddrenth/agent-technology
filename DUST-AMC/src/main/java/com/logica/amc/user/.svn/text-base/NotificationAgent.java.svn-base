package com.logica.amc.user;

import com.logica.amc.base.*;
import com.logica.amc.moteur.MOTEURAgent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.List;
import java.util.Iterator;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns.mail.MailSender;

/**
 *
 * @author Eduard Drenth: Logica, 21-okt-2009
 * 
 */
public class NotificationAgent extends AMCAgent {

    public static final String MAILHOST = "mailhost";
    public static final String RECEPIENT = "recepient";
    public static final String SENDER = "sender";
    public static final String TYPE = "user informer";
    private Log log = LogFactory.getLog(NotificationAgent.class);
    private MailSender ms = null;
    private java.util.List<StatusNotification> l = new java.util.LinkedList<StatusNotification>();

    @Override
    protected void setup() {
        super.setup();
        try {
            ms = new MailSender(JadeHelper.getProperty(MAILHOST));
        } catch (MessagingException ex) {
            log.error("error initializing mail", ex);
        }
    }

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
    }

    private void notify(List emails, String subject, String content) throws MessagingException {
        if (emails != null && emails.size() > 0) {
            String[] mails = new String[emails.size()];
            int i = 0;
            for (Object o : emails.toArray()) {
                mails[i++] = String.valueOf(o);
            }
            try {
                ms.send(mails, new String[]{}, new String[]{}, JadeHelper.getProperty(SENDER), subject + "", content + "", 3);
            } catch (IllegalArgumentException illegalArgumentException) {
                log.warn("unable to send (" + content + ") to " + emails, illegalArgumentException);
            } catch (MessagingException messagingException) {
                log.warn("unable to send (" + content + ") to " + emails, messagingException);
            }
        }
    }

    private void sendOverview(StatusNotification sn) {
        synchronized (l) {
            if (ms != null) {
                String c = "";
                for (StatusNotification snn : l) {
                    if (snn.getWorkflow().equals(snn.getWorkflow())) {
                        c += "\n\n" + snn.toString();
                    }
                }
                try {
                    notify(sn.getEmails(), "workflow overview: " + sn.getType(), c);
                    for (Iterator<StatusNotification> it = l.iterator(); it.hasNext();) {
                        if (it.next().getWorkflow().equals(sn.getWorkflow())) {
                            it.remove();
                        }
                    }
                } catch (MessagingException ex) {
                    log.error("failed to send mail for " + sn.getWorkflow() + " type " + sn.getType(), ex);
                }
            }
        }
    }

    @Override
    protected void handleStatus(StatusIs status) {
        StatusNotification sn = status.getStatus();
        l.add(sn);
        log.debug(sn.getMessage());
        if (StatusNotification.SUCCESS.equals(sn.getType()) || StatusNotification.ERROR.equals(sn.getType())) {
            sendOverview(sn);
            log.debug("detected " + sn.getType() + " of workflow: " + sn.getWorkflow());
            try {
                ACLMessage mesg = CNSHelper.createInformMessage(
                        CNSHelper.findAgent(this, MOTEURAgent.TYPE), AMCOntology.ONTOLOGY_NAME);
                CNSHelper.sendMessage(this, mesg, status);
            } catch (Exception ex) {
                log.error("unable to kill agents for " + sn.getWorkflow(), ex);
            }
        } else {
            try {
                notify(sn.getEmails(), "info for " + sn.getWorkflow() + ", " + sn.getType(), sn.getDetails());
            } catch (MessagingException ex) {
                log.error("failed to send mail for " + sn.getWorkflow() + " type " + sn.getType(), ex);
            }
        }
    }
}
