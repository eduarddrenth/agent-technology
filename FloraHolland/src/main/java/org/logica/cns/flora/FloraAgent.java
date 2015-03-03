/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.util.leap.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.flora.auction.AuctionAgent;
import org.logica.cns.flora.buyer.BuyerAgent;
import org.logica.cns.flora.gui.AbsFloraEvent;
import org.logica.cns.flora.gui.FloraEvent;
import org.logica.cns.flora.gui.FloraEvent.EVENTTYPE;
import org.logica.cns.flora.gui.FloraEvent.OBJECTTYPE;
import org.logica.cns.flora.model.FloraOntology;
import org.logica.cns.flora.model.concepts.AgentState;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Location;
import org.logica.cns.flora.order.OrderAgent;
import org.logica.cns.flora.truck.TruckAgent;
import org.logica.cns.generic.CNSAgentInitializer;
import org.logica.cns.generic.CNSAgentInitializerImpl;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.CNSHelper;

/**
 *
 * @author eduard
 */
public class FloraAgent<S extends AgentState> extends Agent implements Notifyer {
    
    private DFAgentDescription dfa = null;
    private int eventSequence = 0;
    
    public void notify(String txt) {
        AbsFloraEvent<AgentState> event = new AbsFloraEvent<AgentState>(getData()) {
           private Location loc = new Location();

            @Override
            public EVENTTYPE getEventType() {
                return EVENTTYPE.GENERIC;
            }

            @Override
            public OBJECTTYPE getObjectType() {
                if (FloraAgent.this instanceof TruckAgent) {
                    return OBJECTTYPE.TRUCK;
                } else if (FloraAgent.this instanceof OrderAgent) {
                    return OBJECTTYPE.ORDER;
                } else if (FloraAgent.this instanceof BuyerAgent) {
                    return OBJECTTYPE.BUYER;
                } else if (FloraAgent.this instanceof AuctionAgent) {
                    return OBJECTTYPE.AUCTION;
                } else {
                    throw new CNSException(FloraAgent.this.getClass().getName() + " not supported");
                }
            }

            @Override
            public Location getLocation() {
                return loc;
            }

            @Override
            public String toString() {
                return String.format("%s: %s\n",getState().getId().getLocalName(),getMsg());
            }
            
            
        };
        event.setMsg(txt);
        notify(event);
    }
    
    @Override
    public void notify(FloraEvent event) {
        synchronized(Main.handlers) {
           event.setSequence(eventSequence++);
            for (NotificationHandler notificationHandler : Main.handlers) {
                notificationHandler.handleNotification(event);
            }
        }
    }

    @Override
    public void addHandler(NotificationHandler handler) {
        synchronized(Main.handlers) {
            Main.handlers.add(handler);
        }
    }
    
    

    /**
     * Used by {@link #publishLocation(org.logica.cns.flora.model.concepts.Auction) }
     */
    public enum LOCATION_TYPE {
        FROM, TO, AT
    }
    
    

    /**
     * can be used for logging
     */
    public static final Log log = LogFactory.getLog(FloraAgent.class);
    private S data;

    @Override
    protected void setup() {
        super.setup();
        CNSAgentInitializer init = new CNSAgentInitializerImpl(this);
        // we will use this grammar
        init.addOntology(FloraOntology.getInstance());
        // use default codec
        init.addLanguage(new SLCodec());
        // register an agent using its class name
        init.addServiceDescription(CNSHelper.createServiceDescription(getClass().getSimpleName(), getClass().getSimpleName()));
        if (this instanceof OrderAgent) {
            init.addServiceDescription(CNSHelper.createServiceDescription(OrderAgent.BUYME,OrderAgent.BUYME));
        }
        init.initializeAgent();
        if (this instanceof TruckAgent || this instanceof BuyerAgent) {
            FloraGUI gui = new FloraGUI(getAID());
            gui.setTitle(getLocalName());
            addHandler(gui);
            gui.setVisible(true);
        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            log.warn("unable to deregister", ex);
        }
    }

    /**
     * return the state of this agent
     * @return 
     */
    public S getData() {
        return data;
    }

    /**
     * subclasses can call this from {@link #setup() } to initialize their state
     * @param data 
     */
    protected void setData(S data) {
        this.data = data;
    }
    
    protected DFAgentDescription findMySelf() throws FIPAException {
         if (null==dfa) {
            dfa = new DFAgentDescription();
            dfa.setName(getAID());
            DFAgentDescription[] found = DFService.search(this, dfa);
            if (null==found||found.length!=1) {
                throw new CNSException("Didn't find myself: " + getAID().getLocalName());
            }
            this.dfa = found[0];
         }
        return dfa;
    }
    
    /**
     * publish a location for an agent to the yellow pages
     * @param auction the auction to publish
     * @param type to, from or at
     * @throws CNSException 
     */
    protected void publishLocation(Auction auction, LOCATION_TYPE type) throws CNSException {
        try {
            DFAgentDescription dfa = findMySelf();
            dfa.addServices(CNSHelper.createServiceDescription(
                            auction.getId().getLocalName(),
                            type.toString()
                        )
                    );
            DFService.modify(this, dfa);
        } catch (FIPAException ex) {
            throw new CNSException("unable to publish location for " + getAID(), ex);
        }
    }

    /**
     * deregister a location from the yellow pages
     * @param auction the auction to deregister
     * @param type to, from or at
     * @throws CNSException 
     */
    protected void publishGone(Auction auction, LOCATION_TYPE type) throws CNSException {
        try {
            DFAgentDescription dfa = findMySelf();
            ServiceDescription sd = null;
            for (Iterator it = dfa.getAllServices(); it.hasNext();) {
                sd = (ServiceDescription) it.next();
                if (sd.getName().equals(auction.getId().getLocalName()) && sd.getType().equals(type.toString())) {
                    break;
                }
            }
            if (sd!=null&&dfa.removeServices(sd)) {
                log.debug("left " + auction.getId().getLocalName());
            }
            DFService.modify(this,dfa);
        } catch (FIPAException ex) {
            throw new CNSException("unable to remove location for " + getAID(), ex);
        }
    }
    
    protected Auction parseArguments() throws CNSException {
        if (null == getArguments() || getArguments().length == 0 || !(getArguments()[0] instanceof Auction)) {
            throw new CNSException("A " + getClass().getSimpleName() + " needs an auction as the first argument");
        }
        return (Auction) getArguments()[0];
    }
}
