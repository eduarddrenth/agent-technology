/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.flora.model.concepts;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

/**
 *
 * @author eduard
 */
public class Size implements Concept {

    private float length, width, height, weight;

    @Slot(mandatory=true)
    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    @Slot(mandatory=true)
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Slot(mandatory=true)
    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Slot(mandatory=true)
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }


}
