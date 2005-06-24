package org.alfresco.filesys.smb;

/**
 * File Sharing Mode Class
 */
public class SharingMode
{

    // File sharing mode constants

    public final static int NOSHARING = 0x0000;
    public final static int READ = 0x0001;
    public final static int WRITE = 0x0002;
    public final static int DELETE = 0x0004;

    public final static int READWRITE = READ + WRITE;
}
