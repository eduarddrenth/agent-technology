/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.generic;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author eduard
 */
public class CNSHelper {

    private CNSHelper() {
    }

    /**
     * {@link #executeWhenFound(jade.core.Agent, org.logica.cns.generic.ServiceFoundHandler, java.lang.String[]) }
     * @param a the agent that wants to execute 
     * @param afh
     * @param type
     */
    public static void executeWhenFound(final Agent a, final ServiceFoundHandler afh, String type) {
        executeWhenFound(a, afh, new String[] {type});
    }

    /**
     * {@link #executeWhenFound(jade.core.Agent, org.logica.cns.generic.ServiceFoundHandler, jade.domain.FIPAAgentManagement.ServiceDescription[]) }
     * @param a
     * @param afh
     * @param types
     */
    public static void executeWhenFound(final Agent a, final ServiceFoundHandler afh, String[] types) {
        ServiceDescription[] sds = new ServiceDescription[types.length];
        int i = 0;
        for (String t : types) {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(t);
            sds[i++] = sd;
        }
        executeWhenFound(a, afh, sds);
    }
    /**
     * Execute code when the DF, yellow pages, send a message that the combination of {@link ServiceDescription}s was found.
     * The subscription to the DF will be {@link DFService#createCancelMessage(jade.core.Agent, jade.core.AID, jade.lang.acl.ACLMessage) canceled} once services are found.
     * @param a
     * @param afh
     * @param sds
     */
    public static void executeWhenFound(final Agent a, final ServiceFoundHandler afh, ServiceDescription[] sds) {
        DFAgentDescription dad = new DFAgentDescription();
        for (ServiceDescription sd : sds) {
            dad.addServices(sd);
        }
        final ACLMessage msg = DFService.createSubscriptionMessage(a, a.getDefaultDF(), dad, null);
        SubscriptionInitiator sub = new SubscriptionInitiator(a, msg) {

            @Override
            protected void handleInform(ACLMessage inform) {
                ACLMessage cancel = DFService.createCancelMessage(a, a.getDefaultDF(), msg);
                a.send(cancel);
                try {
                    afh.handleFound(a, DFService.decodeNotification(inform.getContent()));
                } catch (FIPAException ex) {
                    afh.handleFIPAException(ex);
                }
            }
        };
        a.addBehaviour(sub);
    }

    /**
     * Creates a ACLMessage using {@link FIPANames.ContentLanguage#FIPA_SL} and {@link ACLMessage#INFORM}
     * @param receiver
     * @param ontology
     * @return
     */
    public static ACLMessage createInformMessage(AID receiver, String ontology) {
        return createMessage(receiver, ontology, FIPANames.ContentLanguage.FIPA_SL, ACLMessage.INFORM);
    }

    /**
     * Creates a ACLMessage using {@link FIPANames.ContentLanguage#FIPA_SL} and {@link ACLMessage#INFORM}
     * @param receivers
     * @param ontology
     * @return
     */
    public static ACLMessage createInformMessage(AID[] receivers, String ontology) {
        return createMessage(receivers, ontology, FIPANames.ContentLanguage.FIPA_SL, ACLMessage.INFORM);
    }

    /**
     * {@link #createMessage(jade.core.AID[], java.lang.String, java.lang.String, int) }
     * @param receiver
     * @param ontology
     * @param codec
     * @param predicate
     * @return
     */
    public static ACLMessage createMessage(AID receiver, String ontology, String codec, int predicate) {
        return createMessage(new AID[]{receiver}, ontology, codec, predicate);
    }

    /**
     * generic helper to create a basic ACLMessage
     * @param receivers
     * @param ontology {@link Ontology#getName() }
     * @param codec {@link Codec#getName() }
     * @param predicate {@link ACLMessage}
     * @return
     */
    public static ACLMessage createMessage(AID[] receivers, String ontology, String codec, int predicate) {
        ACLMessage msg = new ACLMessage(predicate);
        msg.setLanguage(codec);
        msg.setOntology(ontology);
        if (receivers != null) {
            for (AID aid : receivers) {
                msg.addReceiver(aid);
            }
        }
        return msg;
    }

    /**
     * sends a {@link #createMessage(jade.core.AID[], java.lang.String, java.lang.String, int) prepared mess} with a certain content.
     * @param sender
     * @param message
     * @param content
     * @deprecated using this method you will never get feedback on the arrival and/or processing of your message. Think in conversations
     * instead of messages, see the jade.proto package.
     */
    public static void sendMessage(Agent sender, ACLMessage message, ContentElement content) throws CodecException, OntologyException {
        sender.getContentManager().fillContent(message, content);
        sender.send(message);
    }

    /**
     * Constructs a description of an agent as used when {@link DFService#register(jade.core.Agent, jade.domain.FIPAAgentManagement.DFAgentDescription) } or
     * {@link DFService#search(jade.core.Agent, jade.domain.FIPAAgentManagement.DFAgentDescription) }
     * @param name
     * @param services
     * @return
     */
    public static DFAgentDescription constructDFAgentDescription(AID name, List<ServiceDescription> services) {
        DFAgentDescription dfad = new DFAgentDescription();
        dfad.setName(name);
        for (ServiceDescription s : services) {
            dfad.addServices(s);
        }
        return dfad;
    }

