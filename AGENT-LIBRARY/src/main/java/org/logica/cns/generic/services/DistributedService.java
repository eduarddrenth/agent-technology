/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.generic.services;

import jade.core.BaseService;
import jade.core.Filter;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.Service;
import jade.core.SliceProxy;
import jade.core.VerticalCommand;
import java.io.Serializable;

/**
 * This interface facilitates the propagation of information accross seperate containers, triggered by an incomming or outgoing {@link VerticalCommand}, which can be for example
 * sending or receiving a message.
 * @author eduard
 */
public interface DistributedService<T extends Serializable> extends Service.Slice {

    /**
     * The name of the {@link HorizontalCommand} that will be issued by the {@link SliceProxy} for this Service
     */
    public final static String PUBLISH_COMMAND = "PUBLISH_COMMAND";

    /**
     * The method that can be called by the {@link Filter} in the {@link BaseService Service}.
     * @param object
     * @param serviceName 
     * @throws IMTPException
     */
    public abstract void publish(T object, String serviceName) throws IMTPException;

}
