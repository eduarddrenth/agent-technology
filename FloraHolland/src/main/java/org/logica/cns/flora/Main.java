/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.SniffOn;
import jade.lang.acl.ACLMessage;
import jade.tools.sniffer.Sniffer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.logica.cns.flora.auction.AuctionAgent;
import org.logica.cns.flora.model.concepts.Auction;
import org.logica.cns.flora.traject.TrajectAgent;
import org.logica.cns.generic.CNSHelper;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author eduard
 */
public class Main {
    
    static List<NotificationHandler> handlers = new ArrayList<NotificationHandler>();
    
    public static final String AALSMEEER = "Aalsmeer";
    public static final String NAALDWIJK = "Naaldwijk";
    public static final String RIJNSBURG = "Rijnsburg";
    public static final String RHEINMAAS = "Veiling Rhein-Maas";
    public static final String BLEISWIJK = "Bleiswijk";
    public static final String EELDE = "Eelde";
    
    public static final List<String> VEILINGEN = Collections.unmodifiableList(Arrays.asList(new String[] {AALSMEEER,NAALDWIJK,RIJNSBURG,RHEINMAAS,BLEISWIJK,EELDE}));
    
        // sniifer erbij
   private static Sniffer sniffer = new Sniffer();

    public static void main(String[] args) throws Exception {
        JadeHelper.launchJade(args[0]);
        
        handlers.add(new ConsoleHandler());
        handlers.add(new FileHandler(new File(JadeHelper.getProperty("GUIEVENTS"))));

        
        JadeHelper.acceptLocalAgent(sniffer, "sniffer");
        
//        Thread.sleep(2000);
        
        List<Auction> l = new ArrayList<Auction>();
        FloraAgent starter = null;
        for (int i = 0; i < 6; i++) {
            AuctionAgent a = new AuctionAgent();
            if (starter==null) starter = a;
            // 6 VEILINGEN
            JadeHelper.acceptLocalAgent(a, VEILINGEN.get(i));
            while (a.getData()==null) {
                Thread.sleep(500);
            }
            l.add(a.getData());
            snif(a.getAID());
            
        }
        
        List<Auction> m = new ArrayList<Auction>();

//        // de trajecten
//        for (Auction auction : l) {
//            if (m.contains(auction)) {
//                continue;
//            }
//            for (Auction auction1 : l) {
//                if (auction.equals(auction1)) {
//                    continue;
//                }
//                Auction[] a = new Auction[] {auction, auction1};
//                JadeHelper.startLocalAgent(auction.getId().getLocalName() + "_" + auction1.getId().getLocalName(), TrajectAgent.class.getName(), a);
//            }
//            m.add(auction);
//        }


        // start creating trucks and orders
        new Thread(new AgentGenerator(l,starter)).start();
        

    }
    
    static void snif(AID agent) throws CodecException, OntologyException {
        SniffOn snif = new SniffOn();
        snif.addSniffedAgents(agent);
        snif.setSniffer(sniffer.getAID());
        
        Action action = new Action();
        action.setAction(snif);
        action.setActor(sniffer.getAID());
        
        ACLMessage msg = CNSHelper.createMessage(sniffer.getAID(), JADEManagementOntology.NAME, FIPANames.ContentLanguage.FIPA_SL, ACLMessage.REQUEST);
        sniffer.getContentManager().fillContent(msg, action);
        sniffer.send(msg);
        
    }
    
  public static int showRandomInteger(int aStart, int aEnd, Random aRandom){
    if ( aStart > aEnd ) {
      throw new IllegalArgumentException("Start cannot exceed End.");
    }
    //get the range, casting to long to avoid overflow problems
    long range = (long)aEnd - (long)aStart + 1;
    // compute a fraction of the range, 0 <= frac < range
    long fraction = (long)(range * aRandom.nextDouble());
    return  (int)(fraction + aStart);    
  }
}
