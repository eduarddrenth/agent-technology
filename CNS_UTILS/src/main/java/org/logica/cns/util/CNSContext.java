package org.logica.cns.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.CNSAgent;
import org.logica.cns.generic.JadeHelper;

public class CNSContext implements Serializable {

    public static final String KEY_VALUE_SEPERATOR = "|";
    
    private static Log log = LogFactory.getLog(CNSContext.class);
    private static final long serialVersionUID = 1L;
    private Map<String, String> context_parameters = new HashMap<String, String>();

    /**
     * create a context for an agent in a container
     */
    private CNSContext() {
    }


    public String getParameter(String key) {
        if (context_parameters.get(key) != null) {
            return context_parameters.get(key);
        } else {
            log.warn("param " + key + " does not exist in " + JadeHelper.getContainerName());
            return null;
        }
    }

    public boolean hasParameter(String key) {
        return context_parameters.get(key) != null;
    }

    public int getParameterAsInt(String key) {
        return Integer.parseInt(getParameter(key));
    }

    public long getParameterAsLong(String key) {
        return Long.parseLong(getParameter(key));
    }

    public void addParameter(String name, String value) {
        if (context_parameters.containsKey(name)) {
            throw new IllegalArgumentException("key " + name + " already there");
        }
        context_parameters.put(name, value);
        if (log.isDebugEnabled()) {
            log.debug("add context parameter (" + name + "," + value + ")");
        }
    }

    public int getNumParams() {
        return context_parameters.size();
    }

    public Iterator getParameters() {
        return context_parameters.entrySet().iterator();
    }

    /**
     *
     * @return a String[] that can be used as arguments when creating an Agent
     * @see CNSAgent#getArguments()
     */
    public String[] toArgs() {
        String[] args = new String[getNumParams()];
        int i = -1;
        for (Iterator it = getParameters(); it.hasNext();) {
            Map.Entry e = (Entry) it.next();
            args[++i] = makeParameterString((String)e.getKey(), (String)e.getValue());
        }
        return args;
    }

    /**
     * construct a context from the arguments to an agent
     * @param args
     * @return
     */
    public static CNSContext fromArgs(String[] args) {
        CNSContext c = new CNSContext();
        for (String s : args) {
            String[] param = s.split("[" + KEY_VALUE_SEPERATOR + "]", 2);
            c.addParameter(param[0], param[1]);
        }
        return c;
    }

    /**
     * Preferred way to construct a key - value pair String.
     * @param key
     * @param value
     * @return
     * @see #KEY_VALUE_SEPERATOR
     */
    public static String makeParameterString(String key, String value) {
        return key + KEY_VALUE_SEPERATOR + value;
    }
}
