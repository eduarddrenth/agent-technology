package org.logica.ctis.security;

import jade.core.*;
import jade.core.management.AgentManagementSlice;
import jade.security.JADESecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This simple SecurityService facilitates security for {@link VerticalCommand}s,
 * for example making sure only agents with a token that can be validated will
 * be allowed to join the Jade Platform. The way it works is that at a regular
 * container a token is retrieved and connected to an agent AID, at the
 * Main-Container (or mediator) the token is extracted from the AID and
 * validated. Retrieving a token and validating is a matter of implementing two
 * very simple interfaces. Validating can be done using for example an external
 * LDAP. The Service can contain a {@link TokenProvider} or a {@link TokenValidator}.
 * <ul> <li>A TokenProvider provides a token for a VerticalCommand issued by a
 * AID, the service then connects the token to the AID</li>
 *
 *
 * <li>A TokenValidator retrieves a token from a VerticalCommand issued by a
 * AID, the service then calls validate with the token, the command and the {@link AID}
 * as arguments</li> </ul> Building a TokenProvider or a Validator is left to
 * users of this security service. <ul> <li>configuration at main container</li>
 * </ul>
 * <pre>
 * services=org.logica.ctis.security.SecurityService(true)
 * org.logica.ctis.security.SecurityService_trustedAgentNames=ams;rma;df
 * org.logica.ctis.security.SecurityService_tokenvalidator=validatorClassname
 * </pre> <ul> <li>configuration at 'client' container</li> </ul>
 * <pre>
 * services=org.logica.ctis.security.SecurityService
 * org.logica.ctis.security.SecurityService_tokenprovider=providerClassname
 * </pre> <ul> <li>configuration at split 'client' container</li> </ul>
 * <pre>
 * be-required-services=org.logica.ctis.security.SecurityService
 * At the runtime where the 'mediator' lives in <b>leap.properties</b>: org.logica.ctis.security.SecurityService_tokenprovider=providerClassname
 * or you can do validation at the mediator: org.logica.ctis.security.SecurityService_tokenvalidator=validatorClassname
 * when doing validation at the mediator you probably won't use validation at the main-container
 * </pre>
 *
 * @author Eduard Drenth: Logica, 5-match-2012
 *
 */
public class SecurityService extends BaseService {

    public static final String CTIS_SECURITY_SERVICE = "ctis security service";
    /**
     * Use in combination with a {@link TokenValidator}. Property to provide {@link AID#getLocalName() localnames}
     * separated by a ";" provided in this property will always be 'trusted'.
     */
    public static final String CTIS_TRUSTED_AGENT_NAMES = "org.logica.ctis.security.SecurityService_trustedAgentNames";
    /**
     * classname of a {@link TokenValidator} to be used
     */
    public static final String CTIS_TOKEN_VALIDATOR_CLASS = "org.logica.ctis.security.SecurityService_tokenvalidator";
    /**
     * classname of a {@link TokenProvider} to be used
     */
    public static final String CTIS_TOKEN_PROVIDER_CLASS = "org.logica.ctis.security.SecurityService_tokenprovider";
    static final String TOKENKEY = "TOKENKEY";
    private static final Logger log = Logger.getLogger(SecurityService.class.getName());
    private TokenValidator validator = null;
    private TokenProvider provider = null;
    private List<String> trustedAgents = new ArrayList<String>();

