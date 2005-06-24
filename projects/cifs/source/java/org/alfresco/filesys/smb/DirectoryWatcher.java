package org.alfresco.filesys.smb;

/**
 * Directory Watcher Interface
 */
public interface DirectoryWatcher
{

    // Notification event types

    public final static int FileActionUnknown = -1;
    public final static int FileNoAction = 0;
    public final static int FileAdded = 1;
    public final static int FileRemoved = 2;
    public final static int FileModified = 3;
    public final static int FileRenamedOld = 4;
    public final static int FileRenamedNew = 5;

    /**
     * Directory change occurred
     * 
     * @param typ int
     * @param fname String
     */
    public void directoryChanged(int typ, String fname);
}
