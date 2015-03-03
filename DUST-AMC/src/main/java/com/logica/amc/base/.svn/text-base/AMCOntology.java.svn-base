package com.logica.amc.base;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.TermSchema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Eduard Drenth: Logica, 21-okt-2009
 * 
 */
public class AMCOntology extends Ontology implements AMCVocabulary {

    private static AMCOntology me = null;

    private static Log log = LogFactory.getLog(AMCOntology.class);
    private static final long serialVersionUID = 1L;
    public static final String ONTOLOGY_NAME = AMCOntology.class.getSimpleName();

    private AMCOntology() {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try {

            ConceptSchema cs = new ConceptSchema(STATUS);
            cs.add(DATE, (TermSchema) getSchema(BasicOntology.DATE));
            cs.add(MESSAGE, (TermSchema) getSchema(BasicOntology.STRING));
            cs.add(TYPE, (TermSchema) getSchema(BasicOntology.STRING));
            cs.add(WORKFLOW, (TermSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
            cs.add(JOBID, (TermSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
            cs.add(DETAILS, (TermSchema) getSchema(BasicOntology.STRING),ObjectSchema.OPTIONAL);
            cs.add(EMAILS, (TermSchema) getSchema(BasicOntology.STRING),0,2);
            add(cs, StatusNotification.class);

            PredicateSchema p = new PredicateSchema((STATUSIS));
            p.add(STATUS, cs);
            add(p,StatusIs.class);

            PredicateSchema j = new PredicateSchema(JOBNOTIFICATION);
            j.add(JOBID, getSchema(BasicOntology.STRING));
            add(j,JobIdNotification.class);

            PredicateSchema w = new PredicateSchema(AGENTSREADY);
            w.add(WORKFLOW, getSchema(BasicOntology.STRING));
            add(w,AgentsPrepared.class);

        } catch (OntologyException ex) {
            log.error("wrong ontology", ex);
        }
    }
    
    public static final synchronized AMCOntology getInstance() {
        if (null == me) {
            me = new AMCOntology();
        }
        return me;
    }

    
}
