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
public interface Notifyer {
    void notify(FloraEvent event);
    void addHandler(NotificationHandler handler);
}
