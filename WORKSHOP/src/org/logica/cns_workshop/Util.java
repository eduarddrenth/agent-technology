package org.logica.cns_workshop;

/**
 *
 * @author Eduard Drenth: Logica, 7-jan-2010
 * 
 */
public class Util {

    private Util() {}

    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.pow(x1-x2,2) + Math.pow(y1-y2,2);
    }
}
