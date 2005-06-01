package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides direct access to a local file.
 * <p>
 * This class does not provide remote access to the file.
 * 
 * @author Derek Hulley
 */
public class FileContentReader extends AbstractContentReader
{
    private static final Log logger = LogFactory.getLog(FileContentReader.class);
    
    private File file;
    
    /**
     * @param file the file for reading and writing
     */
    public FileContentReader(File file)
    {
        super(FileContentStore.STORE_PROTOCOL + file.getAbsolutePath());

        if (file == null)
        {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
    }
    
    /**
     * @return Returns the file that this reader accesses
     */
    public File getFile()
    {
        return file;
    }

    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        return new FileContentReader(this.file);
    }
    
    /**
     * @see File#exists()
     */
    public boolean exists()
    {
        return file.exists();
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
