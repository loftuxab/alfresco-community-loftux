/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.alfresco.connector.Connector;
import org.alfresco.connector.RemoteClient;
import org.alfresco.connector.Response;
import org.alfresco.tools.DataUtil;
import org.alfresco.tools.ObjectGUID;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Task which can be used to import an archive from a remote location
 * 
 * This task was a quick migration from the ScriptImporter bean which was
 * previously used and, as such, does some fairly ridiculous things (like
 * loading the entire byte array into memory).
 * 
 * At present, the only archives being distributed are those directly from
 * Alfresco and we know these to be pretty small.  Thus, leaving this in for
 * the time being.
 * 
 * In short order, however, we should refactor to stream the byte array
 * directly to a temp file on disk.
 * 
 * @author muzquiano
 */
public class ImportTask extends AbstractTask
{
    protected static Log logger = LogFactory.getLog(ImportTask.class);
    
    protected static String DEFAULT_WEBAPP_ID = "ROOT";
    
    protected Connector alfrescoConnector;
    
    protected String storeId;
    protected String webappId = DEFAULT_WEBAPP_ID;
    protected String url;
    
	public ImportTask(String name)
	{
	    super(name);
	}
	
	/**
	 * Sets the "alfresco" endpoint connector
	 * 
	 * @param context the new request context
	 */
	public void setAlfrescoConnector(Connector alfrescoConnector)
	{
	    this.alfrescoConnector = alfrescoConnector;
	}
	
	/**
	 * Gets the "alfresco" endpoint connector.
	 * 
	 * @return the alfresco connector
	 */
	public Connector getAlfrescoConnector()
	{
	    return this.alfrescoConnector;
	}
	
	/**
	 * Sets the store id.
	 * 
	 * @param storeId the new store id
	 */
	public void setStoreId(String storeId)
	{
	    this.storeId = storeId;
	}
	
	public void setWebappId(String webappId)
	{
	    this.webappId = webappId;
	}
	
	public void setUrl(String url)
	{
	    this.url = url;
	}

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.AbstractTask#cancel()
     */
    public void cancel()
    {
        // TODO: introduce a way to interrupt the import?
        
        this.isCancelled = true;
    }   
	
	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.AbstractTask#execute()
	 */
	public void execute() throws Throwable
	{
	    // update
	    this.setStatus("Starting Import");
	    
        // build a remote client to the destination
        RemoteClient r = new RemoteClient("");

        // update
        this.setStatus("Downloading from Alfresco Network...");
        
        // pull down the bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        r.call(url, baos);

        byte[] array = baos.toByteArray();
        
        importArchive(array);
    }

    /**
     * Imports a Surf archive
     * 
     * @param array         the byte array
     */
    private void importArchive(byte[] array)
    {
        // assume empty
        String alfrescoPath = "/";

        if (webappId != null && !"".equals(webappId))
        {
            alfrescoPath = "/WEB-INF/classes";
        }

        // update
        this.setStatus("Writing archive...");
        
        // write to temporary file
        String tempFilePath = writeToTempFile(array);

        // update
        this.setStatus("Importing archive...");
        
        // read zip file entries
        int count = 0;
        ZipFile zf = null;
        try
        {
            zf = new ZipFile(tempFilePath);
            
            int totalEntries = zf.size();
            
            Enumeration en = zf.entries();
            while (en.hasMoreElements())
            {
                // update
                this.setStatus("Importing " + (count+1) + " of " + totalEntries);
                
                // get the entry
                ZipEntry zipEntry = (ZipEntry) en.nextElement();

                // get the entry name
                String zipEntryName = zipEntry.getName();

                if (!zipEntry.isDirectory())
                {
                    if (zipEntryName.startsWith("alfresco/"))
                    {
                        String path = zipEntryName;

                        // post to "alfresco" directory location
                        String uri = "/remotestore/create" + alfrescoPath + "/"
                                + path + "?s=" + storeId + "&w=" + webappId;

                        post(zf, zipEntry, uri);
                    }
                    if (zipEntryName.startsWith("web-root/"))
                    {
                        String path = zipEntryName.substring(9);

                        // post to "alfresco" directory location
                        String uri = "/remotestore/create/" + path + "?s="
                                + storeId + "&w=" + webappId;

                        post(zf, zipEntry, uri);
                    }
                }
                
                // increment the count
                count++;
            }

        }
        catch (IOException ioe)
        {
            logger.warn("Unable to import archive from zip: " + tempFilePath, ioe);
        }
        finally
        {
            try
            {
                if (zf != null)
                {
                    zf.close();
                }

                File f = new File(tempFilePath);
                if (f.exists())
                {
                    f.delete();
                }
            }
            catch (Exception e)
            {
                // oh well, we gave it our best shot
            }
        }
    }

    protected void post(ZipFile zf, ZipEntry zipEntry, String uri)
    {
        String zipEntryName = zipEntry.getName();

        // Open a Connector to Alfresco
        InputStream in = null;
        try
        {
            in = zf.getInputStream(zipEntry);

            Connector c = getAlfrescoConnector();

            Response response = c.call(uri, null, in);
            if (response.getStatus().getCode() != 200)
            {
                // throw out
                logger.warn(
                        "Received a " + response.getStatus().getCode()
                                + " on call to: " + uri);
            }
        }
        catch (IOException ioe)
        {
            // we failed to write one of the files...
            logger.warn(
                    "The file '" + zipEntryName
                            + "' could not be written to the store");
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException i)
                {
                }
            }
        }
    }

    /**
     * Writes the contents of an array to a temporary file
     * 
     * @param array
     * @return
     */
    protected String writeToTempFile(byte[] array)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(array);

        String tempFileName = new ObjectGUID().toString() + ".zip";
        File tempDir = TempFileProvider.getTempDir();

        // write the temp file
        String tempFilePath = tempDir.getPath() + File.separatorChar
                + tempFileName;
        File tempFile = new File(tempFilePath);
        try
        {
            FileOutputStream out = new FileOutputStream(tempFile);
            DataUtil.copyStream(in, out);

            out.close();
            in.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            tempFilePath = null;
        }

        return tempFilePath;
    }

	
	
}