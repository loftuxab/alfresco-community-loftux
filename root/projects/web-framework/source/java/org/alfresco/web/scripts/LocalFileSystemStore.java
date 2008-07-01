/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.FrameworkHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.cache.TemplateLoader;

/**
 * Simple implementation of a local store file system.
 * 
 * This is extremely light weight and is used as a base case for
 * comparing other store performance vs. the local file system.
 * 
 * @author muzquiano
 */
public class LocalFileSystemStore implements Store
{
    private static Log logger = LogFactory.getLog(LocalFileSystemStore.class);
    
    private String root;
    private String path;
    private File rootDir;
    
    protected File getRootDir()
    {
        if(rootDir == null && FrameworkHelper.isInitialized())
        {
            this.rootDir = new File(getBasePath());
        }
        return this.rootDir;
    }
    
    /**
     * @param root      the root path
     */
    public void setRoot(String root)
    {
        this.root = root;
    }
    
    /**
     * @param path      the relative path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        if(getRootDir() == null)
        {
            return true;
        }
        return getRootDir().exists();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        File file = new File(toAbsolutePath(documentPath));
        return (file != null && file.exists() && file.isFile());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if(file == null)
        {
            throw new IOException("Unable to locate file to check modification time: " + documentPath);
        }
        
        return file.lastModified();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String documentPath, String content) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if(file == null)
        {
            throw new IOException("Unable to locate file for update: " + documentPath);
        }
        
        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath)
        throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if(file == null)
        {
            throw new IOException("Update to remove document failed, file not found: " + documentPath);
        }
        
        return file.delete();
    }        
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
        // check whether a file already exists
        if(hasDocument(documentPath))
        {
            throw new IOException("Unable to create document, already exists: " + documentPath);
        }
        
        File file = new File(toAbsolutePath(documentPath));
        
        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if(file == null)
        {
            throw new IOException("Unable to get input stream from document: " + documentPath);
        }
        
        return new FileInputStream(file);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        List<String> list = new ArrayList<String>(256);
        
        // exhaustive traverse of absolute paths
        gatherAbsolutePaths(getRootDir().getAbsolutePath(), list);
        
        // convert to array
        String[] array = list.toArray(new String[list.size()]);
        
        // down shift to relative paths
        String absRootPath = getRootDir().getAbsolutePath() + File.separatorChar;
        int absRootPathLen = absRootPath.length();
        for(int i = 0; i < array.length; i++)
        {
            array[i] = array[i].substring(absRootPathLen); 
        }

        return array;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern)
    {
        throw new AlfrescoRuntimeException("getDocumentPaths() not supported by local file system store.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths()
    {
        throw new AlfrescoRuntimeException("getDescriptionDocumentPaths() not supported by remote store.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script)
    {
        throw new AlfrescoRuntimeException("getScriptDocumentPaths() not supported by remote store.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        throw new AlfrescoRuntimeException("getScriptLoader() not supported by remote store.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        throw new AlfrescoRuntimeException("getTemplateLoader() not supported by remote store.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getBasePath()
     */
    public String getBasePath()
    {
        String fullPath = this.path;
        if(this.root != null && root.startsWith("."))
        {
            // make relative to the web app real path
            fullPath = FrameworkHelper.getRealPath(this.root.substring(1)) + this.path;
        }
        return fullPath;
    }
    
    protected String toAbsolutePath(String documentPath)
    {
        return getRootDir().getAbsolutePath() + File.separatorChar + documentPath;
    }
    
    protected void gatherAbsolutePaths(String absPath, List<String> list)
    {
        File file = new File(absPath);
        if(file.exists())
        {
            if(file.isFile())
            {
                list.add(absPath);
            }
            else if(file.isDirectory())
            {
                // get all of the children
                String[] childDocumentPaths = file.list();
                for(int i = 0; i < childDocumentPaths.length; i++)
                {
                    String childAbsPath = absPath + File.separatorChar + childDocumentPaths[i];
                    gatherAbsolutePaths(childAbsPath, list);
                }
            }
        }
    }
}
