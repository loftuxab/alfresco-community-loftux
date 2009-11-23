/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;


/**
 * ClassPath based Web Script Store
 * 
 * @author davidc
 */
public class ClassPathStore implements ApplicationContextAware, Store
{
    // Logger
    private static final Log logger = LogFactory.getLog(ClassPathStore.class);
    
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    protected boolean mustExist = false;
    protected String classPath;
    protected Resource storeResource;
    protected String storeResourcePath;
    protected int storeResourcePathLength;
    protected File storeDir;

    
    /**
     * Sets whether the class path must exist
     * 
     * If it must exist, but it doesn't exist, an exception is thrown
     * on initialisation of the store
     * 
     * @param mustExist
     */
    public void setMustExist(boolean mustExist)
    {
        this.mustExist = mustExist;
    }
    
    /**
     * Sets the class path
     * 
     * @param classPath  classpath
     */
    public void setClassPath(String classPath)
    {
        String cleanClassPath = (classPath.endsWith("/")) ? classPath.substring(0, classPath.length() -1) : classPath;
        this.classPath = cleanClassPath;
    }

    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.resolver = applicationContext;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
        try
        {
            // NOTE: Locate root of web script store
            // NOTE: Following awkward approach is used to mirror lookup of web scripts within store.  This
            //       ensures root paths match.
            Resource rootResource = null;
            Resource[] resources = resolver.getResources("classpath*:" + classPath + "*");
            for (Resource resource : resources)
            {
                String externalForm = resource.getURL().toExternalForm();
                if (externalForm.endsWith(classPath) || externalForm.endsWith(classPath + "/"))
                {
                    rootResource = resource;
                    break;
                }
            }
            
            if (rootResource != null && rootResource.exists())
            {
                storeResource = rootResource;
                storeResourcePath = storeResource.getURL().toExternalForm();
                String cleanStoreResourcePath = (storeResourcePath.endsWith("/")) ? storeResourcePath.substring(0, storeResourcePath.length() -1) : storeResourcePath;
                storeResourcePathLength = cleanStoreResourcePath.length();
                if (logger.isTraceEnabled())
                    logger.trace("Provided classpath: " + classPath + " , storeRootPath: " + storeResourcePath + ", storeRootPathLength: " + storeResourcePathLength);
                
                try
                {
                    // retrieve file system directory
                    storeDir = resources[0].getFile();
                }
                catch(FileNotFoundException e)
                {
                    // NOTE: this means that installation of web scripts is not possible
                }
            }
            else if (mustExist)
            {
                throw new WebScriptException("Web Script Store classpath:" + classPath + " must exist; it was not found");
            }
        }
        catch(IOException e)
        {
            throw new WebScriptException("Failed to initialise Web Script Store classpath: " + classPath, e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        return (storeResource != null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getBasePath()
     */
    public String getBasePath()
    {
        return "classpath:" + classPath;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#isSecure()
     */
    public boolean isSecure()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        String[] paths;

        try
        {
            List<String> documentPaths = getPaths("classpath*:" + classPath + "/**/*");
            paths = documentPaths.toArray(new String[documentPaths.size()]);
        }
        catch (IOException e)
        {
            // Note: Ignore: no documents found
            paths = new String[0];
        }
        
        return paths;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern) throws IOException
    {
        if ((path == null) || (path.length() == 0))
        {
            path = "/";
        }
        
        if (! path.startsWith("/"))
        {
            path = "/" + path;
        }
        
        if (! path.endsWith("/"))
        {
            path = path + "/";
        }
        
        if ((documentPattern == null) || (documentPattern.length() == 0))
        {
            documentPattern = "*";
        }
        
        final StringBuilder pattern = new StringBuilder(128);
        pattern.append("classpath*:").append(classPath)
               .append(path)
               .append((includeSubPaths ? "**/" : ""))
               .append(documentPattern);
        
        List<String> documentPaths = getPaths(pattern.toString());
        return documentPaths.toArray(new String[documentPaths.size()]);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths() throws IOException
    {
        return getDocumentPaths("/", true, "*.desc.xml");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script) throws IOException
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPaths("/", false, scriptPaths);
    }
    
    /**
     * Helper to return a list of resource document paths based on a search pattern.
     */
    private List<String> getPaths(String pattern)
        throws IOException
    {
        Resource[] resources = resolver.getResources(pattern);
        List<String> documentPaths = new ArrayList<String>(resources.length);
        for (Resource resource : resources)
        {
            if (resource.getURL().toExternalForm().startsWith(storeResourcePath))
            {
                String resourcePath = resource.getURL().toExternalForm();
                String documentPath = resourcePath.substring(storeResourcePathLength +1);
                documentPath = documentPath.replace('\\', '/');
                if (logger.isTraceEnabled())
                    logger.trace("Item resource path: " + resourcePath + " , item path: " + documentPath);
                documentPaths.add(documentPath);
            }
        }
        return documentPaths;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath)
        throws IOException
    {
        Resource document = createRelative(documentPath);
        return document.getURL().openConnection().getLastModified();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        boolean exists = false;
        try
        {
            Resource document = createRelative(documentPath);
            exists = document.exists();
        }
        catch(IOException e)
        {
        }
        return exists;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDescriptionDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath)      
        throws IOException
    {
        Resource document = createRelative(documentPath);
        if (logger.isTraceEnabled())
            logger.trace("getDocument: documentPath: " + documentPath + " , storePath: " + document.getURL().toExternalForm());
        
        if (!document.exists())
        {
            throw new IOException("Document " + documentPath + " does not exist within store " + getBasePath());
        }
        return document.getInputStream();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content) throws IOException
    {
        File document = new File(storeDir, documentPath);
        
        // create directory
        File path = document.getParentFile();
        path.mkdirs();
        
        // create file
        if (!document.createNewFile())
        {
            throw new IOException("Document " + documentPath + " already exists");
        }
        OutputStream output = new FileOutputStream(document);
        try
        {
            PrintWriter writer = new PrintWriter(output);
            writer.write(content);
            writer.flush();
        }
        finally
        {
            output.flush();
            output.close();
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String documentPath, String content) throws IOException
    {
        File document = new File(storeDir, documentPath);
        
        // check for write access
        if (!document.canWrite())
        {
            throw new IOException("Document " + documentPath + " is not writable");
        }
        OutputStream output = new FileOutputStream(document);
        try
        {
            PrintWriter writer = new PrintWriter(output);
            writer.write(content);
            writer.flush();
        }
        finally
        {
            output.flush();
            output.close();
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath)
        throws IOException
    {
        // do not allow for deletion of documents from the classpath
        // the classpath is read-only
        return false;
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        // ensure classpath starts and ends with /
        String templateClassPath = (classPath.charAt(0) == '/') ? classPath : "/" + classPath;
        return new ClassTemplateLoader(ClassPathStore.class, templateClassPath);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new ClassPathScriptLoader();
    }        

    /**
     * Construct a relative resource
     * 
     * @param resource  root resource
     * @param path  relative path
     * @return  relative resource
     * @throws IOException
     */
    private Resource createRelative(String path)
        throws IOException
    {
        // Special handling for directory resource URLs (these end in a slash)
        if (storeResourcePath.endsWith("/"))
        {
            return storeResource.createRelative(storeResource.getFilename() + "/" + path);
        }
        
        int prefixIdx = storeResourcePath.lastIndexOf("/");
        String prefix = (prefixIdx != -1) ? storeResourcePath.substring(prefixIdx) : "";
        return storeResource.createRelative(prefix + "/" + path);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.storeResourcePath;
    }


    /**
     * Class path based script loader
     * 
     * @author davidc
     */
    private class ClassPathScriptLoader implements ScriptLoader
    {

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptLoader#getScriptLocation(java.lang.String)
         */
        public ScriptContent getScript(String path)
        {
            ScriptContent location = null;
            try
            {
                Resource script = createRelative(path);
                if (script.exists())
                {
                    location = new ClassPathScriptLocation(storeResource, path, script);
                }
            }
            catch(IOException e)
            {
            }
            return location;
        }
    }

    /**
     * Class path script location
     * 
     * @author davidc
     */
    private static class ClassPathScriptLocation implements ScriptContent
    {
        private Resource store;
        private String path;
        private Resource location;

        /**
         * Construct
         * 
         * @param store
         * @param path
         * @param location
         */
        public ClassPathScriptLocation(Resource store, String path, Resource location)
        {
            this.store = store;
            this.path = path;
            this.location = location;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.repository.ScriptLocation#getInputStream()
         */
        public InputStream getInputStream()
        {
            try
            {
                return location.getInputStream();
            }
            catch (IOException e)
            {
                throw new WebScriptException("Unable to retrieve input stream for script " + getPathDescription());
            }
        }

        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.repository.ScriptLocation#getReader()
         */
        public Reader getReader()
        {
            try
            {
                return new InputStreamReader(getInputStream(), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                throw new AlfrescoRuntimeException("Unsupported Encoding", e);
            }
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#getPath()
         */
        public String getPath()
        {
            String path = "<unknown path>";
            try
            {
                path = location.getURL().toExternalForm();
            }
            catch(IOException ioe)
            {
            };
            return path;
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#getPathDescription()
         */
        public String getPathDescription()
        {
            String desc = "<unknown path>";
            try
            {
                desc = "/" + path + " (in classpath store " + store.getURL().toExternalForm() + ")";
            }
            catch(IOException ioe)
            {
            };
            return desc;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#isCachable()
         */
        public boolean isCachable()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.alfresco.web.scripts.ScriptContent#isSecure()
         */
        public boolean isSecure()
        {
            return true;
        }

        @Override
        public String toString()
        {
            return getPathDescription();
        }
    }
}
