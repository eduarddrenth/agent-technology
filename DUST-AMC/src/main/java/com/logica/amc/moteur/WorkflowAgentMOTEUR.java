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
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.CNSMessageHandler;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns.generic.ServiceFoundHandler;
import org.logica.cns.io.Tail;
import org.logica.cns.io.Tail.Result;

/**
 *
 * @author Eduard Drenth: Logica, 13-nov-2009
 * 
 */
public class WorkflowAgentMOTEUR extends AMCAgent implements Observer {

    private static Log log = LogFactory.getLog(WorkflowAgentMOTEUR.class);
    private String workflowDir = null;
    private String workflowOut = null;
    private String workflowErr = null;
    public static final String TYPE = "workflow agent moteur";
    public static final String WORKFLOW_DIR_PARAM = "workflowdir";
    public static final String WORKFLOW_OUT_PARAM = "workflowout";
    public static final String WORKFLOW_ERR_PARAM = "workflowerr";
    public static final String TIMEREGEX = "timeregex";
    public static final String JOBREGEX = "jobregex";
    public static final String STATUS_KEYS = "statusprops";
    private Pattern timepattern = null;
    private Pattern jobpattern = null;
    private List<LogPattern> isStatus = new LinkedList<LogPattern>();
    private BlockingQueue<String> history = new LinkedBlockingQueue<String>();
    private List<String> jobsrunning = new LinkedList<String>();
    private boolean nojobs = true;
    private Tail err = null;
    private Tail out = null;
    private AID moteurAgent;
    private AID userWorkflowAgent;

    @Override
    protected void setup() {
        super.setup();
            workflowDir = getCNSContext().getParameter(WORKFLOW_DIR_PARAM);
            workflowOut = JadeHelper.getProperty(WORKFLOW_OUT_PARAM);
            workflowErr = JadeHelper.getProperty(WORKFLOW_ERR_PARAM);

            timepattern = Pattern.compile(JadeHelper.getProperty(TIMEREGEX));
            jobpattern = Pattern.compile(JadeHelper.getProperty(JOBREGEX));
            try {
                loadStatus();
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException("unable to load keys for status", ex);
            } catch (IOException ex) {
                throw new IllegalArgumentException("unable to load keys for status", ex);
            }
    }

    class WfaHandler extends AMCHandler {

        @Override
        protected void handleContent(Predicate predicate, ACLMessage msg) throws CodecException, UngroundedException, OntologyException {
            if (predicate instanceof AgentsPrepared) {
                AgentsPrepared ap = (AgentsPrepared) predicate;
                if (ap.getWorkflow().equals(getLocalName())) {
                    CNSHelper.executeWhenFound(WorkflowAgentMOTEUR.this, new ServiceFoundHandler() {

                        @Override
                        public void handleFound(Agent a, DFAgentDescription[] dads) {
                            if ((WorkflowAgentMOTEUR.this).canTail()) {
                                (WorkflowAgentMOTEUR.this).doTail();
                            }
                        }

                        @Override
                        public void handleFIPAException(FIPAException ex) {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }
                    }, new String[] {WorkflowAgentMOTEUR.this.getLocalName(),WorkflowAgentUser.TYPE});
                } else {
                    log.error(ap.getWorkflow() + " not for me " + getLocalName());
                }
            } else if (predicate instanceof StatusIs) {
                if (userWorkflowAgent != null) {
                    StatusNotification sn = ((StatusIs) predicate).getStatus();
                    if (sn.getType().equals(StatusNotification.JOBERROR)) {
                        sn.setWorkflow(getLocalName());
                        sn.setEmails(ContextHelper.findEmails(getLocalName(), sn.getType()));
                        ACLMessage mesg = CNSHelper.createInformMessage(userWorkflowAgent, AMCOntology.ONTOLOGY_NAME);
                        CNSHelper.sendMessage(WorkflowAgentMOTEUR.this, mesg, predicate);
                    }
                }
            } else {
                super.handleContent(predicate, msg);
            }
        }
    }

