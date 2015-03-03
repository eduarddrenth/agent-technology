/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.logica.cns.generic.services;

import jade.core.VerticalCommand;

/**
 *
 * @author eduard
 */
public class MyService extends PublishService<String> {


    @Override
    protected boolean shouldPublish(VerticalCommand command, boolean direction) {
        return true;
    }

    @Override
    protected String doPublish(VerticalCommand command, boolean direction) {
        return command.getName();
    }


}
