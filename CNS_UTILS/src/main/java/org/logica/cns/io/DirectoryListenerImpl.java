package org.logica.cns.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Eduard Drenth: Logica, 13-nov-2009
 * 
 */
public class DirectoryListenerImpl implements DirectoryListener {

    private static class MyRunner implements Runnable {

        private DirectoryListenerImpl d = null;

        public MyRunner(DirectoryListenerImpl d) {
            this.d = d;
        }

        @Override
        public void run() {
            d.run();
        }
    }
    private static final Log log = LogFactory.getLog(DirectoryListenerImpl.class);
    private File[] initialListing = null;
    private File[] currentListing = null;
    private DirectoryChanges changes = null;
    private Set<DirectoryChangeHandler> handlers = new HashSet<DirectoryChangeHandler>(1);
    private File dir = null;
    private boolean dorun = true;
    private boolean pauze = false;
    private Thread myThread = null;
    private static final FileFilter dirFilter = new FileFilter() {

        public boolean accept(File pathname) {
            if (!pathname.canRead()) {
                log.error("cannot read: " + pathname.getName());
                return false;
            }
            return true;
        }
    };

    public DirectoryListenerImpl(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory() && dir.canRead()) {
            this.dir = dir;
            initialListing = dir.listFiles(dirFilter);
        } else {
            if (!dir.exists()) {
                throw new IllegalArgumentException("does not exist: " + dir.getName());
            }
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("no directory: " + dir.getName());
            }
            if (!dir.canRead()) {
                throw new IllegalArgumentException("cannot read: " + dir.getName());
            }
        }
    }

    private void run() {
        while (dorun) {
            if (!pauze) {
                currentListing = dir.listFiles(dirFilter);
                if (!Arrays.equals(initialListing, currentListing) && currentListing != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("initial listing " + Arrays.asList(initialListing));
                        log.debug("current listing " + Arrays.asList(currentListing));
                    }
                    changes = changes(initialListing, currentListing);
                    notifyChanges(changes);
                    initialListing = currentListing;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                log.warn("unable to sleep", ex);
            }
        }
    }

    public void start() {
        if (null == myThread) {
            myThread = new Thread(new MyRunner(this));
            dorun = true;
            pauze = false;
            myThread.start();
        }
    }

    public void stop() throws InterruptedException {
        if (isPolling()) {
            dorun = false;
            myThread.interrupt();
            myThread.join();
        }
    }

    public void pauze() {
        pauze = true;
    }

    public void resume() {
        pauze = false;
    }

    public boolean isPolling() {
        return dorun && !pauze && null != myThread && myThread.isAlive();
    }

    private static DirectoryChanges changes(File[] initial, File[] current) {
        DirectoryChanges changes = new DirectoryChangesImpl();
        if (null != initial && null != current) {
            List<File> i = Arrays.asList(initial);
            List<File> c = Arrays.asList(current);
            for (File file : i) {
                if (!c.contains(file)) {
                    changes.addRemoved(file);
                }
            }
            for (File file : c) {
                if (!i.contains(file)) {
                    changes.addAdded(file);
                }
            }
        }
        return changes;
    }

    public void notifyChanges(DirectoryChanges changes) {
        for (DirectoryChangeHandler h : handlers) {
            h.handleChanges(changes);
        }
    }

    /**
     *
     * @param handler
     * @return true if the handler was registered
     */
    public boolean addDirectoryChangeHandler(DirectoryChangeHandler handler) {
        return handlers.add(handler);
    }

    /**
     *
     * @param handler
     * @return true if the handler was unregistered
     */
    public boolean removeDirectoryChangeHandler(DirectoryChangeHandler handler) {
        return handlers.remove(handler);
    }
}
