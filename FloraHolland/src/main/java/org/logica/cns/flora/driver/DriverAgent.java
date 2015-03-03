/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.driver;

import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.model.concepts.Driver;

/**
 *
 * @author eduard
 */
public class DriverAgent extends FloraAgent<Driver>{

    @Override
    protected void setup() {
        super.setup();
        Driver d = new Driver();
        d.setId(getAID());
        setData(d);
    }



}
