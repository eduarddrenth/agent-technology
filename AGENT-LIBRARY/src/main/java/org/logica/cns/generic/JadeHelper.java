/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.generic;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentManager.Listener;
import jade.core.ContainerID;
import jade.core.MainContainer;
import jade.core.MainContainerImpl;
import jade.core.MicroRuntime;
import jade.core.NotFoundException;
import jade.core.ProfileException;
import jade.core.ProfileImpl;
import jade.core.UnreachableException;
import jade.core.event.MTPEvent;
import jade.core.event.PlatformEvent;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.domain.JADEAgentManagement.QueryPlatformLocationsAction;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.security.JADESecurityException;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author eduard
 */
public class JadeHelper {

    /**
     * name of the property used by {@link #launchJade(jade.util.leap.Properties) } to either use a
     * {@link MicroRuntime} or a {@link jade.core.Runtime}.
     */
    public static final String SPLITCONTAINER = "splitcontainer";

    /**
     * by default an instance of this class is used for handling termination of a Jade (Micro)Runtime
     * @see #setCloser(java.lang.Runnable)
     */
    public static class DefaultTerminationRunnable implements Runnable {

        /**
         * After 1 second delay prints a message and {@link System#exit(int) exits the jvm}
         */
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
            System.out.println("exited Jade, closing jvm");
            System.exit(0);
        }
    }

    /**
     * by default an instance of this class is connected to the Jade Main-Container, it just logs
     * @see #setPlatformListener(jade.core.AgentManager.Listener) 
     */
    public static class PlatformListener implements Listener {

        /**
         * This Listener is informed that an {@link AgentContainer} was added
         * @param ev the event that was triggered
         */
        public void addedContainer(PlatformEvent ev) {
            log.info(ev.getContainer().getID() + " added");
        }

        /**
         * This Listener is informed that an {@link AgentContainer} was removed
         * @param ev the event that was triggered
         */
        public void removedContainer(PlatformEvent ev) {
            log.info(ev.getContainer().getID() + " removed");
        }

        /**
         * This Listener is informed that an {@link Agent} was added
         * @param ev the event that was triggered
         */
        public void bornAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " born");
        }

        /**
         * This Listener is informed that an {@link Agent} was removed
         * @param ev the event that was triggered
         */
        public void deadAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " dead");
        }

        /**
         * This Listener is informed that an {@link Agent} moved to another container
         * @param ev the event that was triggered
         */
        public void movedAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " moved to " + ev.getNewContainer().getID());
        }

        /**
         * This Listener is informed that an {@link Agent} was suspended
         * @param ev the event that was triggered
         */
        public void suspendedAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " suspended");
        }

        /**
         * This Listener is informed that an {@link Agent} resumed
         * @param ev the event that was triggered
         */
        public void resumedAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " resumed");
        }

        /**
         * This Listener is informed that an {@link Agent} was frozen
         * @param ev the event that was triggered
         */
        public void frozenAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " frozen");
        }

        /**
         * This Listener is informed that an {@link Agent} was thawed
         * @param ev the event that was triggered
         */
        public void thawedAgent(PlatformEvent ev) {
            log.info(ev.getAgent().getName() + " thawed");
        }

        /**
         * This Listener is informed that a channel for communication with other platforms was added to the platform
         * @param ev the event that was triggered
         */
        public void addedMTP(MTPEvent ev) {
            log.info(ev.getChannel().getAddress() + " transport added");
        }

        /**
         * This Listener is informed that a channel for communication with other platforms was removed fromthe platform
         * @param ev the event that was triggered
         */
        public void removedMTP(MTPEvent ev) {
            log.info(ev.getChannel().getAddress() + " transport removed");
        }

        /**
         * This Listener is informed that a message to the platform came in over a channel
         * @param ev the event that was triggered
         */
        public void messageIn(MTPEvent ev) {
            log.info("message in from " + ev.getEnvelope().getFrom().getName());
        }

        /**
         * This Listener is informed that a message from the platform is send out over a channel
         * @param ev the event that was triggered
         */
        public void messageOut(MTPEvent ev) {
            log.info("message out from " + ev.getEnvelope().getFrom().getName());
        }
    }

    /**
     * Kills an agent, can only be used from the Runtime with the {@link MainContainer} in it
     * @param agent the id of the agent to be killed
     * @throws ProfileException when the killing fails
     */
    public static void kill(AID agent) throws ProfileException {
        if (null != myProfile) {
            try {
                doKill(agent, 3);
            } catch (InterruptedException ex) {
                log.error("unable to kill " + agent.getName(), ex);
                throw new ProfileException("unable to kill " + agent.getName(), ex);
            } catch (UnreachableException ex) {
                log.error("unable to kill " + agent.getName(), ex);
                throw new ProfileException("unable to kill " + agent.getName(), ex);
            } catch (JADESecurityException ex) {
                log.error("unable to kill " + agent.getName(), ex);
                throw new ProfileException("unable to kill " + agent.getName(), ex);
            }
        } else {
            throw new ProfileException("unable to kill " + agent.getName());
        }
    }

    /**
     * tries to kill an agent, throws an exception if that fails
     * @param agent the agent to be killed
     * @param retries the number of times to retry
     * @throws ProfileException when the number of retries exceeds
     * @throws InterruptedException when the killing fails
     * @throws UnreachableException when the killing fails
     * @throws JADESecurityException when the killing fails
     */
    private static void doKill(AID agent, int retries)
            throws ProfileException, UnreachableException, JADESecurityException, InterruptedException {
        try {
            myProfile.getMyMain().kill(agent, null, null);
        } catch (NotFoundException ex) {
            if (retries > 0) {
                log.warn("trying again to kill " + agent.getName() + ", retries left: " + retries);
                Thread.sleep(1000);
                doKill(agent, --retries);
            } else {
                throw new ProfileException("unable to kill " + agent.getName(), ex);
            }
        }
    }

    /**
     * does a certain agent live in a certain container
     * @param cid the container to check
     * @param agent the agent to find
     * @return true when the agent is in this container
     * @throws ProfileException when the lookup fails
     * @throws NotFoundException when the lookup fails
     * @throws ControllerException when the lookup fails
     */
    public static boolean livesInContainer(ContainerID cid, AID agent)
            throws ProfileException, NotFoundException, ControllerException {
        if (isMain()) {
            return myProfile.getMyMain().getContainerID(agent).equals(cid);
        } else if (container != null) {
            return container.getContainerName().equals(cid.getName()) && container.getAgent(agent.getLocalName()) != null;
        } else {
            throw new NotFoundException("unable to find out if " + agent.getLocalName() + " lives in " + cid.getName());
        }
    }

    private static CNSProfile myProfile = null;
    private static Runnable closer = new DefaultTerminationRunnable();

    /**
     * use a certain Listener for platform events, call before launching the platform.
     * @param platformListener the listener to use for the platform
     */
    public static void setPlatformListener(Listener platformListener) {
        JadeHelper.platformListener = platformListener;
    }
    private static Listener platformListener = new PlatformListener();

    /**
     * use a certain Runnable for handling Jade termination, call before launching the platform or container.
     * @param closer the hanlder for closing the platform
     */
    public static void setCloser(Runnable closer) {
        JadeHelper.closer = closer;
    }
    /**
     * property used for switching caching of container ids on or off.
     */
    public static final String CACHE_CONTAINER_IDS = "cache_container_ids";
    private static Log log = LogFactory.getLog(JadeHelper.class);
    private static String containerName = "unknown";
    private static Properties p = null;
    private static AgentContainer container = null;
    private static Map<String, ContainerID> cids = new HashMap<String, ContainerID>();
    private static boolean main = false;

    /**
     *
     * @return true when this JVM holds a Main-Container
     */
    public static boolean isMain() {
        return main;
    }

    /**
     * get a property value, throws IllegalArgumentException when key not found
     * @param key the key to find
     * @return
     * @throws IllegalArgumentException when property is not there
     */
    public static String getProperty(String key) throws IllegalArgumentException {
        if (hasProperty(key)) {
            return p.getProperty(key);
        } else {
            throw new IllegalArgumentException("key not found: " + key);
        }
    }

    /**
     *
     * @param key the key to find
     * @return true when this Runtime contains the property
     */
    public static boolean hasProperty(String key) {
        return p != null && p.containsKey(key);
    }

    private JadeHelper() {
    }

    /**
     * try to launch Jade using a property file.
     * @param jadePropertiesFileName the path to the properties file
     * @return an AgentContainer or null (MicroRuntime)
     * @throws IOException thrown when Jade is already running
     * @throws ControllerException when starting Jade fails
     * @throws ProfileException when starting Jade fails
     */
    public static synchronized AgentContainer launchJade(String jadePropertiesFileName)
            throws IOException, ControllerException, ProfileException {
        if (null == p) {
            Properties props = new Properties();
            props.load(jadePropertiesFileName);
            launchJade(props);
        } else {
            throw new IOException("Jade already running: " + containerName);
        }
        return container;
    }

    /**
     * try to launch Jade using a set of properties.
     * @param jadeProperties the properties to use
     * @return an AgentContainer or null (MicroRuntime)
     * @throws IOException thrown when Jade is already running
     * @throws ControllerException when starting Jade fails
     * @throws ProfileException when starting Jade fails
     */
    public static synchronized AgentContainer launchJade(Properties jadeProperties)
            throws IOException, ControllerException, ProfileException {
        if (null == p) {
            if ((jadeProperties.containsKey(ProfileImpl.MAIN) && Boolean.parseBoolean(jadeProperties.getProperty(ProfileImpl.MAIN)))
                    || (jadeProperties.containsKey("container") && !Boolean.parseBoolean(jadeProperties.getProperty("container")))
                    || (!jadeProperties.containsKey("container") && !jadeProperties.containsKey(ProfileImpl.MAIN))) {
                // main
                launchJadePlatform(jadeProperties);
            } else {
                // container
                launchJadeContainer(jadeProperties);
            }
        } else {
            throw new IOException("Jade already running: " + containerName);
        }
        return container;
    }

    private static synchronized void initProperties(Properties p) {
        JadeHelper.p = p;
        if (!p.containsKey(CACHE_CONTAINER_IDS)) {
            p.put(CACHE_CONTAINER_IDS, "true");
        }
    }

    private static void launchJadeContainer(jade.util.leap.Properties jadeProperties) throws IOException, ControllerException {
        if (null == p) {

            initProperties(jadeProperties);

            containerName = jadeProperties.getProperty("container-name");

            if (isSplitContainer()) {
                jadeProperties.setProperty(MicroRuntime.JVM_KEY, MicroRuntime.J2SE);

                MicroRuntime.startJADE(jadeProperties, closer);
                if (!MicroRuntime.isRunning()) {
                    throw new ControllerException("failed to start Jade");
                }
            } else {
                container = jade.core.Runtime.instance().createAgentContainer(new ProfileImpl(jadeProperties));
                if (closer != null) {
                    jade.core.Runtime.instance().invokeOnTermination(closer);
                }
            }
        } else {
            throw new IOException("container already running: " + containerName);
        }
    }

    private static class CNSProfile extends ProfileImpl {

        CNSProfile(Properties aProp) {
            super(aProp);
        }

        MainContainerImpl getMyMain() throws ProfileException {
            return getMain();
        }
    }

    private static AgentContainer launchJadePlatform(Properties jadeProperties) throws IOException, ControllerException, ProfileException {
        if (null == p) {
            main = true;
            initProperties(jadeProperties);
            jade.core.Runtime.instance().invokeOnTermination(closer);
            myProfile = new CNSProfile(jadeProperties);
            container = jade.core.Runtime.instance().createMainContainer(myProfile);
            containerName = container.getContainerName();
            if (platformListener != null) {
                myProfile.getMyMain().addListener(platformListener);
            }
            return container;
        } else {
            throw new IOException("platform already running");
        }
    }

    /**
     * try to stop the Jade (Micro)Runtime
     */
    public static void stopJade() {
        log.info("shutting down " + containerName);
        if (isSplitContainer()) {
            MicroRuntime.stopJADE();
        } else {
            jade.core.Runtime.instance().shutDown();
        }
    }

    /**
     * 
     * @param localName the name of the agent (is unique in the platform)
     * @param className the Java class for the agent
     * @param args the arguments to the agent
     * @return
     * @throws Exception when creation of the agent fails
     */
    public static AgentController startLocalAgent(String localName, String className, Object[] args) throws Exception  {
        if (isSplitContainer()) {
            MicroRuntime.startAgent(localName, className, (String[]) args);
            return MicroRuntime.getAgent(localName);
        } else {
            AgentController ac = container.createNewAgent(localName, className, args);
            ac.start();
            return ac;
        }
    }

    /**
     * join an instantiated Agent to the local Container
     * @param agent the agent to add to the container
     * @param name the localName for the agent
     * @throws Exception when creation of the agent fails
     */
    public static void acceptLocalAgent(Agent agent, String name) throws Exception {
        if (isSplitContainer()) {
            throw new IllegalStateException("unable to accept an agent when using MicroBoot (split container)");
        } else if (container != null) {
            container.acceptNewAgent(name, agent).start();
        }
    }

    /**
     * 
     * @param a the agent that will call the AMS to create an agent
     * @param localName the name of the agent to be created
     * @param containerName the name of the container where the agent should be cretaed
     * @param className the Java class for the agent
     * @param args the arguments to the agent
     * @throws CodecException when creation fails
     * @throws UngroundedException when creation fails
     * @throws OntologyException when creation fails
     * @throws InterruptedException when creation fails
     */
    public static AgentCreator createAgent(final Agent a, String localName, String containerName, String className, Object[] args)
            throws CodecException, UngroundedException, OntologyException, InterruptedException {
        CreateAgent ca = new CreateAgent();
        ca.setAgentName(localName);
        ca.setClassName(className);
        ContainerID cid = null;
        cid = getId(a, containerName, 3);
        log.debug("cid found: " + cid);
        if (null != cid) {
            ca.setContainer(cid);
        } else {
            throw new CNSException("no or more than 1 containerid found: " + containerName);
        }
        for (Object o : args) {
            ca.addArguments(o);
        }

        // create and send the message to the ams
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setOntology(JADEManagementOntology.NAME);
        msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        a.getContentManager().fillContent(msg, new Action(a.getAMS(), ca));
        msg.addReceiver(a.getAMS());
        
        AgentCreator ae = new AgentCreator(a, msg);
        a.addBehaviour(ae);
        return ae;
    }

    private static ContainerID getId(Agent a, String containerName, int retries)
            throws CodecException, UngroundedException, OntologyException, InterruptedException {
        for (int i = 0; i <= retries; i++) {
            ContainerID cid = getContainerID(a, containerName, p.containsKey(CACHE_CONTAINER_IDS) && Boolean.parseBoolean(getProperty(CACHE_CONTAINER_IDS)));
            if (cid != null) {
                return cid;
            }
            Thread.sleep(1000);
        }
        return null;
    }

    /**
     * kill an Agent in the local Container
     * @param agent the agent to be killed
     * @throws NotFoundException when killing fails
     * @throws ControllerException when killing fails
     */
    public static void killLocalAgent(AID agent) throws NotFoundException, ControllerException {
        if (isSplitContainer()) {
            MicroRuntime.killAgent(agent.getLocalName());
        } else {
            container.getAgent(agent.getLocalName()).kill();
        }
    }

    /**
     * kill an agent with a certain context
     * @param a the agent that requests the kill
     * @param agent the agent to be killed
     * @throws CodecException when killing fails
     * @throws OntologyException when killing fails
     */
    public static void killAgent(Agent a, AID agent) throws CodecException, OntologyException {
        KillAgent kill = new KillAgent();
        kill.setAgent(agent);

        // create and send the message to the ams
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setOntology(JADEManagementOntology.NAME);
        msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        a.getContentManager().fillContent(msg, new Action(a.getAMS(), kill));
        msg.addReceiver(a.getAMS());
        a.send(msg);

    }

    /**
     * get the Container ID for a name, the requesting Agent must understand JADEManagementOntology
     * @param a the agent that looks for the container id
     * @param containerName the of the container to find
     * @param useCache to use a cache or not
     * @return the id of the container or null
     * @throws CodecException when the lookup fails
     * @throws UngroundedException when the lookup fails
     * @throws OntologyException when the lookup fails
     */
    public static ContainerID getContainerID(Agent a, String containerName, boolean useCache)
            throws CodecException, UngroundedException, OntologyException {
        List<ContainerID> ids = getContainerIDs(a, Arrays.asList(new String[]{containerName}), useCache);
        return (null != ids && ids.size() == 1) ? ids.get(0) : null;
    }

    /**
     * get the Container ID's for names, the requesting Agent must understand JADEManagementOntology
     * @param a the agent that performs this request
     * @param containerNames may be null, when set only ids in this list will be included
     * @param useCache to use a cache or not
     * @return null when no ids found
     * @throws CodecException when the lookup fails
     * @throws OntologyException when the lookup fails
     * @throws UngroundedException when the lookup fails
     */
    public static List<ContainerID> getContainerIDs(Agent a, List<String> containerNames, boolean useCache)
            throws CodecException, UngroundedException, OntologyException {
        if (log.isDebugEnabled()) {
            log.debug("looking for containers: " + containerNames);
        }
        if (useCache) {
            boolean look = false;
            List<ContainerID> result = new ArrayList<ContainerID>();
            for (String s : containerNames) {
                if (cids.containsKey(s)) {
                    if (log.isDebugEnabled()) {
                        log.debug("found in cache: " + s);
                    }
                    result.add(cids.get(s));
                } else {
                    look = true;
                }
            }
            if (!look) {
                return result;
            }
        }
        String mid = (containerNames == null) ? "null" : containerNames.toString();
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol(jade.domain.FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setOntology(JADEManagementOntology.NAME);
        msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        msg.setReplyWith(mid);
        // send a request to the AMS
        a.getContentManager().fillContent(msg, new Action(a.getAMS(),
                new QueryPlatformLocationsAction()));
        msg.addReceiver(a.getAMS());
        a.send(msg);
        // wait for the answer from the ams, risk on blocking here
        msg = a.blockingReceive(MessageTemplate.and(MessageTemplate.MatchOntology(
                JADEManagementOntology.NAME), MessageTemplate.MatchInReplyTo(mid)));
        Result res = null;
        if (null != msg) {
            //extract the content and cast to type Result
            ContentElement ce = a.getContentManager().extractContent(msg);
            if (ce instanceof Result) {
                res = (Result) ce;
            } else {
                return null;
            }
            // make a list of all ContainerID's given in the result
            jade.util.leap.Iterator it = res.getItems().iterator();
            List<ContainerID> result = new ArrayList<ContainerID>();
            while (it.hasNext()) {
                ContainerID cid = (ContainerID) it.next();
                if (null == containerNames) {
                    // alle gevonden ids gebruiken
                    if (log.isDebugEnabled()) {
                        log.debug("found cid for " + containerName + ": " + cid);
                    }
                    cids.put(containerName, cid);
                    result.add(cid);
                } else {
                    // alleen ids uit lijstje gebruiken
                    for (String cname : containerNames) {
                        if (cid.getName().equals(cname)) {
                            if (log.isDebugEnabled()) {
                                log.debug("found cid for " + cname + ": " + cid);
                            }
                            cids.put(cname, cid);
                            result.add(cid);
                        }
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * get the Container ID's for names, the requesting Agent must understand JADEManagementOntology
     * @param searcher the agent that looks for the container
     * @param agent  the agent whose container is looked up
     * @return null when no ids found
     * @throws CodecException when the lookup fails
     * @throws OntologyException when the lookup fails
     */
    public static ContainerID findContainer(Agent searcher, AID agent) throws CodecException, OntologyException {
        if (log.isDebugEnabled()) {
            log.debug("looking for container of: " + agent.getName());
        }
        WhereIsAgentAction wa = new WhereIsAgentAction();
        wa.setAgentIdentifier(agent);
        Action actExpr = new Action(searcher.getAMS(), wa);
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(searcher.getAMS());
        request.setOntology(JADEManagementOntology.getInstance().getName());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        request.setReplyWith("cid lookup " + agent.getName());
        searcher.getContentManager().fillContent(request, actExpr);
        searcher.send(request);
        // this may block!
        ACLMessage msg = searcher.blockingReceive(MessageTemplate.and(MessageTemplate.MatchOntology(
                JADEManagementOntology.NAME), MessageTemplate.MatchInReplyTo("cid lookup " + agent.getName())));
        Result r = (Result) searcher.getContentManager().extractContent(msg);
        return (ContainerID) r.getValue();
    }

    /**
     * @return The name of the {@link AgentContainer} as started by {@link #launchJade(jade.util.leap.Properties) }
     */
    public static String getContainerName() {
        return containerName;
    }

    /**
     *
     * @return true when property {@link #SPLITCONTAINER} is set to true
     */
    public static boolean isSplitContainer() {
        return hasProperty(SPLITCONTAINER) && Boolean.parseBoolean(getProperty(SPLITCONTAINER));
    }

    /**
     *
     * @return an AgentContainer or null (MicroRuntime)
     */
    public static AgentContainer getContainer() {
        return container;
    }
}
