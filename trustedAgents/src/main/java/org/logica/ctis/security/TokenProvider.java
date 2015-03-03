package org.logica.ctis.security;


import jade.core.AID;
import jade.core.VerticalCommand;
import jade.security.JADESecurityException;

/**
 * Implementors can for example follow a login procedure to retreive a token (sessionid,...) for the arguments.
 * @see SecurityService
 * @author Eduard Drenth: Logica, 6-feb-2010
 *
 */
public interface TokenProvider {

    /**
     *
     * @param command the command to be validated
     * @param agentId the id of the agent that is connected to the command to be validated
     * @return The token to be used for this command and this agentId.
     * @throws JADESecurityException
     * @see TokenValidator#isValid(java.lang.String, jade.core.VerticalCommand, jade.core.AID)
     */
    public String getToken(VerticalCommand command, AID agentId) throws JADESecurityException;
}
