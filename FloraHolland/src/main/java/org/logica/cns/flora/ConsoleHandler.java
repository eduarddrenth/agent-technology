/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora;

import org.logica.cns.flora.gui.FloraEvent;

/**
 *
 * @author eduard
 */
public class ConsoleHandler implements NotificationHandler {

    @Override
    public void handleNotification(FloraEvent event) {
        if (event.getEventType().equals(FloraEvent.EVENTTYPE.GENERIC)) {
            System.out.println(event.toString());
        }
    }
    
}
