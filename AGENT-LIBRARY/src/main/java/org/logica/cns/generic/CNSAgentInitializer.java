package org.logica.cns.generic;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * Object responsible for initializing an Agent. {@link #initializeAgent() } should do in this order:
 * <ul>
 *      <li>{@link ContentManager#registerLanguage(jade.content.lang.Codec) register languages }</li>
 *      <li>{@link ContentManager#registerOntology(jade.content.onto.Ontology)  register ontologies }</li>
 *      <li>{@link Agent#addBehaviour(jade.core.behaviours.Behaviour) add behavior for receiving messages }</li>
 *      <li>{@link RegisterBehavior register the agent with the df}</li>
 * </ul>
 * At runtime when creating an agent you should do:
 * <ul>
 *      <li>instantiate an implementation for each agent</li>
 *      <li>{@link #addLanguage(jade.content.lang.Codec) add the languages the agent understands}</li>
 *      <li>{@link #addOntology(jade.content.onto.Ontology) add the gramar the agent understands}</li>
 *      <li>{@link #addServiceDescription(jade.domain.FIPAAgentManagement.ServiceDescription) add the descriptions to publish this agent}</li>
 *      <li>{@link #setMessageReceiver(org.logica.cns.generic.CNSReceiveMessagesBehavior) set the object that will receive messages to this agent }</li>
 * </ul>
 *
 * @author Eduard Drenth: Logica, 15-mrt-2010
 *
 */
public interface CNSAgentInitializer {

    /**
     * Can be called from {@link Agent#setup() }, should add ontologies and codecs with the agent, should add a {@link #startReceiving() message receiver} to the agent and finally {@link #register() }
     * 
     */
    void initializeAgent();

    /**
     * add ontologies to this initializer that should be {@link #initializeAgent(jade.core.Agent)  registered with an agent}
     */
    void addOntology(Ontology o);

    /**
     * add codec to this initializer that should be {@link #initializeAgent(jade.core.Agent)  registered with an agent}
     */
    void addLanguage(Codec c);

    /**
     * add service descriptions to this initializer that should be {@link #register() used for registering} the agent
     */
    void addServiceDescription(ServiceDescription s);

    /**
     * This behavior should be {@link #initializeAgent(jade.core.Agent) added to the agent} in order to start receiving messages.
     * @param receiver
     */
    void setMessageReceiver(Behaviour receiver);

    /**
     * register an agent in the df should be done as the last step in {@link #initializeAgent(jade.core.Agent)}
     */
    void register();

}
