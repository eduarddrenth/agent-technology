package org.logica.ctis.security;

import jade.core.AID;
import jade.core.VerticalCommand;
import jade.security.JADESecurityException;

/**
 * Implementors can for example call a isValid(token) function of an external LDAP.
 * @see SecurityService
 * @author Eduard Drenth: Logica, 6-feb-2010
 *
 */
public interface TokenValidator {

    /**
     *
     * @param token The token to be validated
     * @param command the command to be validated
     * @param agentId the id of the agent that is connected to the command to be validated
     * @return
     * @throws JADESecurityException
     */
    public abstract boolean isValid(String token, VerticalCommand command, AID agentId) throws JADESecurityException;

}
