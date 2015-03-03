/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.flora.model.concepts;

import jade.content.Concept;
import jade.content.onto.annotations.SuppressSlot;

/**
 *
 * @author eduard
 */
public class Location implements Concept {
    
    private double latitude, longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    @SuppressSlot
    public double getDistance(Location other) {
        return Math.sqrt(Math.pow(getLatitude() - other.getLatitude(),2) + Math.pow(getLongitude() - other.getLongitude(),2));
    }
    
    @SuppressSlot
    public double getKilometerDistance(Location other) {
        return 77.716 * getDistance(other);
    }
}