package com.logica.amc.user;

import com.logica.amc.base.*;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author Eduard Drenth: Logica, 21-okt-2009
 * 
 */
public class WorkflowAgentUser extends AMCAgent {

    public static final String TYPE = "receive workflow messages";
    public static final String WORKFLOW = "workflow";
    private static final Log log = LogFactory.getLog(WorkflowAgentUser.class);

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
        init.addServiceDescription(CNSHelper.createServiceDescription(getLocalName(),getLocalName()));
        init.addServiceDescription(CNSHelper.createServiceDescription(getCNSContext().getParameter(WORKFLOW), getCNSContext().getParameter(WORKFLOW)));
    }

    public static void main(String[] args) throws Exception {
        JadeHelper.launchJade(args[1]);
    }

    @Override
    protected void handleStatus(StatusIs status) {
        try {
            ACLMessage msg = CNSHelper.createInformMessage(CNSHelper.findAgent(this, NotificationAgent.TYPE), AMCOntology.ONTOLOGY_NAME);
            CNSHelper.sendMessage(this, msg, status);
        } catch (FIPAException ex) {
            log.error("not sent", ex);
        } catch (CodecException ex) {
            log.error("not sent", ex);
        } catch (OntologyException ex) {
            log.error("not sent", ex);
        }
    }

}
