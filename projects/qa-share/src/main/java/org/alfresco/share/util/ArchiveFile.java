package org.alfresco.share.util;

import java.util.List;

/**
 * The purpose of this interface is to provide utility methods for various
 * archive file operations.
 * 
 * @author Abhijeet Bharade
 * 
 */
public interface ArchiveFile
{

    /**
     * Gives the path where is the archive is store.
     * 
     * @return the archivePath
     */
    public abstract String getArchivePath();

    /**
     * Returns the list of file and folders in the archive.
     * 
     * @return the fileList
     */
    public abstract List<String> getFileList();

    /**
     * Returns boolean depending on whether the the archive has an empty folder
     * or not.
     * 
     * @return the hasFiles
     */
    public abstract boolean hasEmptyFolder();

    /**
     * Reads an archive file eg: zip and returns the list of files in it.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<String> getFileNamesInArchive() throws Exception;

    /**
     * This method returns a bool depending on whether the archive is empty or
     * not.
     * 
     * @return
     */
    public abstract boolean isArchiveEmpty();

}