    /**
     * Calls {@link #constructDFAgentDescription(jade.core.AID, java.util.List) }
     * @param name
     * @param service
     * @return 
     */
    public static DFAgentDescription constructDFAgentDescription(AID name, ServiceDescription service) {
        List<ServiceDescription> l = new ArrayList<ServiceDescription>(1);
        l.add(service);
        return constructDFAgentDescription(name, l);
    }

    private static final String[] EMPTYSTRINGARRAY = new String[0];
    /**
     * {@link #createServiceDescription(java.lang.String, java.lang.String, java.lang.String[], java.lang.String[]) }
     * @param name
     * @param type
     * @return
     */
    public static ServiceDescription createServiceDescription(String name, String type) {
        return createServiceDescription(name, type, EMPTYSTRINGARRAY, EMPTYSTRINGARRAY);
    }

    /**
     * get a ServiceDescription for registration of an agent in the yellow pages (df), or for searching an agent in the df. Note that for
     * registration the name is obligatory
     * @param name
     * @param type
     * @param ontologies an array of ontologienames understood by the agent
     * @param languages an array of languages understood by the agent
     * @return
     */
    public static ServiceDescription createServiceDescription(String name, String type, String[] ontologies, String[] languages) {
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType(type);
        if (null != ontologies) {
            for (String s : ontologies) {
                sd.addOntologies(s);
            }
        }
        if (null != languages) {
            for (String s : languages) {
                sd.addOntologies(s);
            }
        }
        return sd;
    }

    /**
     * search for agent id's that are registered with a type
     * @param searcher
     * @param type
     * @return
     */
    public static AID[] findAgents(Agent searcher, String type) throws FIPAException {
        return findAgents(searcher, new String[]{type});
    }

    /**
     * search for agent id's that are registered with a set of types
     * @param searcher
     * @param types
     * @return
     */
    public static AID[] findAgents(Agent searcher, String[] types) throws FIPAException {
        ServiceDescription[] sds = new ServiceDescription[types.length];
        int i = 0;
        for (String t : types) {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(t);
            sds[i++] = sd;
        }
        return findAgents(searcher, sds);
    }

    /**
     * search for agent id's that are registered with at least one a set of types
     * @param searcher
     * @param types
     * @return
     */
    public static AID[] findAgentsOR(Agent searcher, String[] types) throws FIPAException {
        ServiceDescription[] sds = new ServiceDescription[types.length];
        int i = 0;
        for (String t : types) {
            ServiceDescription sd = new ServiceDescription();
            sd.setType(t);
            sds[i++] = sd;
        }
        return findAgentsOR(searcher, sds);
    }

    /**
     * {@link #findAgent(jade.core.Agent, java.lang.String[]) }
     * @param searcher
     * @param type
     * @return
     */
    public static AID findAgent(Agent searcher, String type) throws FIPAException {
        return findAgent(searcher, new String[]{type});
    }

    /**
     * search for an agent id that are registered with a type
     * @param searcher
     * @param types
     * @return
     */
    public static AID findAgent(Agent searcher, String[] types) throws FIPAException {
        ServiceDescription[] sds = new ServiceDescription[types.length];
        int i = 0;
        for (String s : types) {
            sds[i++] = createServiceDescription(null, s);
        }
        return findAgent(searcher, sds);
    }

    /**
     * search for an agent id that are registered with {@link ServiceDescription}s
     * @param searcher
     * @param criteria
     * @return
     */
    public static AID findAgent(Agent searcher, ServiceDescription[] criteria) throws FIPAException, CNSException {
        AID[] aids = findAgents(searcher, criteria);
        if (aids.length != 1) {
            String types = "types searched: ";
            for (ServiceDescription sd : criteria) {
                types += sd.getType() + ", ";
            }
            throw new CNSException(searcher.getName() + ", required only 1 result, found: " + Arrays.asList(aids) + ", " + types);
        }
        return aids[0];
    }

    /**
     * search for agent id's that are registered with {@link ServiceDescription}s
     * @param searcher
     * @param criteria
     * @return
     */
    public static AID[] findAgents(Agent searcher, ServiceDescription[] criteria) throws FIPAException {
        return findAgentsAND(searcher, criteria);
    }

    /**
     * search for agent id's that are registered with all {@link ServiceDescription}s
     * @param searcher
     * @param criteria
     * @return
     */
    public static AID[] findAgentsAND(Agent searcher, ServiceDescription[] criteria) throws FIPAException {
        List<AID> list = new ArrayList<AID>(3);
        DFAgentDescription template = new DFAgentDescription();
        for (ServiceDescription sd : criteria) {
            template.addServices(sd);
        }
        // hier kunnen we evt. cachen
        DFAgentDescription[] result = DFService.search(searcher, template);
        if (null != result) {
            for (int i = 0; i < result.length; ++i) {
                list.add(result[i].getName());
            }
        }
        return list.toArray(new AID[list.size()]);
    }

    /**
     * search for agent id's that are registered with at least one of the {@link ServiceDescription}s
     * @param searcher
     * @param criteria
     * @return
     */
    public static AID[] findAgentsOR(Agent searcher, ServiceDescription[] criteria) throws FIPAException {
        List<AID> list = new ArrayList<AID>(3);
        for (ServiceDescription sd : criteria) {
            DFAgentDescription template = new DFAgentDescription();
            template.addServices(sd);
            DFAgentDescription[] result = DFService.search(searcher, template);
            if (null != result) {
                for (int i = 0; i < result.length; ++i) {
                    list.add(result[i].getName());
                }
            }
        }
        return list.toArray(new AID[list.size()]);
    }
}
