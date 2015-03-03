package org.logica.cns.generic.pubsub;

/**
 * Responsible for 
 * @author eduard
 */
public interface EventPublisher<E extends Event> {
    
    boolean shouldPublish(E event);
    void publish(E event);
    
}
