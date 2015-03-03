/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.congestion;

import org.logica.cns.flora.FloraAgent;
import org.logica.cns.flora.model.concepts.Congestion;

/**
 *
 * @author eduard
 */
public class CongestionAgent extends FloraAgent<Congestion> {

    @Override
    protected void setup() {
        super.setup();
        Congestion g = new Congestion();
        g.setId(getAID());
        setData(g);
    }



}
