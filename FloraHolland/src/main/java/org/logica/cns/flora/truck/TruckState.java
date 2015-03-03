package org.logica.cns.flora.truck;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import java.util.Random;
import org.logica.cns.flora.gui.TruckLocation;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.model.concepts.Order;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns.flora.Main;

/**
 * 
 * @author Eduard Drenth: Logica, 21-jun-2010
 * 
 */
public class TruckState extends FSMBehaviour {

    public static final String SEARCHING = "SEARCHING";
    public static final String LOADING = "LOADING";
    public static final String DRIVING = "MOVING";
    public static final String UNLOADING = "UNLOADING";
    public static final String FINISHED = "FINISHED";

    public static final int NOTHING_FOUND = 0;
    public static final int DONE = 1;

    private TruckAgent ta;
    
    private Searching searchState;
    
    private java.util.List<Order> load = new java.util.ArrayList<Order>();
    
    private Random r = new Random();


    public TruckState(Agent a) {
        super(a);

        ta = (TruckAgent) a;
        
        searchState = new Searching(ta);


        registerFirstState(searchState, SEARCHING);
        registerState(new Loading(a,500), LOADING);
        registerState(new Driving(ta,1000), DRIVING);
        registerState(new UnLoading(a,500), UNLOADING);
        registerLastState(new Finished(), FINISHED);

        registerTransition(SEARCHING, LOADING, DONE);
        registerTransition(SEARCHING, FINISHED, NOTHING_FOUND);
        registerTransition(LOADING,DRIVING, DONE);
        registerTransition(LOADING,FINISHED, 0);
        registerTransition(DRIVING,UNLOADING, DONE);
        registerTransition(UNLOADING,SEARCHING, NOTHING_FOUND);
        registerTransition(UNLOADING,FINISHED, DONE);
        
        TruckLocation tl = new TruckLocation(ta.getData());
        ta.notify(tl);
        
    }

    class Loading extends TickerBehaviour {

        public Loading(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onStart() {
            super.onStart();
            Auction a = new Auction();
            a.setId(searchState.getDestination());
            ta.getData().setTo(a);
            List l = new ArrayList();
            for (Order o : searchState.getOrders()) {
                l.add(o);
            }
            ta.getData().setLoad(l);
        }

        @Override
        protected void onTick() {
            if (searchState.getOrders().size() > 0) {
                ta.notify("loading orders " + searchState.getOrders());
                load.add(searchState.getOrders().remove(0));
            } else {
                stop();
            }
        }

        @Override
        public int onEnd() {
            return (ta.getData().getTo()!=null)?DONE:0;
        }


    }
    class UnLoading extends TickerBehaviour {

        public UnLoading(Agent a, long period) {
            super(a, period);
        }

        @Override
        public void onStart() {
            super.onStart();
            ta.arrive(ta.getData().getTo());
        }

        @Override
        protected void onTick() {
            if (ta.getData().getLoad().size() > 0) {
                ta.notify("unloading orders " + ta.getData().getLoad());
                ta.getData().getLoad().remove(0);
                
                org.logica.cns.flora.model.predicates.OrderArrived oap = new org.logica.cns.flora.model.predicates.OrderArrived();
                oap.setOrder(load.remove(0));
                ACLMessage msg = searchState.createMsg(oap.getOrder().getId(), ACLMessage.REQUEST, FIPANames.InteractionProtocol.FIPA_REQUEST);
                try {
                    myAgent.getContentManager().fillContent(msg, oap);
                    ta.addBehaviour(new AchieveREInitiator(myAgent, msg));
                } catch (CodecException ex) {
                    throw new CNSException("unable to unload order", ex);
                } catch (OntologyException ex) {
                    throw new CNSException("unable to unload order", ex);
                }
            } else {
                stop();
            }
        }

        @Override
        public int onEnd() {
            return DONE;
        }


    }
    private void again() {
        reset();
        load.clear();
        ta.addBehaviour(this);
    }
    
    @Override
    public void onStart() {
        try {
            Thread.sleep(Main.showRandomInteger(1,10, r) * 1000);
        } catch(InterruptedException ex) {}
    }

    class Finished extends OneShotBehaviour {

        @Override
        public void action() {
        }

        @Override
        public int onEnd() {
            if (ta.getData().isWorkingHours()) {
                ta.notify("still working.....waiting for " + (Integer.parseInt(JadeHelper.getProperty("TRUCKPAUZE"))/1000) + " seconds");
                ta.getData().setSpeed(0.1);
                try {
                    Thread.sleep(Integer.parseInt(JadeHelper.getProperty("TRUCKPAUZE")));
                } catch (InterruptedException ex) {
                }
                again();
            } else {
                ta.notify("going home.....");
            }
            return DONE;
        }
    }
}
