package org.logica.cns.generic;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OntologyServer;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @see OntologyServer for a reflection based way to handle messages
 * @see MsgReceiver in the jade distribution
 * @deprecated see jade interaction protocols for a more generic and powerfull way of dealing with communication between agents
 * 
 * @author eduard
 */
public class CNSReceiveMessagesBehavior extends CyclicBehaviour {

    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(CNSReceiveMessagesBehavior.class);
    private boolean active = true;
    public static final String SKIPPED_CONTENT = "result.*query-platform-locations";
    private MessageTemplate template;
    private CNSMessageHandler messageHandler;


    /**
     *
     * @return true when the behavior is active
     */
    public final boolean isActive() {
        return active;
    }

    /**
     * when set to false, no messages are received
     * @param active
     */
    public final void setActive(boolean active) {
        this.active = active;
    }

    public CNSReceiveMessagesBehavior(Agent agent, CNSMessageHandler messageHandler) {
        super(agent);
	this.messageHandler=messageHandler;
    }

    /**
     * MessageTemplate used when receiving {@link ACLMessage}s. The MessageTemplate only allows messages understood.
     * This base version allows content in one of the {@link CNSAgent#fillLanguages(java.util.List) languages} and
     * in one of the {@link CNSAgent#fillOntologies(java.util.List) ontologies} of the agent.
     * @return
     */
    protected MessageTemplate getMessageTemplate() {
        if (template == null) {
            MessageTemplate mtl = null;
            for (String l : myAgent.getContentManager().getLanguageNames()) {
                if (null == mtl) {
                    mtl = MessageTemplate.MatchLanguage(l);
                } else {
                    mtl = MessageTemplate.or(mtl, MessageTemplate.MatchLanguage(l));
                }
            }
            MessageTemplate mto = null;
            for (String o : myAgent.getContentManager().getOntologyNames()) {
                if (null == mto) {
                    mto = MessageTemplate.MatchOntology(o);
                } else {
                    mto = MessageTemplate.or(mto, MessageTemplate.MatchOntology(o));
                }
            }
            log.warn(myAgent.getName()+": only receiving messages for one of ("
                    + Arrays.asList(myAgent.getContentManager().getLanguageNames()) + ") and one of ("
                    + Arrays.asList(myAgent.getContentManager().getOntologyNames()) + ")");
            template = MessageTemplate.and(MessageTemplate.and(mtl, mto), MessageTemplate.not(new MessageTemplate(new RegexMatchExpression(SKIPPED_CONTENT))));
        }
        return template;
    }

    /**
     * calls {@link CNSAgent#handleACLMessage(jade.lang.acl.ACLMessage) } when a message is received
     */
    public final void action() {
        if (active) {
            ACLMessage msg = myAgent.receive(getMessageTemplate());
            if (msg != null) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("received message " + msg.getContent());
                    }
                    messageHandler.handleACLMessage(msg,myAgent);
                } catch (UngroundedException e) {
                    log.error(msg.getContent(), e);
                } catch (CodecException e) {
                    log.error(msg.getContent(), e);
                } catch (OntologyException e) {
                    log.error(msg.getContent(), e);
                }

            } else {
                block();
            }
        } else {
            block();
        }
    }
}
