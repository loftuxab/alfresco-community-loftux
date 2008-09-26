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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.connector.Connector;
import org.alfresco.connector.ConnectorContext;
import org.alfresco.connector.ConnectorProvider;
import org.alfresco.connector.ConnectorService;
import org.alfresco.connector.HttpMethod;
import org.alfresco.connector.Response;
import org.alfresco.connector.exception.RemoteConfigException;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.URLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static Log logger = LogFactory.getLog(RemoteStore.class);
    
    private static final String API_LISTPATTERN = "listpattern";
    private static final String API_LISTALL = "listall";
    private static final String API_GET = "get";
    private static final String API_CREATE = "create";
    private static final String API_DELETE = "delete";
    private static final String API_UPDATE = "update";
    private static final String API_LASTMODIFIED = "lastmodified";
    private static final String API_HAS = "has";
    
    private ConnectorService connectorService;
    private String defaultRepositoryStoreId;
    private String api;
    private String path;
    private String endpoint;
    
    private ThreadLocal<String> repositoryStoreId = new ThreadLocal<String>();
    private ThreadLocal<ConnectorProvider> connProvider = new ThreadLocal<ConnectorProvider>();
    
    
    /**
     * Binds this instance to the given repository store id for the current thread
     */
    public void bindRepositoryStoreId(String repositoryStoreId)
    {
        this.repositoryStoreId.set(repositoryStoreId);
    }
    
    /**
     * Binds this instance to the given ConnectorProvider instance for the current thread
     * 
     * @param connector     Connector to bind for the current thread
     */
    public void bindConnectorProvider(ConnectorProvider provider)
    {
        this.connProvider.set(provider);
    }

    /**
     * Unbinds this instance from any thread local values
     */
    public void unbind()
    {
        this.repositoryStoreId.remove();
        this.connProvider.remove();
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
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore initialised with endpoint id '" + endpoint + "' API path '" +
                         api + "' path prefix '" + path + "'.");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        // always return true - even if a remote store appears to be down we cannot
        // assume this is always the case and must retry until it is restored
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        boolean hasDocument = false;
        
        Response res = callGet(buildEncodeCall(API_HAS, documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            hasDocument = Boolean.parseBoolean(res.getResponse());
        }
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.hasDocument() " + documentPath + " = " + hasDocument);
        
        return hasDocument;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        Response res = callGet(buildEncodeCall(API_LASTMODIFIED, documentPath));
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            try
            {
                long lastMod = Long.parseLong(res.getResponse());
                
                if (logger.isDebugEnabled())
                    logger.debug("RemoteStore.lastModified() " + documentPath + " = " + lastMod);
                
                return lastMod;
            }
            catch (NumberFormatException ne)
            {
                throw new IOException("Failed to process lastModified response: " + ne.getMessage());
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
        Response res = callPost(buildEncodeCall(API_UPDATE, documentPath), in);
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.updateDocument() " + documentPath + " = " + res.getStatus().getCode());
        
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
        Response res = callDelete(buildEncodeCall(API_DELETE, documentPath));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.removeDocument() " + documentPath + " = " +
                         (Status.STATUS_OK == res.getStatus().getCode()));
        
        return (Status.STATUS_OK == res.getStatus().getCode());
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
        Response res = callPost(buildEncodeCall(API_CREATE, documentPath), in);
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.createDocument() " + documentPath + " = " + res.getStatus().getCode());
        
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
        return getDocumentResponse(documentPath).getResponseStream();
    }
    
    private Response getDocumentResponse(String path)
        throws IOException
    {
        Response res = callGet(buildEncodeCall(API_GET, path));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.getDocument() " + path + " = " + res.getStatus().getCode());
        
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            return res;
        }
        else
        {
            throw new IOException("Unable to retrieve document path: " + path +
                    " in remote store: " + endpoint +
                    " due to error: " + res.getStatus().getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        Response res = callGet(buildEncodeCall(API_LISTALL, ""));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.getAllDocumentPaths() " + res.getStatus().getCode());
        
        if (Status.STATUS_OK == res.getStatus().getCode())
        {
            List<String> list = new ArrayList<String>(128);
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
        Map<String, String> args = new HashMap<String, String>(1, 1.0f);
        args.put("m", documentPattern);
        Response res = callGet(buildEncodeCall(API_LISTPATTERN, path, args));
        
        if (logger.isDebugEnabled())
            logger.debug("RemoteStore.getDocumentPaths() " + path + " subpaths: " + includeSubPaths +
                         " pattern: " + documentPattern + " = " + res.getStatus().getCode());
        
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
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths()
    {
        return getDocumentPaths("", true, "*.desc.xml");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script)
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPaths("", false, scriptPaths);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new RemoteStoreScriptLoader();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        return new RemoteStoreTemplateLoader();
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
        return buildEncodeCall(method, documentPath, null);
    }
    
    /**
     * Helper to build and encode a remote store call
     * 
     * @param method        Remote store method name
     * @param documentPath  Document path to encode, can be empty but not null
     * @param args          Args map to apply to URL call, can be null or empty
     * 
     * @return encoded URL to execute
     */
    private String buildEncodeCall(String method, String documentPath, Map<String, String> args)
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
        
        if (args != null && args.size() != 0)
        {
            buf.append('?');
            int count = 0;
            for (Map.Entry<String, String> entry : args.entrySet())
            {
                if (count++ != 0)
                {
                    buf.append('&');
                }
                buf.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue()));
            }
        }
        
        return buf.toString();
    }

    /**
     * Perform a POST call to the given URI with the supplied input.
     */
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

    /**
     * Perform a GET call to the given URI.
     */
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
    
    /**
     * Perform a DELETE call to the given URI.
     */
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
     * Get a Connector for access to the endpoint. If a connector has been bound to the
     * current thread then use it, else retrieve a transient connector instance from the
     * ConnectorService.
     * 
     * @return Connector
     * 
     * @throws RemoteConfigException
     */
    private Connector getConnector() throws RemoteConfigException
    {
        Connector conn = null;
        ConnectorProvider provider = this.connProvider.get();
        if (provider != null)
        {
            conn = provider.provide();
        }
        if (conn == null)
        {
            conn = this.connectorService.getConnector(this.endpoint);
        }
        return conn; 
    }
    
    
    /**
     * Remote Store implementation of a Script Loader
     * 
     * @author Kevin Roast
     */
    private class RemoteStoreScriptLoader implements ScriptLoader
    {
        /**
         * @see org.alfresco.web.scripts.ScriptLoader#getScript(java.lang.String)
         */
        public ScriptContent getScript(String path)
        {
            ScriptContent sc = null;
            if (hasDocument(path))
            {
                sc = new RemoteScriptContent(path);
            }
            return sc;
        }
    }
    
    
    /**
     * Remote Store implementation of a Template Loader
     * 
     * @author Kevin Roast
     */
    private class RemoteStoreTemplateLoader implements TemplateLoader
    {
        /**
         * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
         */
        public void closeTemplateSource(Object templateSource) throws IOException
        {
            // nothing to do - we return a reader to fully retrieved in-memory data
        }

        /**
         * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
         */
        public Object findTemplateSource(String name) throws IOException
        {
            RemoteStoreTemplateSource source = null;
            if (hasDocument(name))
            {
                source = new RemoteStoreTemplateSource(name);
            }
            return source;
        }

        /**
         * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
         */
        public long getLastModified(Object templateSource)
        {
            return ((RemoteStoreTemplateSource)templateSource).lastModified();
        }

        /**
         * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object, java.lang.String)
         */
        public Reader getReader(Object templateSource, String encoding) throws IOException
        {
            return ((RemoteStoreTemplateSource)templateSource).getReader(encoding);
        }
    }
    
    
    /**
     * Template Source - loads from a Remote Store.
     * 
     * TODO: implement caching of remotely loaded template content?
     * 
     * @author Kevin Roast
     */
    private class RemoteStoreTemplateSource
    {
        private String templatePath;
        
        private RemoteStoreTemplateSource(String path)
        {
            this.templatePath = path;
        }
        
        private long lastModified()
        {
            try
            {
                return RemoteStore.this.lastModified(templatePath);
            }
            catch (IOException e)
            {
                return -1;
            }
        }
        
        private Reader getReader(String encoding)
            throws IOException
        {
            Response res = getDocumentResponse(templatePath);
            if (encoding == null || encoding.equals(res.getEncoding()))
            {
                return new StringReader(res.getResponse());
            }
            else
            {
                return new InputStreamReader(res.getResponseStream(), encoding);
            }
        }
    }
    
    
    /**
     * Script Content - loads from a Remote Store.
     * 
     * TODO: implement caching of remotely loaded script content?
     * 
     * @author Kevin Roast
     */
    private class RemoteScriptContent implements ScriptContent
    {
        private String scriptPath;
        
        /**
         * Constructor
         * 
         * @param path  Path to remote script content
         */
        private RemoteScriptContent(String path)
        {
            this.scriptPath = path;
        }
        
        /**
         * @see org.alfresco.web.scripts.ScriptContent#getPath()
         */
        public String getPath()
        {
            return path + '/' + this.scriptPath;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#getPathDescription()
         */
        public String getPathDescription()
        {
            return path + '/' + this.scriptPath + " loaded from endpoint: " + endpoint;
        }
        
        /**
         * @see org.alfresco.web.scripts.ScriptContent#getInputStream()
         */
        public InputStream getInputStream()
        {
            try
            {
                return getDocumentResponse(scriptPath).getResponseStream();
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException("Unable to load script: " + scriptPath, e);
            }
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#getReader()
         */
        public Reader getReader()
        {
            try
            {
                Response res = getDocumentResponse(scriptPath);
                if (res.getEncoding() != null)
                {
                    return new InputStreamReader(res.getResponseStream(), res.getEncoding());
                }
                else
                {
                    return new InputStreamReader(res.getResponseStream());
                }
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException("Unable to load script: " + scriptPath, e);
            }
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#isSecure()
         */
        public boolean isSecure()
        {
            return false;
        }
    }
}
