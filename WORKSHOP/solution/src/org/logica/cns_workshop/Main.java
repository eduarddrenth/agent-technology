package org.logica.cns_workshop;

import jade.core.Profile;
import jade.util.leap.Properties;
import java.net.InetAddress;
import org.logica.cns.generic.JadeHelper;
import org.logica.cns_workshop.gui.SmileyGUI;
import org.logica.cns_workshop.smilies.SmileyAgent;

/**
 *
 * @author Eduard Drenth: Logica, 21-dec-2009
 *
 */
public class Main {

   public static final String NUMSMILEYS = "numsmiles";

   public static void main(String[] args) throws Exception {
      if (args == null || args.length==0) {
         args = new String[]{"config/jadeall.properties"};
      }
      
      Properties p = new Properties();
      p.load(args[0]);

      if (args[0].contains("jademain")) {

         JadeHelper.launchJade(args[0]);

      } else if (args[0].contains("jademediator")) {

         JadeHelper.launchJade(args[0]);

      } else {

         /* we start smileys
          * container name and smiley names dependend on machine address
          * this way we can easily distribute and recognize smileys
          *
          */
         p.setProperty(Profile.CONTAINER_NAME, InetAddress.getLocalHost().toString());

         SmileyGUI.getInstance();

         JadeHelper.launchJade(p);

         for (int i = 0; i <= Integer.parseInt(JadeHelper.getProperty(NUMSMILEYS)); i++) {

            // start the smiley
            JadeHelper.startLocalAgent(InetAddress.getLocalHost().getHostAddress().substring(InetAddress.getLocalHost().getHostAddress().lastIndexOf(".") + 1)
                + "_SMILEY_" + i, SmileyAgent.class.getName(), null);
         }

      }
   }

}