    @Override
    protected synchronized CNSMessageHandler getMessageHandler() {
            return new WfaHandler();
    }

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
        init.addServiceDescription(CNSHelper.createServiceDescription(getLocalName(),getLocalName()));
    }

    private void loadStatus() throws FileNotFoundException, IOException {
        File f = new File(JadeHelper.getProperty(STATUS_KEYS));
        BufferedReader bi = null;
        try {
            bi = new BufferedReader(new FileReader(f));
            String r = null;
            while ((r = bi.readLine()) != null) {
                if (r.trim().startsWith("#") || r.trim().isEmpty()) {
                    continue;
                }
                String[] keyValue = r.split("=");
                if (keyValue.length != 2) {
                    throw new IllegalArgumentException("invalid line: " + r);
                }
                isStatus.add(new LogPattern(keyValue[0], Pattern.compile(keyValue[1])));
            }
        } finally {
            if (null != bi) {
                bi.close();
            }
        }
    }

    @Override
    public void takeDown() {
        try {
            super.takeDown();
            if (err != null && out != null) {
                out.stop();
                err.stop();
                out.close();
                err.close();
            }
        } catch (IOException ex) {
            log.error("failure closing tail", ex);
        } catch (InterruptedException ex) {
            log.error("failure closing tail", ex);
        }
    }

    public synchronized void update(Observable o, Object arg) {
        if (null != arg) {
            Tail.Result r = (Result) arg;
            if (r.getPath().equals(workflowDir + File.separator + workflowErr)) {
                if (null == userWorkflowAgent) {
                    try {
                        userWorkflowAgent = CNSHelper.findAgent(this, new String[]{WorkflowAgentUser.TYPE, getLocalName()});
                    } catch (FIPAException ex) {
                        log.error("not found", ex);
                    }
                }
                if (userWorkflowAgent != null) {
                    if (null == moteurAgent) {
                        try {
                            moteurAgent = CNSHelper.findAgent(this, MOTEURAgent.TYPE);
                        } catch (FIPAException ex) {
                        log.error("not found", ex);
                        }
                    }
                    for (String s : r.getLines()) {
                        for (LogPattern e : isStatus) {
                            if (e.p.matcher(s).find()) {
                                log.debug("found type " + e.type + " in " + s);
                                StatusNotification sn = new StatusNotification();
                                sn.setMessage(s);
                                sn.setType(e.type);
                                sn.setWorkflow(getLocalName());
                                sn.setEmails(ContextHelper.findEmails(getLocalName(), e.type));

                                if (StatusNotification.JOBERROR.equals(e.type) || StatusNotification.RESUBMIT.equals(e.type)) {
                                    log.info("found " + e.type + " in " + s);
                                }

                                Matcher m = timepattern.matcher(s);
                                if (m.find()) {
                                    sn.setDate(new Date(Long.parseLong(m.group(1))));
                                }

                                // jobid if any
                                m = jobpattern.matcher(s);
                                if (m.find()) {
                                    log.debug("found job in  " + s);
                                    String jobid = m.group(1);
                                    sn.setJobid(jobid);
                                    if (sn.getType().equals(StatusNotification.JOBSTART)) {
                                        JobIdNotification jin = new JobIdNotification();
                                        jin.setJobid(jobid);

                                        jobsrunning.add(jobid);
                                        nojobs = false;

                                        log.info(jobid + " started");

                                        ACLMessage msg = CNSHelper.createInformMessage(moteurAgent, AMCOntology.ONTOLOGY_NAME);
                                        try {
                                            CNSHelper.sendMessage(this, msg, jin);
                                        } catch (CodecException ex) {
                                            log.error("not sent", ex);
                                        } catch (OntologyException ex) {
                                            log.error("not sent", ex);
                                        }
                                    }
                                    if (sn.getType().equals(StatusNotification.JOBSUCCESS)) {
                                        jobsrunning.remove(jobid);
                                        log.info(jobid + " succeeded");
                                    }
                                }

                                if (sn.getType().equals(StatusNotification.ERROR) || sn.getType().equals(StatusNotification.SUCCESS)) {
                                    // we weten niet uit de logfile wat er aan de hand is.....alleen dat de workflow beeindigd is
                                    if (jobsrunning.size() > 0 || nojobs) {
                                        log.info("workflow " + getLocalName() + " failed " + jobsrunning);
                                        sn.setType(StatusNotification.ERROR);
                                    } else {
                                        log.info("workflow " + getLocalName() + " succeeded " + jobsrunning);
                                        sn.setType(StatusNotification.SUCCESS);
                                    }
                                }


                                StatusIs sis = new StatusIs();
                                sis.setStatus(sn);

                                ACLMessage msg = CNSHelper.createInformMessage(userWorkflowAgent, AMCOntology.ONTOLOGY_NAME);
                                try {
                                    CNSHelper.sendMessage(this, msg, sis);
                                    history.add(s);
                                } catch (CodecException ex) {
                                    log.error("not sent", ex);
                                } catch (OntologyException ex) {
                                    log.error("not sent", ex);
                                }

                                break;
                            }
                        }
                    }
                }
            } else if (r.getPath().equals(workflowDir + File.separator + workflowOut)) {
            }
        } else {
            log.error("failure during tailing file: " + err.getFilePath());
            try {
                StatusNotification sn = new StatusNotification();
                sn.setDate(new Date());
                sn.setType(StatusNotification.ERROR);
                sn.setWorkflow(getLocalName());
                sn.setMessage("problem monitoring workflow");
                sn.setEmails(ContextHelper.findEmails(getLocalName(), StatusNotification.ERROR));
                StatusIs sis = new StatusIs();
                sis.setStatus(sn);
                ACLMessage mesg = CNSHelper.createInformMessage(moteurAgent, AMCOntology.ONTOLOGY_NAME);
                CNSHelper.sendMessage(this, mesg, sis);
            } catch (Exception ex) {
                log.error("unable to kill agents for " + getLocalName(), ex);
            }
        }
    }

    @Override
    protected void handleStatus(StatusIs status) {
        log.warn("received status info: " + status.getStatus().getMessage());
        throw new UnsupportedOperationException("Not supported yet.");
    }

    boolean canTail() {
        return new File(workflowDir + File.separator + workflowErr).canRead()&&new File(workflowDir + File.separator + workflowOut).canRead();
    }

    void doTail() {
        try {
            out = new Tail(workflowDir + File.separator + workflowOut);
            err = new Tail(workflowDir + File.separator + workflowErr);
            err.addObserver(WorkflowAgentMOTEUR.this);
            out.addObserver(WorkflowAgentMOTEUR.this);
        } catch (IOException iOException) {
            throw new IllegalArgumentException("unable to tail workflow errors and/or workflow output", iOException);
        }
        try {
            log.info("start tailing: " + out.getFilePath());
            out.start();
            log.info("start tailing: " + err.getFilePath());
            err.start();
        } catch (FileNotFoundException fileNotFoundException) {
            throw new IllegalArgumentException("unable to tail workflow errors and/or workflow output", fileNotFoundException);
        }
    }

    private class LogPattern {

        private String type;
        private Pattern p;

        private LogPattern(String type, Pattern p) {
            this.type = type;
            this.p = p;
        }
    }
}
