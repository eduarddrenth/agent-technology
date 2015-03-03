package org.logica.cns.io;

import java.io.File;
import java.util.Set;

/**
 *
 * @author Eduard Drenth: Logica, 13-nov-2009
 *
 */
public interface DirectoryChanges {

    /**
     *
     * @return a Set containing files changed in a directory
     */
    public Set<File> getChanged();

    /**
     *
     * @return a Set containing files removed in a directory
     */
    public Set<File> getRemoved();

    /**
     *
     * @return a Set containing files added in a directory
     */
    public Set<File> getAdded();

    public boolean addRemoved(File f);

    public boolean addAdded(File f);

    public boolean addChanged(File f);

}
