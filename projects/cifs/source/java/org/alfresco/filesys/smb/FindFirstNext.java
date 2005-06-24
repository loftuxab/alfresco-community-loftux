package org.alfresco.filesys.smb;

/**
 * Find First/Next Flags
 */
public class FindFirstNext
{
    // Find first/find next flags

    public static final int CloseSearch =       0x01;
    public static final int CloseAtEnd =        0x02;
    public static final int ReturnResumeKey =   0x04;
    public static final int ResumePrevious =    0x08;
    public static final int BackupIntent =      0x10;
}
