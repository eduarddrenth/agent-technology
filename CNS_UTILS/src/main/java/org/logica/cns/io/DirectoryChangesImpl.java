package org.logica.cns.io;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Eduard Drenth: Logica, 13-nov-2009
 * 
 */
public class DirectoryChangesImpl implements DirectoryChanges {

    private Set<File> removed = new HashSet<File>(10);
    private Set<File> added = new HashSet<File>(10);
    private Set<File> changed = new HashSet<File>(10);

    public Set<File> getRemoved() {
        return removed;
    }

    public Set<File> getAdded() {
        return added;
    }

    public boolean addRemoved(File f) {
        return removed.add(f);
    }

    public boolean addAdded(File f) {
        return added.add(f);
    }

    public Set<File> getChanged() {
        return changed;
    }

    public boolean addChanged(File file) {
        return changed.add(file);
    }

}
