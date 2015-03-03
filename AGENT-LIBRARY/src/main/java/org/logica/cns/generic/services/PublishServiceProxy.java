/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.generic.services;

import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.ServiceException;
import jade.core.ServiceManager;
import jade.core.SliceProxy;
import java.io.Serializable;

/**
 * This proxy will be instantiated and initialized by the {@link ServiceManager} for the container. This class is being looked up by concatenating {@link BaseService#getName() } with "Proxy".
 * @author eduard
 */
public class PublishServiceProxy<T extends Serializable> extends SliceProxy implements DistributedService<T> {

    /**
     * This function builds a {@link GenericCommand} with the name {@link DistributedService#PUBLISH_COMMAND} and the {@link BaseService#getName() } of the service and sends it to the container by
     * calling {@link Node#accept(jade.core.HorizontalCommand) }.
     * @param object
     * @throws IMTPException
     */
    @Override
    public void publish(T object, String serviceName) throws IMTPException {
        GenericCommand cmd = new GenericCommand(PUBLISH_COMMAND, serviceName, null);
        cmd.addParam(object);
        try {
            getNode().accept(cmd);
        } catch (ServiceException ex) {
            throw new IMTPException("unable to publish", ex);
        }
    }

}
