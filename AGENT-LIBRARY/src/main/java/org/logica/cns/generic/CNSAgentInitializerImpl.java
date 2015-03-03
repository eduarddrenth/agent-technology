package org.logica.cns.generic;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Default implementation, probably no need to override this. Use an instance for each agent.
 *
 * @author Eduard Drenth: Logica, 15-mrt-2010
 * 
 */
public class CNSAgentInitializerImpl implements CNSAgentInitializer {

    private Agent agent = null;
    private final List<Ontology> ontologies = new ArrayList<Ontology>(2);
    private final List<Codec> codecs = new ArrayList<Codec>(1);
    private final List<ServiceDescription> servicedescriptions = new ArrayList<ServiceDescription>(3);
    private Behaviour receiver = null;

    public CNSAgentInitializerImpl(Agent agent) {
        this.agent = agent;
    }


    @Override
    public void initializeAgent() {

        ContentManager cm = agent.getContentManager();

        for (Ontology og : ontologies) {
            cm.registerOntology(og);
        }
        for (Codec co : codecs) {
            cm.registerLanguage(co);
        }

        if (receiver != null) {
            agent.addBehaviour(receiver);
        }


        register();

    }

    @Override
    public void addOntology(Ontology o) {
        ontologies.add(o);
    }

    @Override
    public void addLanguage(Codec c) {
        codecs.add(c);
    }

    @Override
    public void addServiceDescription(ServiceDescription s) {
        servicedescriptions.add(s);
    }

    @Override
    public void register() {
        agent.addBehaviour(new RegisterBehavior(agent, 500, CNSHelper.constructDFAgentDescription(agent.getAID(), servicedescriptions)));
    }

    @Override
    public void setMessageReceiver(Behaviour receiver) {
        this.receiver = receiver;
    }

}
