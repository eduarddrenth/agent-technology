package org.logica.cns.io;

/**
 * A DirectoryChangeHandler is responsable for handling changes in a directory
 * @author Eduard Drenth: Logica, 13-nov-2009
 *
 */
public interface DirectoryChangeHandler {

    /**
     * deal with added and removed files in a directory
     * @param changes
     */
    public void handleChanges(DirectoryChanges changes);

}
