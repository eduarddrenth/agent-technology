package org.logica.cns.generic;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Eduard Drenth: Logica, 22-dec-2009
 * 
 */
public class RegexMatchExpression implements MessageTemplate.MatchExpression {

    private static final Log log = LogFactory.getLog(RegexMatchExpression.class);

    private Pattern p = null;

    public RegexMatchExpression(String regex) {
        p = Pattern.compile(regex);
    }

    /**
     *
     * @param msg
     * @return true when the {@link ACLMessage#getContent() } matches the regex given in the Constructor.
     */
    public boolean match(ACLMessage msg) {
        if (p == null || msg == null || msg.getContent() == null) return false;
        if (log.isDebugEnabled()) {
            log.debug("skipping? ( "+msg.getContent()+" ): " + p.matcher(msg.getContent()).find());
        }
        return p.matcher(msg.getContent()).find();
    }


}
