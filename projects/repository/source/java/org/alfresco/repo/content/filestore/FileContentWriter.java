package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides direct access to a local file.
 * <p>
 * This class does not provide remote access to the file.
 * 
 * @author Derek Hulley
 */
public class FileContentWriter extends AbstractContentWriter
{
    private static final Log logger = LogFactory.getLog(FileContentWriter.class);
    
    private File file;
    
    /**
     * @param file the file for reading and writing.  This will most likely be directly
     *      related to the content URL.
     */
    public FileContentWriter(File file)
    {
        super(FileContentStore.STORE_PROTOCOL + file.getAbsolutePath());
        
        this.file = file;
    }
    
    /**
     * @return Returns the file that this writer accesses
     */
    public File getFile()
    {
        return file;
    }

    /**
     * The URL of the write is known from the start and this method contract states
     * that no consideration needs to be taken w.r.t. the stream state.
     */
    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        return new FileContentReader(this.file);
    }
    
   @Override
    protected OutputStream getDirectOutputStream() throws ContentIOException
    {
        try
        {
            // we may not write to an existing file - EVER!!
            if (file.exists() && file.length() > 0)
            {
                throw new IOException("File exists - overwriting not allowed");
            }
            FileOutputStream os = new FileOutputStream(file);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Opened output stream to file: " + file);
            }
            return os;
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to open output stream to file: " + this, e);
        }
    }
}
