package org.logica.ctis.security;

import jade.core.AID;
import jade.core.VerticalCommand;
import jade.security.JADESecurityException;

public class DummyTokenValidator implements TokenValidator {

    public boolean isValid(String token, VerticalCommand commandName, AID objectName) throws JADESecurityException {
        System.out.println("--- Validating token "+token+" associated to agent "+objectName);
        return true;
    }

}
