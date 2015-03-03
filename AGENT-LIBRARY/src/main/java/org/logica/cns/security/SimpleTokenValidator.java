package org.logica.cns.security;

import jade.core.AID;
import jade.core.ProfileException;
import jade.core.VerticalCommand;
import jade.security.JADESecurityException;
import jade.util.leap.Properties;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.JadeHelper;
import org.logica.ctis.security.TokenValidator;

/**
 *
 * @author Eduard Drenth: Logica, 29-apr-2010
 * 
 */
public class SimpleTokenValidator implements TokenValidator {
    private static final Log log = LogFactory.getLog(SimpleTokenValidator.class);
    private Properties p = new Properties();

    public static final String TOKENFILE = "TOKENFILE";
    public static final String DEFAULTTOKEN = "DEFAULT";
    public static final String ALLOWLOCAL = "ALLOWLOCAL";

    private boolean allowlocal = false;

    public SimpleTokenValidator() throws IOException {
        p.load(JadeHelper.getProperty(TOKENFILE));
        allowlocal = JadeHelper.hasProperty(ALLOWLOCAL) && Boolean.parseBoolean(JadeHelper.getProperty(ALLOWLOCAL));
    }


    @Override
    public boolean isValid(String token, VerticalCommand command, AID aid) throws JADESecurityException {
        boolean retval = false;
        String name = aid.getName().replaceFirst("@.*", "");
        if (allowlocal) {
            String cname = name.substring(name.lastIndexOf("_")+1);
            if (JadeHelper.getContainerName().equals(cname)) {
                if (log.isDebugEnabled()) {
                    log.debug("allowing local agent: " + aid);
                }
                retval = true;
            }
        }
        if (!retval) {
            String s = p.getProperty(name);
            if (s == null) {
                s = p.getProperty(DEFAULTTOKEN);
            }
            retval = s != null && s.equals(token);
        }
        if (!retval) {
            try {
                // somehow kill the agent
                JadeHelper.kill(aid);
            } catch (ProfileException ex) {
                log.error("failure killing illegal agent " + aid, ex);
            }
        }
        return retval;
    }

}
