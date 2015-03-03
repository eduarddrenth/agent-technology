package com.logica.amc.workernode;

import com.logica.amc.base.AMCOntology;
import com.logica.amc.base.StatusIs;
import com.logica.amc.base.StatusNotification;
import com.logica.amc.moteur.MOTEURAgent;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author Eduard Drenth: Logica, 23-nov-2009
 * 
 */
public class ErrorNotification extends OneShotBehaviour {
    Log log = LogFactory.getLog(ErrorNotification.class);

    public ErrorNotification(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        try {
            File f = null;
            String errors = "failure in job-shell";
            try {
                f = new File(JadeHelper.getProperty("stderr"));
                BufferedReader in = null;
                String r = null;
                try {
                    in = new BufferedReader(new FileReader(f));
                    int i = 0;
                    while ((r = in.readLine()) != null && i <= ((WorkerNodeAgent) myAgent).maxLines) {
                        errors += r;
                        i++;
                    }
                } catch (FileNotFoundException ex) {
                    log.error("unable to read " + f.getPath(), ex);
                } catch (IOException ex) {
                    log.error("unable to read " + f.getPath(), ex);
                } finally {
                    if (null != in) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            } catch (IllegalArgumentException illegalArgumentException) {
                log.error("cannot read stderr", illegalArgumentException);
            }
            StatusNotification sn = new StatusNotification();
            sn.setDate(new Date());
            sn.setType(StatusNotification.JOBERROR);
            sn.setMessage(String.valueOf(WorkerNodeAgent.error));
            sn.setDetails(errors);
            sn.setJobid(myAgent.getLocalName());
            StatusIs sis = new StatusIs();
            sis.setStatus(sn);
            AID moteur = CNSHelper.findAgent(myAgent, MOTEURAgent.TYPE);
            ACLMessage msg = CNSHelper.createInformMessage(moteur, AMCOntology.ONTOLOGY_NAME);
            CNSHelper.sendMessage(myAgent, msg, sis);
            JadeHelper.stopJade();
        } catch (FIPAException ex) {
            log.error("not sent", ex);
        } catch (CodecException ex) {
            log.error("not sent", ex);
        } catch (OntologyException ex) {
            log.error("not sent", ex);
        }
    }

}
