package org.logica.cns.security;

import jade.core.AID;
import jade.core.VerticalCommand;
import jade.security.JADESecurityException;
import jade.util.leap.Properties;
import java.io.IOException;
import org.logica.cns.generic.JadeHelper;
import org.logica.ctis.security.TokenProvider;
/**
 *
 * @author Eduard Drenth: Logica, 29-apr-2010
 * 
 */
public class SimpleTokenProvider implements TokenProvider {

    private Properties p = new Properties();

    public static final String TOKENFILE = "TOKENFILE";
    public static final String DEFAULTTOKEN = "DEFAULT";

    public SimpleTokenProvider() throws IOException {
        p.load(JadeHelper.getProperty(TOKENFILE));
    }



    public String getToken(VerticalCommand cmd, AID aid) throws JADESecurityException {
        String key = aid.getName();
        if (p.containsKey(key)) {
            return p.getProperty(key);
        } else if (p.containsKey(DEFAULTTOKEN)) {
            return p.getProperty(DEFAULTTOKEN);
        } else {
            throw new JADESecurityException(key + " not secured");
        }
    }

}
