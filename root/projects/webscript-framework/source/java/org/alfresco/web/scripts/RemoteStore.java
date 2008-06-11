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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.connector.RemoteClient;
import org.alfresco.connector.Response;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.URLEncoder;

import freemarker.cache.TemplateLoader;

/**
 * @author Kevin Roast
 */
public class RemoteStore implements Store
{
    private String defaultRepositoryStoreId;
    private String path;
    private String endpoint;
    private RemoteClient remote;
    
    private ThreadLocal<String> repositoryStoreId = new ThreadLocal<String>();    
    
    /**
     * Binds this instance to the given repository store id for the 
     * current thread
     * 
     * @param repositoryStoreId
     */
    public void bindRepositoryStoreId(String repositoryStoreId)
    {
        this.repositoryStoreId.set(repositoryStoreId);
    }

    /**
     * Unbinds this instance from any repository store for the 
     * current thread
     *
     */
    public void unbindRepositoryStoreId()
    {
        this.repositoryStoreId.remove();
    }

    /**
     * Gets the repostiry store id currently bound to this instance 
     * for the current thread
     * 
     * @return
     */
    public String getRepositoryStoreId()
    {
        String storeId = defaultRepositoryStoreId;
        if(storeId == null)
        {
            storeId = this.repositoryStoreId.get();
        }
        return storeId;
    }
    
    /**
     * @param path      the relative path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }
    
    /**
     * @param endpoint  the endpoint to set
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    public void setDefaultRepositoryStoreId(String repoStoreId)
    {
        this.defaultRepositoryStoreId = repoStoreId;
    }


    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
        this.remote = new RemoteClient(this.endpoint);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        boolean hasDocument = false;
        Response res = this.remote.call(buildEncodeCall("has", documentPath));
        if (HttpServletResponse.SC_OK == res.getStatus().getCode())
        {
            hasDocument = Boolean.parseBoolean(res.getResponse());
        }
        return hasDocument;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        Response res = this.remote.call(buildEncodeCall("lastmodified", documentPath));
        if (HttpServletResponse.SC_OK == res.getStatus().getCode())
        {
            return Long.parseLong(res.getResponse());
        }
        else
        {
          throw new IOException("Unable to get lastModified date of document path: " + documentPath +
                " in remote store: " + endpoint +
                " due to error: " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String documentPath, String content) throws IOException
    {
       ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
       Response res = this.remote.call(buildEncodeCall("update", documentPath), true, in);
       if (HttpServletResponse.SC_OK != res.getStatus().getCode())
       {
          throw new IOException("Unable to update document path: " + documentPath +
                " in remote store: " + endpoint +
                " due to error: " + res.getStatus().getMessage());
       }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath)
        throws IOException
    {
        // TODO: Implement remove for Remote Store
        return false;
    }        
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
       ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
       Response res = this.remote.call(buildEncodeCall("create", documentPath), true, in);
       if (HttpServletResponse.SC_OK != res.getStatus().getCode())
       {
          throw new IOException("Unable to create document path: " + documentPath +
                " in remote store: " + endpoint +
                " due to error: " + res.getStatus().getMessage());
       }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
        Response res = this.remote.call(buildEncodeCall("get", documentPath));
        if (HttpServletResponse.SC_OK == res.getStatus().getCode())
        {
            return res.getResponseStream();
        }
        else
        {
            throw new IOException("Unable to retrieve document path: " + documentPath +
                " in remote store: " + endpoint +
                " due to error: " + res.getStatus().getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        throw new AlfrescoRuntimeException("getAllDocumentPaths() not supported by remote store.");
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern)
    {
        throw new AlfrescoRuntimeException("getDocumentPaths() not supported by remote store.");
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
        return this.path;
    }
    
    
    /**
     * Helper to build and encode a remote store call
     * 
     * @param method        Remote store method name
     * @param documentPath  Document path to encode
     * 
     * @return encoded URL to execute
     */
    private String buildEncodeCall(String method, String documentPath)
    {
        // TODO: Have this method take into account the currently bound
        // store id.  The store id could be an avm store id but it could
        // also potentially be any store in the repository
        
        // TODO: Do we need to separate out the concept of a store id
        // from an AVM store Id?  Are they different?  AVM stores currently
        // assume a certain path structure to accomodate web projects.
        // Ideally, this could all be handled via configuration and still
        // use a single remote store implementation.
        
        StringBuilder buf = new StringBuilder(128);
        
        buf.append('/');
        buf.append(method);
        buf.append(this.path);
        
        for (StringTokenizer t = new StringTokenizer(documentPath, "/"); t.hasMoreTokens(); /**/)
        {
            buf.append('/').append(URLEncoder.encode(t.nextToken()));
        }
        
        return buf.toString();
    }
}
