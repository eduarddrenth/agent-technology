package com.logica.amc.moteur;

import com.logica.amc.base.SQLiteDB;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author Eduard Drenth: Logica, 27-jan-2010
 * 
 */
class ContextHelper {
    public static final String UNKNOWN = "unknown";


    private ContextHelper() {
    }

    private static final Log log = LogFactory.getLog(ContextHelper.class);

    public static final String WORKFLOWTOUSER = "workflowtouser_file";
    private static String workflowToUserFile = "";
    private static Map<String, String> workflowUsers = new HashMap<String, String>();
    private static Map<String, UserConfig> userConfigs = new HashMap<String, UserConfig>();

    public static final String USERCONFIG_PREFIX = "config/user_";

    static {
        workflowToUserFile = JadeHelper.getProperty(WORKFLOWTOUSER);
    }

    static final void clean(String workfow) {
        workflowUsers.remove(workfow);
    }

    static final String findWorkflowUser(String workflow) throws FileNotFoundException, IOException {
        if (workflowUsers.containsKey(workflow)) {
            return workflowUsers.get(workflow);
        }
        //String user = findProp(workflowToUserFile, "[|]", workflow);
        String user = findUserInDB(workflow);
        if (user==null) {
            log.warn("user for " + workflow + " not found, using unknown");
            return UNKNOWN;
        } else {
            workflowUsers.put(workflow, user);
        }
        return workflowUsers.get(workflow);
    }

    private static final String findUserInDB(String workflow) {
        try {
            return SQLiteDB.findUser(SQLiteDB.getConnection(workflowToUserFile), workflow);
        } catch (ClassNotFoundException ex) {
            log.error(ex,ex);
        } catch (SQLException ex) {
            log.error(ex,ex);
        }
        return null;
    }

    static List findEmails(String workflow, String type) {
        List emails = new LinkedList();
        try {
            String user = findWorkflowUser(workflow);
            String email = findEmail(user, type);
            String admin = findAdminEmail(user, type);
            if (email != null && !email.isEmpty()) {
                emails.add(email);
            }
            if (admin != null && !admin.isEmpty()) {
                emails.add(admin);
            }
        } catch (FileNotFoundException ex) {
            log.error("fout bij zoeken emails " + workflow + ": " + type, ex);
        } catch (IOException ex) {
            log.error("fout bij zoeken emails " + workflow + ": " + type, ex);
        }
        return emails;
    }

    static String findFullName(String user) {
        try {
            return loadUserConfig(user).getFullName();
        } catch (ConfigurationException ex) {
            log.error("unable to find fullname for " + user, ex);
            return null;
        }
    }

    static String findEmail(String user, String event) {
        try {
            return loadUserConfig(user).getUserEmail(event);
        } catch (ConfigurationException ex) {
            log.error("unable to find email for " + user + ", event " + event, ex);
            return null;
        }
    }

    static String findAdminEmail(String user, String event) {
        try {
            return loadUserConfig(user).getAdminEmail(event);
        } catch (ConfigurationException ex) {
            log.error("unable to find admin email for " + user + ", event " + event, ex);
            return null;
        }
    }

    private static UserConfig loadUserConfig(String user) throws ConfigurationException {
        if (!userConfigs.containsKey(user)) {
            File conf = new File(USERCONFIG_PREFIX + user + ".properties");
            if (conf.exists()) {
                userConfigs.put(user, new UserConfig(conf.getPath()));
            } else if (!user.equals("0")) {
                log.warn(USERCONFIG_PREFIX+user + ".properties does not exist, trying " + USERCONFIG_PREFIX+"0.properties");
                return loadUserConfig("0");
            }
        }
        return userConfigs.get(user);
    }

    private static String findProp(String file, String sep, String key) throws FileNotFoundException, IOException {
        File f = new File(file);
        BufferedReader bi = null;
        try {
            bi = new BufferedReader(new FileReader(f));
            String r = null;
            while ((r = bi.readLine()) != null) {
                String[] keyValue = r.split(sep);
                if (keyValue.length != 2) {
                    throw new IllegalArgumentException("invalid line: " + r);
                }
                if (keyValue[0].trim().equals(key)) {
                    return keyValue[1].trim();
                }
            }
        } finally {
            if (null != bi) {
                bi.close();
            }
        }
        return null;
    }

}
