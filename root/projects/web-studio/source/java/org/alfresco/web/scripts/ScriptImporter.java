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
package org.alfresco.web.scripts;

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
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.tools.DataUtil;
import org.alfresco.tools.ObjectGUID;
import org.alfresco.util.TempFileProvider;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;

/**
 * Utility for importing Surf assets from a remote location into the
 * Surf environment
 * 
 * @author muzquiano
 */
public final class ScriptImporter extends ScriptBase
{
    /**
     * Constructs a new ScriptImporter object.
     * 
     * @param context The RequestContext instance for the current
     *            request
     */
    public ScriptImporter(RequestContext context)
    {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
        }

        return this.properties;
    }

    // --------------------------------------------------------------
    // JavaScript Properties
    //    

    /**
     * Imports a Surf archive into the given store from a URL location
     * 
     * @param store
     * @param webappId empty or webapp id
     * @param url
     */
    public void importArchive(String store, String webappId, String url)
    {
        if (url.startsWith("/"))
        {
            String baseUrl = context.getRequest().getScheme() + "://"
                    + context.getRequest().getServerName();
            if (context.getRequest().getServerPort() != 80)
            {
                baseUrl += ":" + context.getRequest().getServerPort();
            }
            baseUrl += context.getRequest().getContextPath();
            url = baseUrl + url;
        }

        // build a remote client to the destination
        RemoteClient r = new RemoteClient("");

        // pull down the bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        r.call(url, baos);

        byte[] array = baos.toByteArray();

        importArchive(store, webappId, array);
    }

    /**
     * Imports a Surf archive into the given store
     * 
     * @param store
     * @param webappId empty or webapp id
     * @param array
     */
    public void importArchive(String store, String webappId, byte[] array)
    {
        // assume empty
        String alfrescoPath = "/";

        if (webappId != null && !"".equals(webappId))
        {
            alfrescoPath = "/WEB-INF/classes";
        }

        // write to temporary file
        String tempFilePath = writeToTempFile(array);

        // read zip file entries
        ZipFile zf = null;
        try
        {
            zf = new ZipFile(tempFilePath);
            Enumeration en = zf.entries();
            while (en.hasMoreElements())
            {
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
                                + path + "?s=" + store + "&w=" + webappId;

                        post(zf, zipEntry, uri);
                    }
                    if (zipEntryName.startsWith("web-root/"))
                    {
                        String path = zipEntryName.substring(9);

                        // post to "alfresco" directory location
                        String uri = "/remotestore/create/" + path + "?s="
                                + store + "&w=" + webappId;

                        post(zf, zipEntry, uri);
                    }
                }
            }

        }
        catch (IOException ioe)
        {
            FrameworkHelper.getLogger().warn(
                    "Unable to import archive from zip: " + tempFilePath, ioe);
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

            Connector c = FrameworkHelper.getConnector(context, "alfresco");

            Response response = c.call(uri, null, in);
            if (response.getStatus().getCode() != 200)
            {
                // throw out
                FrameworkHelper.getLogger().warn(
                        "Received a " + response.getStatus().getCode()
                                + " on call to: " + uri);
            }
        }
        catch (IOException ioe)
        {
            // we failed to write one of the files...
            FrameworkHelper.getLogger().warn(
                    "The file '" + zipEntryName
                            + "' could not be written to the store");
        }
        catch (RemoteConfigException rce)
        {
            // the "alfresco" endpoint wasn't defined
            FrameworkHelper.getLogger().error(
                    "The 'alfresco' endpoint could not be processed", rce);
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
