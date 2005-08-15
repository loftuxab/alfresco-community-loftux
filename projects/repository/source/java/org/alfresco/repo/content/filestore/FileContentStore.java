/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.content.filestore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class FileContentStore implements ContentStore
{
    public static final String STORE_PROTOCOL = "file://";
    
    private static final Log logger = LogFactory.getLog(FileContentStore.class);
    
    private File rootDirectory;
    private String rootAbsolutePath;

    /**
     * @param rootDirectory the root under which files will be stored.  The
     *      directory will be created if it does not exist.
     */
    public FileContentStore(String rootDirectoryStr)
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
        rootAbsolutePath = rootDirectory.getAbsolutePath();
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
     * Generates a new and unique file based on the current date.
     * 
     * @return Returns a new and unique file
     * @throws IOException if the file or parent directories couldn't be created
     */
    private File getNewStorageFile() throws IOException
    {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // create the directory
        StringBuilder sb = new StringBuilder(20);
        sb.append(year).append(File.separatorChar)
          .append(month).append(File.separatorChar)
          .append(day);
        File dir = new File(rootDirectory, sb.toString());
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        
        // create the file
        String localFileName = GUID.generate() + ".bin";
        File file = new File(dir, localFileName);
        // done
        return file;
    }
    
    /**
     * Used to ensure that the URLs passed into the store conform to the
     * {@link FileContentStore#STORE_PROTOCOL correct protocol}.
     * 
     * @param contentUrl a URL of the content to check
     * @throws RuntimeException if the URL is not correct
     */
    private void checkUrl(String contentUrl) throws RuntimeException
    {
        if (!contentUrl.startsWith(FileContentStore.STORE_PROTOCOL))
        {
            throw new RuntimeException(
                    "This store is reserved for URLs starting with " +
                    FileContentStore.STORE_PROTOCOL + ": \n" +
                    "   store: " + this +
                    " the invlid url is: "+contentUrl);
        }
    }

    /**
     * Takes the file absolute path, strips off the protocol and the root
     * path of the store to create the URL.
     * 
     * @param file the file from which to create the URL
     * @return Returns a relative URL
     * @throws Exception
     */
    private String makeContentUrl(File file)
    {
        String path = file.getAbsolutePath();
        // check if it belongs to this store
        if (!path.startsWith(rootAbsolutePath))
        {
            throw new AlfrescoRuntimeException(
                    "File does not fall below the store's root: \n" +
                    "   file: " + file + "\n" +
                    "   store: " + this);
        }
        // strip off the file separator char, if present
        int index = rootAbsolutePath.length();
        if (path.charAt(index) == File.separatorChar)
        {
            index++;
        }
        // strip off the root path
        String url = FileContentStore.STORE_PROTOCOL + path.substring(index);
        // done
        return url;
    }
    
    /**
     * Creates a file from the given relative URL.  The URL must start with
     * the required {@link FileContentStore#STORE_PROTOCOL protocol prefix}.
     * 
     * @param contentUrl the content URL including the protocol prefix
     * @return Returns a file representing the URL - the file may or may not
     *      exist 
     * 
     * @see #checkUrl(String)
     */
    private static boolean warned = false;
    private File makeFile(String contentUrl)
    {
        /*
         * This temporary code is here to prevent mass barfing for data where
         * the content URL is still in the old absolute format
         */
        if (contentUrl.contains("alfresco\\contentstore\\") ||
                contentUrl.contains("alfresco/contentstore/"))
        {
            // strip off everything before that
            int index = contentUrl.indexOf("alfresco\\contentstore\\");
            if (index < 0)
            {
                index = contentUrl.indexOf("alfresco/contentstore/");
            }
            contentUrl = FileContentStore.STORE_PROTOCOL + contentUrl.substring(index + 22);
            if (!warned)
            {
                logger.warn("Content URLs in database are absolute.  See issue AR-4");
                warned = true;
            }
        }
        
        checkUrl(contentUrl);
        // take just the part after the protocol
        String relativeUrl = contentUrl.substring(7);
        // get the file
        File file = new File(rootDirectory, relativeUrl);
        // done
        return file;
    }
    
    /**
     * This implementation requires that the URL start with
     * {@link FileContentStore#STORE_PROTOCOL }.
     */
    public ContentReader getReader(String contentUrl)
    {
        try
        {
            File file = makeFile(contentUrl);
            ContentReader reader = new FileContentReader(file, contentUrl);
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Created content reader: \n" +
                        "   url: " + contentUrl + "\n" +
                        "   file: " + file + "\n" +
                        "   reader: " + reader);
            }
            return reader;
        }
        catch (Throwable e)
        {
            throw new ContentIOException("Failed to get reader for URL: " + contentUrl, e);
        }
    }
    
    /**
     * @return Returns a writer onto a location based on the date
     */
    public ContentWriter getWriter(ContentReader existingContentReader)
    {
        try
        {
            File file = getNewStorageFile();
            // make a URL
            String contentUrl = makeContentUrl(file);
            // create the writer
            ContentWriter writer = new FileContentWriter(file, contentUrl, existingContentReader);
            // set the 
            // done
            if (logger.isDebugEnabled())
            {
                logger.debug("Created content writer: \n" +
                        "   writer: " + writer);
            }
            return writer;
        }
        catch (IOException e)
        {
            throw new ContentIOException("Failed to get writer", e);
        }
    }

    public List<String> listUrls()
    {
        // recursively get all files within the root
        List<String> contentUrls = new ArrayList<String>(1000);
        listUrls(rootDirectory, contentUrls);
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Listed all content URLS: \n" +
                    "   store: " + this + "\n" +
                    "   count: " + contentUrls.size());
        }
        return contentUrls;
    }
    
    /**
     * @param directory the current directory to get the files from
     * @param contentUrls the list of current content URLs to add to
     * @return Returns a list of all files within the given directory and all subdirectories
     */
    private void listUrls(File directory, List<String> contentUrls)
    {
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                // we have a subdirectory - recurse
                listUrls(file, contentUrls);
            }
            else
            {
                // found a file - create and add the URL
                String contentUrl = makeContentUrl(file);
                contentUrls.add(contentUrl);
            }
        }
    }

    /**
     * Attempts to delete the content at the URL provided.
     */
    public boolean delete(String contentUrl) throws ContentIOException
    {
        try
        {
            File file = makeFile(contentUrl);
            // check if the file exists
            if (file.exists()) 
            {
                return file.delete();
            }
            else
            {
                // already gone
                return true;
            }
        }
        catch (Throwable e)
        {
            throw new ContentIOException("Failed to delete content: \n" +
                    "   store: " + this + "\n" +
                    "   URL: " + contentUrl);
        }
    }
}
