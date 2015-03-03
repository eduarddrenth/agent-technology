package com.logica.amc.workernode;

import com.logica.amc.base.*;
import jade.core.MicroRuntime;
import jade.util.leap.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author Eduard Drenth: Logica, 20-nov-2009
 * 
 */
public class WorkerNodeAgent extends AMCAgent {
    public static final String SHELLSTATUS = "shellstatus";

    public static final String TYPE = "job error notifier";
    public static final int MAXLINES = 200;
    public static final String MAXLINES_PARAM = "maxlines";
    int maxLines = MAXLINES;
    static int error = 1;
    private static final Log log = LogFactory.getLog(WorkerNodeAgent.class);

    @Override
    protected void setup() {
        super.setup();
            try {
                maxLines = Integer.parseInt(JadeHelper.getProperty(MAXLINES_PARAM));
            } catch (IllegalArgumentException illegalArgumentException) {
                log.warn("problem setting maxlines, keeping default ("+MAXLINES+")", illegalArgumentException);
            }
            addBehaviour(new ErrorNotification(WorkerNodeAgent.this));
    }

    @Override
    protected void fillDescription(CNSAgentInitializer init) {
        init.addServiceDescription(CNSHelper.createServiceDescription(TYPE, TYPE));
        init.addServiceDescription(CNSHelper.createServiceDescription(getLocalName(), getLocalName()));
    }

    public static void main(String[] args) throws Exception {
        Properties p = new Properties();
        p.load(args[1]);

        if (args.length > 2) {
            p.setProperty(SHELLSTATUS, args[2]);
            try {
                error = Integer.parseInt(JadeHelper.getProperty(SHELLSTATUS));
            } catch (IllegalArgumentException illegalArgumentException) {
                log.warn("problem setting shell status, keeping default ("+1+")", illegalArgumentException);
            }
        }

        // add container name
        p.setProperty("container-name", System.getenv("GLITE_WMS_JOBID"));
        p.setProperty("msisdn", System.getenv("GLITE_WMS_JOBID"));

        JadeHelper.launchJade(p);
        
        MicroRuntime.startAgent(System.getenv("GLITE_WMS_JOBID"), WorkerNodeAgent.class.getName(), null);
    }

    @Override
    protected void handleStatus(StatusIs status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