    /**
     * In this init the {@link TokenValidator} or {@link TokenProvider} is
     * initialized, as well as the {@link #trustedAgents}.
     *
     * @param ac
     * @param p
     * @throws ProfileException
     */
    @Override
    public void init(AgentContainer ac, Profile p) throws ProfileException {
        super.init(ac, p);
        if (p.getParameter(CTIS_TRUSTED_AGENT_NAMES, null) != null) {
            for (Object o : Specifier.parseList(p.getParameter(CTIS_TRUSTED_AGENT_NAMES, null), ';')) {
                String s = (String) o;
                if (s.length() > 0) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("trusting: " + s);
                    }
                    trustedAgents.add(s);
                }
            }
        }
        if (p.getParameter(CTIS_TOKEN_VALIDATOR_CLASS, null) != null) {
            try {
                Class validatorClass = Class.forName(p.getParameter(CTIS_TOKEN_VALIDATOR_CLASS, null));
                validator = (TokenValidator) validatorClass.newInstance();
                log.info("using token validator: " + p.getParameter(CTIS_TOKEN_VALIDATOR_CLASS, null));
                if (validator instanceof ConfigurableTokenValidator) {
                    ((ConfigurableTokenValidator) validator).init(p);
                }
            } catch (ClassNotFoundException ex) {
                throw new ProfileException("error loading validator", ex);
            } catch (InstantiationException ex) {
                throw new ProfileException("error loading validator", ex);
            } catch (IllegalAccessException ex) {
                throw new ProfileException("error loading validator", ex);
            }
        } else if (p.getParameter(CTIS_TOKEN_PROVIDER_CLASS, null) != null) {
            try {
                Class providerClass = Class.forName(p.getParameter(CTIS_TOKEN_PROVIDER_CLASS, null));
                provider = (TokenProvider) providerClass.newInstance();
                log.info("using token provider: " + p.getParameter(CTIS_TOKEN_PROVIDER_CLASS, null));
                if (provider instanceof ConfigurableTokenProvider) {
                    ((ConfigurableTokenProvider) provider).init(p);
                }
            } catch (ClassNotFoundException ex) {
                throw new ProfileException("error loading provider", ex);
            } catch (InstantiationException ex) {
                throw new ProfileException("error loading provider", ex);
            } catch (IllegalAccessException ex) {
                throw new ProfileException("error loading provider", ex);
            }
        } else {
            throw new ProfileException("need to provide either " + CTIS_TOKEN_PROVIDER_CLASS + " or " + CTIS_TOKEN_VALIDATOR_CLASS);
        }
    }

    public String getName() {
        return CTIS_SECURITY_SERVICE;
    }

    /**
     * if this method returns true security will be applied. This implementation
     * applies security to {@link AgentManagementSlice#INFORM_CREATED}
     *
     * @param cmd the command that is checked
     * @param inOrOut the direction for the command, see {@link Filter#INCOMING}
     * and {@link Filter#OUTGOING}
     * @return true when security should be applied
     */
    protected boolean applySecurity(VerticalCommand cmd, boolean inOrOut) {
        return cmd.getName().equals(AgentManagementSlice.INFORM_CREATED);
    }

    private class InFilter extends Filter {

        @Override
        protected boolean accept(VerticalCommand cmd) {
            String name = cmd.getName();
            if (applySecurity(cmd, INCOMING)) {
                AID a = getFromCmd(cmd);
                if (a != null) {
                    String ln = a.getLocalName();
                    if (trustedAgents.contains(ln)) {
                        return true;
                    }
                    try {
                        return validator.isValid((String) a.getAllUserDefinedSlot().get(TOKENKEY), cmd, a);
                    } catch (JADESecurityException ex) {
                        log.log(Level.SEVERE, "error during token validation", ex);
                        return false;
                    }
                }
                log.log(Level.SEVERE, "error during token validation");
                return false;
            } else {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("not handled: " + name);
                }
            }
            return true;
        }
    }
    
    private AID getFromCmd(VerticalCommand cmd) {
        return (cmd.getParams().length > 0 && cmd.getParam(Filter.FIRST) instanceof AID)
                            ? (AID) cmd.getParam(Filter.FIRST)
                            : null;
    }

    private class OutFilter extends Filter {

        @Override
        protected boolean accept(final VerticalCommand cmd) {
            if (applySecurity(cmd, OUTGOING)) {
                try {
                    AID a = getFromCmd(cmd);
                    if (a != null) {
                        a.addUserDefinedSlot(TOKENKEY, provider.getToken(cmd, a));
                        return true;
                    }
                } catch (JADESecurityException ex) {
                    log.log(Level.SEVERE, "error providing token", ex);
                    return false;
                }
            } else {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("not handled: " + cmd.getName());
                }
            }
            return true;
        }
    }
    private Filter inFilter = new InFilter();
    private Filter outFilter = new OutFilter();

    @Override
    public final Filter getCommandFilter(boolean direction) {
        if (validator != null && direction == Filter.INCOMING) {
            return inFilter;
        } else if (provider != null && direction == Filter.OUTGOING) {
            return outFilter;
        } else {
            return null;
        }
    }

    /**
     *
     * @return true
     */
    // todo wait for Jade version 4 @Override
    public boolean isMandatory() {
        return true;
    }
}
