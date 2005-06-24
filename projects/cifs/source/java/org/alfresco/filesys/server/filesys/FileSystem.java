package org.alfresco.filesys.server.filesys;

/**
 * Filesystem Attributes Class
 * <p>
 * Contains constant attributes used to define filesystem features available. The values are taken
 * from the SMB/CIFS protocol query filesystem call.
 */
public final class FileSystem
{

    // Filesystem attributes

    public static final int CaseSensitiveSearch = 0x00000001;
    public static final int CasePreservedNames = 0x00000002;
    public static final int UnicodeOnDisk = 0x00000004;
    public static final int PersistentACLs = 0x00000008;
    public static final int FileCompression = 0x00000010;
    public static final int VolumeQuotas = 0x00000020;
    public static final int SparseFiles = 0x00000040;
    public static final int ReparsePoints = 0x00000080;
    public static final int RemoteStorage = 0x00000100;
    public static final int VolumeIsCompressed = 0x00008000;
    public static final int ObjectIds = 0x00010000;
    public static final int Encryption = 0x00020000;
}
