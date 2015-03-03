package com.logica.amc.moteur;

import com.logica.amc.base.AMCAgent;
import com.logica.amc.base.AMCOntology;
import com.logica.amc.base.AgentsPrepared;
import com.logica.amc.base.JobIdNotification;
import com.logica.amc.base.StatusIs;
import com.logica.amc.base.StatusNotification;
import com.logica.amc.user.WorkflowAgentUser;
import jade.content.Predicate;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSContext;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author Eduard Drenth: Logica, 13-nov-2009
 * 
 */
public class MOTEURAgent extends AMCAgent {

    /**
     * todo, how to find user
     */
    public static final String TYPE = "moteur monitoring";
    public static final long INTERVAL = 1000;
    private long interval = INTERVAL;
    public static final String INTERVAL_PARAM = "interval";
    private static final Log log = LogFactory.getLog(MOTEURAgent.class);
    List<String> activeWorkflows = new LinkedList<String>();
    Map<String, AID> jobBelongsToAgent = new java.util.HashMap<String, AID>();

    @Override
    protected void setup() {
        super.setup();
            if (null != getCNSContext().getParameter(INTERVAL_PARAM)) {
                try {
                    interval = Long.parseLong(getCNSContext().getParameter(INTERVAL_PARAM));
                } catch (NumberFormatException numberFormatException) {
                }
            }

            addBehaviour(new DirectoryPollBehavior(MOTEURAgent.this, interval));
    }

    class MOTEURHandler extends AMCHandler {

        @Override
        protected void handleContent(Predicate predicate, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
            if (predicate instanceof JobIdNotification) {
                jobBelongsToAgent.put(((JobIdNotification) predicate).getJobid(), msg.getSender());
            } else if (predicate instanceof StatusIs) {
                StatusNotification sn = ((StatusIs) predicate).getStatus();
                if (sn.getType().equals(StatusNotification.SUCCESS) || sn.getType().equals(StatusNotification.ERROR)) {
                    try {
                        // remove jobids and
                        AID wfa = CNSHelper.findAgent(MOTEURAgent.this, new String[]{sn.getWorkflow(), WorkflowAgentMOTEUR.TYPE});
                        for (Iterator it = jobBelongsToAgent.entrySet().iterator(); it.hasNext();) {
                            Map.Entry<String, AID> e = (Entry<String, AID>) it.next();
                            if (e.getValue().equals(wfa)) {
                                it.remove();
                            }
                        }
                        killAgents(sn.getWorkflow());
                        activeWorkflows.remove(sn.getWorkflow());
                        ContextHelper.clean(sn.getWorkflow());
                    } catch (Exception ex) {
                        log.error("unable to kill", ex);
                    }

                } else if (sn.getType().equals(StatusNotification.JOBERROR)) {
                    AID workflowAgent = jobBelongsToAgent.get(sn.getJobid());
                    ACLMessage mesg = CNSHelper.createMessage(workflowAgent, AMCOntology.ONTOLOGY_NAME, FIPANames.ContentLanguage.FIPA_SL, ACLMessage.INFORM);
                    CNSHelper.sendMessage(MOTEURAgent.this, mesg, predicate);
                }
            } else if (predicate instanceof Done) {
                Done d = (Done) predicate;
                if (d.getAction() instanceof Action) {
                    Action a = (Action) d.getAction();
                    if (a.getAction() instanceof CreateAgent) {
                        CreateAgent ca = (CreateAgent) a.getAction();
                        List<String> l = new LinkedList<String>();
                        for (Iterator it = ca.getAllArguments(); it.hasNext();) {
                            l.add(it.next().toString());
                        }
                        String[] aa = new String[l.size()];
                        CNSContext ct = CNSContext.fromArgs((String[]) l.toArray(aa));
                        if (ct.getParameter(WorkflowAgentUser.WORKFLOW) != null) {
                            String workflowMonitor = ct.getParameter(WorkflowAgentUser.WORKFLOW);
                            log.info("created: " + ca.getAgentName() + ", notifying " + workflowMonitor);
                            AID workflowAgent;
                            try {
                                workflowAgent = CNSHelper.findAgent(MOTEURAgent.this, new String[]{workflowMonitor, WorkflowAgentMOTEUR.TYPE});
                                AgentsPrepared ap = new AgentsPrepared(workflowMonitor);
                                ACLMessage mesg = CNSHelper.createMessage(workflowAgent, AMCOntology.ONTOLOGY_NAME, FIPANames.ContentLanguage.FIPA_SL, ACLMessage.INFORM);
                                CNSHelper.sendMessage(MOTEURAgent.this, mesg, ap);
                            } catch (FIPAException ex) {
                                log.error("unable to find", ex);
                            }
                        } else {
                            super.handleContent(predicate, msg);
                        }
                    } else {
                        super.handleContent(predicate, msg);
                    }
                } else {
                    super.handleContent(predicate, msg);
                }
            } else {
                super.handleContent(predicate, msg);
            }
        }
    }

    @Override
    protected synchronized CNSMessageHandler getMessageHandler() {
            return new MOTEURHandler();
    }

    @Override
    protected void fillOntology(CNSAgentInitializer init) {
        init.addOntology(JADEManagementOntology.getInstance());
    }

    public static void main(String[] args) throws Exception {
        JadeHelper.launchJade(args[1]);
    }

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
    }

    @Override
    protected void handleStatus(StatusIs status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void killAgents(String workflow) {
        try {
            AID[] agents = CNSHelper.findAgents(this, workflow);
            if (null != agents) {
                for (AID aid : agents) {
                    log.info("killing agent: " + aid.getName());
                    JadeHelper.killAgent(this, aid);
                }
            } else {
                log.warn("agents not found: " + workflow);
            }
            // how to find the grid agents? for now no need, they directly shutdown
        } catch (Exception ex) {
            log.error("unable to kill agents for " + workflow, ex);
        }
    }
}
