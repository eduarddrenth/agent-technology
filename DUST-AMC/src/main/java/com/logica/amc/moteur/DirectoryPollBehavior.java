package com.logica.amc.moteur;

import com.logica.amc.user.WorkflowAgentUser;
import org.logica.cns.io.DirectoryChanges;
import org.logica.cns.io.DirectoryListener;
import org.logica.cns.io.DirectoryChangeHandler;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns.util.CNSContext;

/**
 *
 * @author Eduard Drenth: Logica, 13-nov-2009
 * 
 */
public class DirectoryPollBehavior extends TickerBehaviour implements DirectoryChangeHandler, DirectoryListener {

    public static final String PREFIX = "workflow-";
    public static final String MOTEURDIR_PARAM = "moteurdir";
    private static Log log = LogFactory.getLog(DirectoryPollBehavior.class);
    private File[] initialListing = null;
    private File[] currentListing = null;
    private DirectoryChanges changes = null;
    private Set<DirectoryChangeHandler> handlers = new HashSet<DirectoryChangeHandler>();
    private File workflowdir = null;
    private static final FileFilter dirFilter = new FileFilter() {

        public boolean accept(File pathname) {
            if (!pathname.canRead()) {
                log.error("cannot read: " + pathname.getName());
            }
            return pathname.isDirectory() && pathname.getName().startsWith(PREFIX);
        }
    };

    public DirectoryPollBehavior(Agent a, long interval) {
        super(a, interval);
        workflowdir = new File(JadeHelper.getProperty(MOTEURDIR_PARAM));
        if (workflowdir.exists() && workflowdir.isDirectory() && workflowdir.canRead()) {
            initialListing = workflowdir.listFiles(dirFilter);
            addDirectoryChangeHandler(this);
        } else {
            if (!workflowdir.exists()) {
                throw new IllegalArgumentException("does not exist: " + workflowdir.getName());
            }
            if (!workflowdir.isDirectory()) {
                throw new IllegalArgumentException("no directory: " + workflowdir.getName());
            }
            if (!workflowdir.canRead()) {
                throw new IllegalArgumentException("cannot read: " + workflowdir.getName());
            }
        }
    }

    @Override
    protected void onTick() {
        currentListing = workflowdir.listFiles(dirFilter);
        if (!Arrays.equals(initialListing, currentListing) && currentListing != null) {
            if (log.isDebugEnabled()) {
                log.debug("initial listing " + Arrays.asList(initialListing));
                log.debug("current listing " + Arrays.asList(currentListing));
            }
            changes = changes(initialListing, currentListing);
            notifyChanges(changes);
            initialListing = currentListing;
        }
    }

    private static DirectoryChanges changes(File[] initial, File[] current) {
        DirectoryChanges changes = new MOTEURDirectoryChanges();
        if (null != initial && null != current) {
            List<File> i = Arrays.asList(initial);
            List<File> c = Arrays.asList(current);
            for (File file : i) {
                if (!c.contains(file)) {
                    changes.addRemoved(file);
                }
            }
            for (File file : c) {
                if (!i.contains(file)) {
                    changes.addAdded(file);
                }
            }
        }
        return changes;
    }

    public void handleChanges(DirectoryChanges changes) {
        for (File f : changes.getAdded()) {
            if (f.getName().startsWith(PREFIX)) {
                log.info("detected start of workflow: " + f.getName());
            }
            startAgents(f);
        }
        for (File f : changes.getRemoved()) {
            if (f.getName().startsWith(PREFIX)) {
                log.info("detected removed workflow: " + f.getName());
                ((MOTEURAgent)myAgent).killAgents(f.getName());
            }
        }
    }

    private void startAgents(File f) {
        try {
            // give moteur some time to start
            Thread.sleep(2000);
            // start agent at moteur to monitor this workflow
            CNSContext context = CNSContext.fromArgs(
                    new String[]{
                        CNSContext.makeParameterString(WorkflowAgentMOTEUR.WORKFLOW_DIR_PARAM, f.getPath())
                    }
            );
            JadeHelper.createAgent(myAgent, f.getName(), JadeHelper.getContainerName(), WorkflowAgentMOTEUR.class.getName(), context.toArgs());
            // give it some tim eto start
            Thread.sleep(1000);

            // start agent to do user notification
            String user = ContextHelper.findWorkflowUser(f.getName());
            String container = (JadeHelper.getContainerID( myAgent, user, true) == null) ? JadeHelper.getContainerName() : user;
            log.info("using container for user: " + container);

            context = CNSContext.fromArgs(
                    new String[]{
                        CNSContext.makeParameterString(WorkflowAgentUser.WORKFLOW, f.getName())
                    });
            JadeHelper.createAgent(myAgent, user + "_" + f.getName(),container, WorkflowAgentUser.class.getName(), context.toArgs());
            ((MOTEURAgent) myAgent).activeWorkflows.add(f.getName());
        } catch (Exception ex) {
            log.error("agents not started for " + f.getName(), ex);
            ((MOTEURAgent)myAgent).killAgents(f.getName());
        }
    }

    public void notifyChanges(DirectoryChanges changes) {
        for (DirectoryChangeHandler h : handlers) {
            h.handleChanges(changes);
        }
    }

    /**
     *
     * @param handler
     * @return true if the handler was registered
     */
    public boolean addDirectoryChangeHandler(DirectoryChangeHandler handler) {
        return handlers.add(handler);
    }

    /**
     *
     * @param handler
     * @return true if the handler was unregistered
     */
    public boolean removeDirectoryChangeHandler(DirectoryChangeHandler handler) {
        return handlers.remove(handler);
    }
}
