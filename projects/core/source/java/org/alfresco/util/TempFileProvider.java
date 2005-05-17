package org.alfresco.util;

import java.io.File;
import java.io.IOException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A helper class that provides temp files, providing a common point to clean
 * them up.
 * 
 * @author derekh
 */
public class TempFileProvider
{
    /** subdirectory in the temp directory where Alfresco temporary files will go */
    public static final String ALFRESCO_TEMP_FILE_DIR = "Alfresco";
    /** the system property key giving us the location of the temp directory */
    public static final String SYSTEM_KEY_TEMP_DIR = "java.io.tmpdir";

    private static final Log logger = LogFactory.getLog(TempFileProvider.class);

    /**
     * Static class only
     */
    private TempFileProvider()
    {
    }

    /**
     * @return Returns a temporary directory, i.e. <code>isDir == true</code>
     */
    public static File getTempDir()
    {
        // get the temp directory
        String tempDirPath = System.getProperty(SYSTEM_KEY_TEMP_DIR);
        if (tempDirPath == null)
        {
            throw new AlfrescoRuntimeException("System property not available: " + SYSTEM_KEY_TEMP_DIR);
        }
        // append the Alfresco directory
        tempDirPath = tempDirPath + ALFRESCO_TEMP_FILE_DIR;
        File tempDir = new File(tempDirPath);
        // ensure that the temp directory exists
        if (tempDir.exists())
        {
            // nothing to do
        } else
        {
            // not there yet
            if (!tempDir.mkdirs())
            {
                throw new AlfrescoRuntimeException("Failed to create temp directory: " + tempDir);
            }
            if (logger.isDebugEnabled())
            {
                logger.debug("Created temp directory: " + tempDir);
            }
        }
        // done
        return tempDir;
    }

    /**
     * @return Returns a temp <code>File</code> that will be located in the
     *         <b>Alfresco</b> subdirectory of the default temp directory
     * 
     * @see #ALFRESCO_TEMP_FILE_DIR
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File createTempFile(String prefix, String suffix)
    {
        File tempDir = TempFileProvider.getTempDir();
        // we have the directory we want to use
        return createTempFile(prefix, suffix, tempDir);
    }

    /**
     * @return Returns a temp <code>File</code> that will be located in the
     *         given directory
     * 
     * @see #ALFRESCO_TEMP_FILE_DIR
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File createTempFile(String prefix, String suffix, File directory)
    {
        try
        {
            File tempFile = File.createTempFile(prefix, suffix, directory);
            return tempFile;
        } catch (IOException e)
        {
            throw new AlfrescoRuntimeException("Failed to created temp file: \n" + "   prefix: " + prefix + "\n"
                    + "   suffix: " + suffix + "\n" + "   directory: " + directory, e);
        }
    }
}
