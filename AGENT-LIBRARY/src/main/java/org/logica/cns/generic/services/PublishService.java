/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.logica.cns.generic.services;

import jade.core.BaseService;
import jade.core.Filter;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.VerticalCommand;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The actual Service that is configured in the usual way (services=org.logica.cns.generic.services.PublishService).
 *
 * @author eduard
 */
public abstract class PublishService<T extends Serializable> extends BaseService {

    private Service.Slice localSlice = new DistributedServiceImpl();

    /**
     * getClass().getName()
     * @return getClass().getName()
     */
    @Override
    public final String getName() {
        return getClass().getName();
    }

    /**
     *
     * @return
     */
    @Override
    public final Class<T> getHorizontalInterface() {
        return (Class<T>) DistributedService.class;
    }

    @Override
    public final Slice getLocalSlice() {
        return localSlice;
    }

    /**
     * determine if a certain command should be published
     * @param command
     * @param direction {@link Filter#INCOMING} or {@link Filter#OUTGOING}
     * @return
     */
    protected abstract boolean shouldPublish(VerticalCommand command, boolean direction);

    /**
     * Called when {@link #shouldPublish(jade.core.VerticalCommand, boolean) } returns true. Publishing is only towards other containers than this one.
     * @param command
     * @param direction
     * @return An object to publish or null (null will not be published
     */
    protected abstract T doPublish(VerticalCommand command, boolean direction);

    private Filter outFilter = new InOutFilter(Filter.OUTGOING);
    private Filter inFilter = new InOutFilter(Filter.INCOMING);

    private class InOutFilter extends Filter {

        private boolean direction;

        public InOutFilter(boolean direction) {
            this.direction = direction;
        }

        @Override
        protected boolean accept(VerticalCommand cmd) {
            if (shouldPublish(cmd, direction)) {
                try {
                    Slice[] slices = getAllSlices();
                    if (null != slices) {
                        for (Slice slice : slices) {
                            if (!getLocalNode().getName().equals(slice.getNode().getName())) {
                                T data = doPublish(cmd, direction);
                                if (null != data) {
                                    ((DistributedService) slice).publish(data, getName());
                                }
                            }
                        }
                    }
                } catch (IMTPException ex) {
                    Logger.getLogger(PublishService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ServiceException ex) {
                    Logger.getLogger(PublishService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return true;
        }


    }

    /**
     * returns a filter for incomming and outgoing commands.
     * @param direction
     * @return
     */
    @Override
    public final Filter getCommandFilter(boolean direction) {
        if (direction == Filter.OUTGOING) {
            return outFilter;
        } else {
            return inFilter;
        }
    }

    /**
     * Called whenever data is published from another container, prints to sysout
     * @param published
     */
    protected void handlePublished(T published) {
        System.out.println(published);
    }



    private class DistributedServiceImpl implements Service.Slice {

        @Override
        public Service getService() {
            return PublishService.this;
        }

        @Override
        public Node getNode() throws ServiceException {
            try {
                return getLocalNode();
            } catch (IMTPException ex) {
                throw new ServiceException("unable to get local node", ex);
            }
        }

        @Override
        public VerticalCommand serve(HorizontalCommand cmd) {
            if (cmd.getName().equals(DistributedService.PUBLISH_COMMAND)) {
                handlePublished((T)cmd.getParams()[0]);
            }
            return null;
        }
    }
}
