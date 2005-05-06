package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import org.alfresco.repo.content.AbstractContentWriterImpl;
import org.alfresco.repo.content.ContentIOException;

/**
 * Provides direct access to a local file.
 * <p>
 * This class does not provide remote access to the file.
 * 
 * @author Derek Hulley
 */
public class FileContentWriter extends AbstractContentWriterImpl
{
    private static final Log logger = LogFactory.getLog(FileContentWriter.class);
    
    private File file;
    private String contentUrl;
    
    /**
     * @param file the file for reading and writing.  This will most likely be directly
     *      related to the content URL.
     */
    public FileContentWriter(File file)
    {
        super();
        Assert.notNull(file);
        this.file = file;
        this.contentUrl = FileContentStoreImpl.STORE_PROTOCOL + file.getAbsolutePath();
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("FileContentWriter")
          .append("[ url=").append(contentUrl)
          .append("]");
        return sb.toString();
    }
    
    public String getContentUrl() throws ContentIOException
    {
        return contentUrl;
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
