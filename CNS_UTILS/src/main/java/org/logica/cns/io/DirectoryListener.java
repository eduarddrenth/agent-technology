package org.logica.cns.io;

import java.util.Observable;

/**
 * A DirectoryListener is responsible for detecting changes in a  directory, for example added entries. {@link Observerver Observers} can be notified when a change is detected.
 * Implementors may extend the {@link Observable} class.
 * @author Eduard Drenth: Logica, 13-nov-2009
 *
 */
public interface DirectoryListener {

    /**
     * @param files
     */
    public void notifyChanges(DirectoryChanges changes);

    public boolean addDirectoryChangeHandler(DirectoryChangeHandler handler);

    public boolean removeDirectoryChangeHandler(DirectoryChangeHandler handler);
}
