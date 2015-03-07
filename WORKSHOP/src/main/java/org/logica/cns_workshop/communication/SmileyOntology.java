package org.logica.cns_workshop.communication;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.logica.cns_workshop.communication.SmileyVocabulary.*;

public class SmileyOntology extends Ontology {

   private static final long serialVersionUID = 1L;
   public static final String ONTOLOGY_NAME = SmileyOntology.class.getSimpleName();
   private static Log log = LogFactory.getLog(SmileyOntology.class);
   // singleton instance
   private static Ontology theInstance = new SmileyOntology();

   public static Ontology getInstance() {
      return theInstance;
   }

   // private constructor
   private SmileyOntology() {
      super(ONTOLOGY_NAME, BasicOntology.getInstance());
      try {

         // concepts
         add(new ConceptSchema(LOCATED), Located.class);
         add(new ConceptSchema(DOOR), Door.class);
         add(new ConceptSchema(SMILEY), Smiley.class);
         add(new ConceptSchema(ROOM), Room.class);

            // concept attributes
         ConceptSchema cs = (ConceptSchema) getSchema(LOCATED);
         cs.add(AID, (ConceptSchema) getSchema(BasicOntology.AID));
         cs.add(X_POS, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
         cs.add(Y_POS, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));

         cs = (ConceptSchema) getSchema(DOOR);
         cs.addSuperSchema((ConceptSchema) getSchema(LOCATED));
         cs.add(FOUNDSELF, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN));

         cs = (ConceptSchema) getSchema(ROOM);
         cs.addSuperSchema((ConceptSchema) getSchema(LOCATED));

         cs = (ConceptSchema) getSchema(SMILEY);
         cs.addSuperSchema((ConceptSchema) getSchema(LOCATED));
         cs.add(COLOR, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));
         cs.add(FOUNDDOOR, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN));

         // predicates
         PredicateSchema move = new PredicateSchema(NOTIFY_MOVE);
         move.add(SUBJECT, getSchema(LOCATED));
         move.add(X_MOVE, getSchema(BasicOntology.INTEGER));
         move.add(Y_MOVE, getSchema(BasicOntology.INTEGER));
         add(move, MovedTo.class);

         PredicateSchema location = new PredicateSchema(NOTIFY_LOCATION);
         location.add(SUBJECT, getSchema(LOCATED));
         location.add(SENDER, getSchema(LOCATED));
         add(location, LocatedAt.class);

         PredicateSchema reached = new PredicateSchema(NOTIFY_REACHEDDOOR);
         reached.add(SUBJECT, getSchema(LOCATED));
         add(reached, ReachedDoor.class);

         PredicateSchema neighbour = new PredicateSchema(YOURNEIGHBOUR);
         neighbour.add(SENDER, getSchema(LOCATED));
         neighbour.add(DOOR, getSchema(DOOR));
         neighbour.add(FOUNDDOOR, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN));
         add(neighbour, YourNeighbour.class);

         PredicateSchema ask = new PredicateSchema(ASK_WHEREISDOOR);
         ask.add(SMILEY, getSchema(SMILEY));
         ask.add(X_POS, getSchema(BasicOntology.INTEGER));
         ask.add(Y_POS, getSchema(BasicOntology.INTEGER));
         add(ask, WhereIsDoor.class);

      } catch (OntologyException e) {
         log.error(e);
      }
   }
}
