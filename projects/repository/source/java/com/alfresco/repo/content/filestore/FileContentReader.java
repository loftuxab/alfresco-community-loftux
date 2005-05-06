package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import org.alfresco.repo.content.AbstractContentReaderImpl;
import org.alfresco.repo.content.ContentIOException;

/**
 * Provides direct access to a local file.
 * <p>
 * This class does not provide remote access to the file.
 * 
 * @author Derek Hulley
 */
public class FileContentReader extends AbstractContentReaderImpl
{
    private static final Log logger = LogFactory.getLog(FileContentReader.class);
    
    private File file;
    private String contentUrl;
    
    /**
     * @param file the file for reading and writing
     */
    public FileContentReader(File file)
    {
        Assert.notNull(file);
        this.file = file;
        this.contentUrl = FileContentStoreImpl.STORE_PROTOCOL + file.getAbsolutePath();
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("FileContentReader")
          .append("[ url=").append(contentUrl)
          .append("]");
        return sb.toString();
    }
    
    public String getContentUrl() throws ContentIOException
    {
        return contentUrl;
    }

    @Override
    protected InputStream getDirectInputStream() throws ContentIOException
    {
        try
        {
            // the file must exist at this point
            if (!file.exists())
            {
                throw new IOException("Unable to read from non-existent file");
            }
            FileInputStream is = new FileInputStream(file);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Opened input stream to file: " + file);
            }
            return is;
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to open input stream to file: " + this, e);
        }
    }
}
