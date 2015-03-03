/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.logica.cns.flora.gui.FloraEvent;
import org.logica.cns.generic.CNSException;

/**
 *
 * @author eduard
 */
public class FileHandler implements NotificationHandler {
    
    private File events;
    private OutputStream out;

    public FileHandler(File events) throws FileNotFoundException {
        this.events = events;
        out = new FileOutputStream(events);
    }
    
    @Override
    public void handleNotification(FloraEvent event) {
        if (!event.getEventType().equals(FloraEvent.EVENTTYPE.GENERIC)) {
            try {
                out.write((event.toString() + System.getProperty("line.separator")).getBytes());
            } catch (IOException ex) {
                throw new CNSException("unable to write to file: " + events.getPath());
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        out.close();
    }

    
}
