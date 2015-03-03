/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.order;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.util.leap.Iterator;
import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.Main;
import org.logica.cns.flora.gui.OrderLocation;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Order;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.CNSHelper;

/**
 *
 * @author eduard
 */
public class OrderAgent extends FloraAgent<Order> {
    public static final String BUYME = "BUYME";

    @Override
    protected void setup() {
        super.setup();
        Auction auction = parseArguments();
        Order state = new Order();
        state.setId(getAID());
        state.setFrom(auction);
        // todo init
        setData(state);
        
        int van = Main.VEILINGEN.indexOf(auction.getId().getLocalName());

        publishLocation(auction, LOCATION_TYPE.AT);
        publishLocation(auction, LOCATION_TYPE.FROM);
        
        addBehaviour(new Waiting(this, AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST)));
        
        OrderLocation location = new OrderLocation(state);
        notify(location);

    }
    
    
    public void buy(Auction auction) {
        publishLocation(auction, LOCATION_TYPE.TO);
        publishBought();
        getData().setTo(auction);
    }

    public void gone() {
        publishGone(getData().getFrom(), LOCATION_TYPE.FROM);
        publishGone(getData().getFrom(), LOCATION_TYPE.AT);
    }

    public void arrive(Auction auction) {
        getData().setFrom(auction);
        getData().setTo(null);
        publishLocation(auction, LOCATION_TYPE.AT);
    }
    
    private void publishBought() throws CNSException {
        try {
            DFAgentDescription dfa = findMySelf();
            ServiceDescription sd = null;
            for (Iterator it = dfa.getAllServices(); it.hasNext();) {
                sd = (ServiceDescription) it.next();
                if (sd.getName().equals(BUYME)) {
                    break;
                }
            }
            if (sd!=null&&dfa.removeServices(sd)) {
                log.debug("not for sale");
            }
            DFService.modify(this,dfa);
        } catch (FIPAException ex) {
            throw new CNSException("unable to remove buyme for " + getAID(), ex);
        }
    }

}
