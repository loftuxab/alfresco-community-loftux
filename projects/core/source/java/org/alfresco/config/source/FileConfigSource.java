package org.alfresco.config.source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.alfresco.config.ConfigException;

/**
 * ConfigSource implementation that gets its data via a file or files.
 * 
 * @author gavinc
 */
public class FileConfigSource extends BaseConfigSource
{
    /**
     * Constructs a file configuration source that uses a single file
     * 
     * @param filename the name of the file from which to get config
     * 
     * @see FileConfigSource#FileConfigSource(List<String>)
     */
    public FileConfigSource(String filename)
    {
        this(Collections.singletonList(filename));
    }
    
    /**
     * @param sources
     *            List of file paths to get config from
     */
    public FileConfigSource(List<String> sourceStrings)
    {
        super(sourceStrings);
    }

    /**
     * @param sourceString
     *            a valid filename as accepted by the
     *            {@link java.io.File#File(java.lang.String) file constructor}
     * @return Returns a stream onto the file
     */
    protected InputStream getInputStream(String sourceString)
    {
        InputStream is = null;

        try
        {
            is = new BufferedInputStream(new FileInputStream(sourceString));
        }
        catch (IOException ioe)
        {
            throw new ConfigException("Failed to obtain input stream to file: " +
                    sourceString, ioe);
        }

        return is;
    }
}
