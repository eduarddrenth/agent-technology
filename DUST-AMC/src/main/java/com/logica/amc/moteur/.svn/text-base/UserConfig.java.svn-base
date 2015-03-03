package com.logica.amc.moteur;

import com.logica.amc.base.StatusNotification;
import java.util.Iterator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author Eduard Drenth: Logica, 28-jan-2010
 * 
 */
public class UserConfig extends PropertiesConfiguration {

    public static final int FULLNAME = 0;
    public static final int EMAIL = 1;
    public static final int ADMINEMAIL = 2;

    public String getUserEmail(String event) {
        if (containsKey(event)) {
            return getStringArray(event)[EMAIL];
        } else {
            return null;
        }
    }
    public String getAdminEmail(String event) {
        if (containsKey(event)) {
            return getStringArray(event)[ADMINEMAIL];
        } else {
            return null;
        }
    }
    public String getFullName() {
        if (containsKey(StatusNotification.START)) {
            return getStringArray(StatusNotification.START)[FULLNAME];
        } else {
            return null;
        }
    }

    public UserConfig(String fileName) throws ConfigurationException {
        super(fileName);
        for (Iterator it = getKeys(); it.hasNext();) {
            String key = (String) it.next();
            if (getList(key).size() != 3) {
                throw new ConfigurationException("property " + key + " invalid, <key>=<fullname>,<email>,<adminemail>");
            }
        }
    }
}
