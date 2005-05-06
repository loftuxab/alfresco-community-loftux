package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.util.GUID;

/**
 * Provides a store of node content directly to the file system.
 * <p>
 * The file names obey a convention, but it is not important
 * exactly what they are named.  As a first pass, a subdirectory
 * is created for each protocol, a further subdirectory for each
 * store and finally another for the GUID of the node itself.
 * Within the directory, filenames are generated according to the
 * current time.
 * 
 * @author Derek Hulley
 */
public class FileContentStoreImpl implements ContentStore
{
    public static final String STORE_PROTOCOL = "file://";
    
    private static final Log logger = LogFactory.getLog(FileContentStoreImpl.class);
    
    private File rootDirectory;

    /**
     * @param rootDirectory the root under which files will be stored.  The
     *      directory will be created if it does not exist.
     */
    public FileContentStoreImpl(String rootDirectoryStr)
    {
        rootDirectory = new File(rootDirectoryStr);
        if (!rootDirectory.exists())
        {
            if (!rootDirectory.mkdirs())
            {
                throw new ContentIOException("Failed to create store root: " + rootDirectory, null);
            }
        }
        rootDirectory = rootDirectory.getAbsoluteFile();
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder(36);
        sb.append("FileContentStore")
          .append("[ root=").append(rootDirectory)
          .append("]");
        return sb.toString();
    }
    
    /**
     * Generates a new and unique file for the given node reference.
     * 
     * @param nodeRef the node for which to generate a unique filename
     * @return Returns a new and unique file instance for the node
     * @throws IOException if the file or parent directories couldn't be created
     */
    private File getNewStorageFile(NodeRef nodeRef) throws IOException
    {
        StoreRef storeRef = nodeRef.getStoreRef();
        // create the directory
        String subDir = storeRef.getProtocol() + File.separatorChar +
                storeRef.getIdentifier() + File.separatorChar +
                nodeRef.getId(); 
        File dir = new File(rootDirectory, subDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        // create the file
        long time = System.currentTimeMillis();
        String localFileName = "" + time + "-" + GUID.generate() + ".bin";
        File file = new File(dir, localFileName);
        // done
        return file;
    }
    
    public ContentReader getReader(String contentUrl)
    {
        if (!contentUrl.startsWith(FileContentStoreImpl.STORE_PROTOCOL))
        {
            throw new RuntimeException(
                    "This store is reserved for URLs starting with " +
                    FileContentStoreImpl.STORE_PROTOCOL + ": \n" +
                    "   store: " + this);
        }
        try
        {
            File file = new File(contentUrl.substring(6));
            ContentReader reader = new FileContentReader(file);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Created content reader: \n" +
                        "   url: " + contentUrl + "\n" +
                        "   reader: " + reader);
            }
            return reader;
        }
        catch (Throwable e)
        {
            throw new ContentIOException("Failed to get reader for URL: " + contentUrl, e);
        }
    }

    public ContentWriter getWriter(NodeRef nodeRef)
    {
        try
        {
            File file = getNewStorageFile(nodeRef);
            ContentWriter writer = new FileContentWriter(file);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Created content writer for node reference: \n" +
                        "   node: " + nodeRef + "\n" +
                        "   writer: " + writer);
            }
            return writer;
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to get writer for node: " + nodeRef, e);
        }
    }
}
