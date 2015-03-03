package org.logica.cns_workshop.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import org.logica.cns.generic.JadeHelper;

/**
 *
 * @author Eduard Drenth: Logica, 13-jan-2010
 * 
 */
public class SmileyGUI extends JFrame {

    private static SmileyGUI me = null;

    private SmileyGUI() throws HeadlessException, UnknownHostException {
        super("close window to stop smilies at " + InetAddress.getLocalHost().getHostAddress());
        setPreferredSize(new Dimension(600, 100));
        setSize(getPreferredSize());
        setBackground(Color.LIGHT_GRAY);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                setTitle("shutting down smileys");
                JadeHelper.stopJade();
            }

        });
        setVisible(true);
    }

    public static synchronized SmileyGUI getInstance() throws HeadlessException, UnknownHostException {
        if (me == null) {
            me = new SmileyGUI();
        }
        return me;
    }


}
