/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora;

import jade.core.AID;
import jade.core.Agent;
import jade.tools.sniffer.Sniffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.logica.cns.flora.buyer.BuyerAgent;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.order.OrderAgent;
import org.logica.cns.flora.truck.TruckAgent;
import org.logica.cns.generic.AgentCreator;
import org.logica.cns.generic.CNSException;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author eduard
 */
public class AgentGenerator implements Runnable {
    private List<Auction> auctions = new ArrayList<Auction>();
    private FloraAgent starter;

    public AgentGenerator(List<Auction> auctions, FloraAgent starter) {
        this.auctions = auctions;
        this.starter = starter;
    }

    private boolean doRun = true;

    private Random r = new Random();

    private int t = 0, o = 0;

    @Override
    public void run() {
        t=o=0;
        try {
            for (Auction auction : auctions) {
                Object[] oo = new Object[] { auction };
                String name = auction.getId().getLocalName();
                AgentCreator ag = JadeHelper.createAgent(starter, "Truck_" + name, JadeHelper.getContainerName(), TruckAgent.class.getName(), oo);
                while (ag.getAgentName() == null) {
                    starter.log.debug("waiting for Truck_" + name);
                    Thread.sleep(100);
                }
                AID aid = new AID(ag.getAgentName(),false);
                Main.snif(aid);
                
                ag = JadeHelper.createAgent(starter,"Buyer_" + name, JadeHelper.getContainerName(), BuyerAgent.class.getName(), oo);       
                while (ag.getAgentName() == null) {
                    starter.log.debug("waiting for Buyer_" + name);
                    Thread.sleep(100);
                }
                aid = new AID(ag.getAgentName(),false);
                Main.snif(aid);
            }
            while (doRun && t < 50) {
                t++;
                o++;

                int rand = r.nextInt();
                
                int deze = r.nextInt(6);
                
                Object[] oo = new Object[] { auctions.get(deze) };

                JadeHelper.createAgent(starter,OrderAgent.class.getSimpleName() + "_" + o, JadeHelper.getContainerName(), OrderAgent.class.getName(), oo);


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new CNSException("Unexpected exception", ex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CNSException("unable to start agents", e);
        }
    }

    public boolean isDoRun() {
        return doRun;
    }

    public void setDoRun(boolean doRun) {
        this.doRun = doRun;
    }

}
