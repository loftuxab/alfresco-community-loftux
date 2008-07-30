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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorContext;
import org.alfresco.connector.ConnectorService;
import org.alfresco.connector.HttpMethod;
import org.alfresco.connector.Response;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.URLEncoder;

import freemarker.cache.TemplateLoader;

/**
 * Store implementation that queries and retrieves documents from a remote HTTP endpoint.
 * <p>
 * The endpoint is assumed to support a WebScript Remote Store implementation (such as
 * AVMRemoteStore) that mirrors the required Store API. 
 * 
 * @author Kevin Roast
 */
public class RemoteStore implements Store
{
    private ConnectorService connectorService;
    private String defaultRepositoryStoreId;
    private String api;
    private String path;
    private String endpoint;

    private ThreadLocal<String> repositoryStoreId = new ThreadLocal<String>();
    private ThreadLocal<Connector> connector = new ThreadLocal<Connector>();
    
    
    /**
     * Binds this instance to the given repository store id for the current thread
     */
    public void bindRepositoryStoreId(String repositoryStoreId)
    {
        this.repositoryStoreId.set(repositoryStoreId);
    }
    
    /**
     * Binds this instance to the given Connector instance for the current thread
     * 
     * @param connector     Connector to bind for the current thread
     */
    public void bindConnector(Connector connector)
    {
        this.connector.set(connector);
    }

    /**
     * Unbinds this instance from any thread local values
     */
    public void unbind()
    {
        this.repositoryStoreId.remove();
        this.connector.remove();
    }

    /**
     * @return repository store id currently bound to this instance for the current thread
     */
    public String getRepositoryStoreId()
    {
        String storeId = this.repositoryStoreId.get();
        if (storeId == null)
        {
            storeId = this.defaultRepositoryStoreId;
        }
        return storeId;
    }

    /**
     * @param api       the WebScript API path to set for the remote store i.e. "/remotestore"
     */
    public void setApi(String api)
    {
        this.api = api;
    }

    /**
     * @param path      the path prefix to set for the remote store i.e. "/site-data/components"
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * @param endpoint  the endpoint ID to use when calling the remote API
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    /**
     * @return the endpoint ID being used when calling the remote API
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }

    /**
     * @param repoStoreId   the default repostory store ID to use - overriden by thread local setting
     */
    public void setDefaultRepositoryStoreId(String repoStoreId)
    {
        this.defaultRepositoryStoreId = repoStoreId;
    }

    /**
     * @param service   The ConnectorService bean
     */
    public void setConnectorService(ConnectorService service)
    {
        this.connectorService = service;
    }


    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
        if (this.connectorService == null)
        {
            throw new IllegalArgumentException("ConnectorService reference is mandatory for RemoteStore.");
        }
        if (this.endpoint == null || this.endpoint.length() == 0)
        {
            throw new IllegalArgumentException("Endpoint ID is mandatory for RemoteStore.");
        }
        if (this.api == null || this.api.length() == 0)
        {
            throw new IllegalArgumentException("API name is mandatory for RemoteStore.");
        }
        if (this.path == null)
        {
            throw new IllegalArgumentException("Path prefix is mandatory for RemoteStore.");
        }
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
        Response res = callGet(buildEncodeCall("has", documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
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
        Response res = callGet(buildEncodeCall("lastmodified", documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            try
            {
                return Long.parseLong(res.getResponse());
            }
            catch (NumberFormatException ne)
            {
                throw new IOException("Failed to process lastModified response: " + ne.getMessage(), ne);
            }
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
        Response res = callPost(buildEncodeCall("update", documentPath), in);
        if (Status.STATUS_OK != res.getStatus().getCode())
        {
            throw new IOException("Unable to update document path: " + documentPath +
                    " in remote store: " + endpoint +
                    " due to error: " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath) throws IOException
    {
        Response res = callDelete(buildEncodeCall("delete", documentPath));
        return (Status.STATUS_OK == res.getStatus().getCode());
    }        

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
        Response res = callPost(buildEncodeCall("create", documentPath), in);
        if (Status.STATUS_OK != res.getStatus().getCode())
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
        Response res = callGet(buildEncodeCall("get", documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
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
        Response res = callGet(buildEncodeCall("listall", ""));
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            List<String> list = new ArrayList<String>(32);
            StringTokenizer t = new StringTokenizer(res.getResponse(), "\n");
            while (t.hasMoreTokens())
            {
                list.add(t.nextToken().substring(path.length()));
            }
            return list.toArray(new String[list.size()]);
        }
        else
        {
            return new String[0];
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern)
    {
        // TODO: implement getDocumentPaths()
        throw new AlfrescoRuntimeException("getDocumentPaths() not supported by remote store.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths()
    {
        // TODO: implement getDescriptionDocumentPaths()
        throw new AlfrescoRuntimeException("getDescriptionDocumentPaths() not supported by remote store.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script)
    {
        // TODO: implement getScriptDocumentPaths()
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

        buf.append(this.api);
        buf.append('/');
        buf.append(method);
        buf.append(this.path);

        for (StringTokenizer t = new StringTokenizer(documentPath, "/"); t.hasMoreTokens(); /**/)
        {
            buf.append('/').append(URLEncoder.encode(t.nextToken()));
        }

        return buf.toString();
    }

    private Response callPost(String uri, InputStream in)
    {
        try
        {
            Connector con = getConnector();
            return con.call(uri, null, in);
        }
        catch (RemoteConfigException re)
        {
            throw new AlfrescoRuntimeException("Unable to find config for remote store.", re);
        }
    }

    private Response callGet(String uri)
    {
        try
        {
            Connector con = getConnector();
            return con.call(uri);
        }
        catch (RemoteConfigException re)
        {
            throw new AlfrescoRuntimeException("Unable to find config for remote store.", re);
        }
    }
    
    private Response callDelete(String uri)
    {
        try
        {
            Connector con = getConnector();
            ConnectorContext context = new ConnectorContext(HttpMethod.DELETE, null, null);
            return con.call(uri, context);
        }
        catch (RemoteConfigException re)
        {
            throw new AlfrescoRuntimeException("Unable to find config for remote store.", re);
        }
    }

    /**
     * Get the Connector to use to access the endpoint. If a connector has be bound to the
     * current thread then use it, else retrieve a transient connector instance from the
     * ConnectorService.
     * 
     * @return Connector
     * 
     * @throws RemoteConfigException
     */
    private Connector getConnector() throws RemoteConfigException
    {
        Connector con = connector.get();
        if (con == null)
        {
            con = this.connectorService.getConnector(this.endpoint);
        }
        return con; 
    }
}
