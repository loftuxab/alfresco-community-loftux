package org.alfresco.service.cmr.repository;

import java.io.File;

/**
 * Extension to {@link ContentReader} for Readers which are able to
 *  make the backing file available to you.
 * 
 * @see org.alfresco.service.cmr.repository.ContentReader
 * 
 * @author Derek Hulley
 */
public interface FileContentReader extends ContentReader
{
    /**
     * Provides access to the underlying File that this
     *  Reader accesses.
     *  
     * @return Returns the file that this reader accesses
     */
    public File getFile();

    /**
     * @return Whether the file exists or not
     */
    public boolean exists();
}
