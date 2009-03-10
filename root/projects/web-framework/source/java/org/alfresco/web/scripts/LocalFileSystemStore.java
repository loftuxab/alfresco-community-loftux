/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.scripts;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.FrameworkHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.cache.TemplateLoader;

/**
 * Simple implementation of a local store file system.
 * 
 * This is extremely light weight and is used as a base case for comparing other
 * store performance vs. the local file system.
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
        if (this.rootDir == null)
        {
            this.rootDir = new File(getBasePath());
        }

        return this.rootDir;
    }

    /**
     * @param root the root path
     */
    public void setRoot(String root)
    {
        this.root = root;
    }

    /**
     * @param path the relative path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#isSecure()
     */
    public boolean isSecure()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        if (getRootDir() == null)
        {
            return true;
        }
        return getRootDir().exists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        File file = new File(toAbsolutePath(documentPath));
        return (file != null && file.exists() && file.isFile());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Unable to locate file to check modification time: " + documentPath);
        }

        return file.lastModified();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#updateDocument(java.lang.String,
     *      java.lang.String)
     */
    public void updateDocument(String documentPath, String content)
            throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Unable to locate file for update: " + documentPath);
        }

        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Update to remove document failed, file not found: " + documentPath);
        }

        return file.delete();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String,
     *      java.lang.String)
     */
    public void createDocument(String documentPath, String content)
            throws IOException
    {
        // check whether a file already exists
        if (hasDocument(documentPath))
        {
            throw new IOException(
                    "Unable to create document, already exists: " + documentPath);
        }

        File file = new File(toAbsolutePath(documentPath));

        FileWriter fw = new FileWriter(file);
        fw.write(content);
        fw.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Unable to get input stream from document: " + documentPath);
        }

        return new FileInputStream(file);
    }

    /*
     * (non-Javadoc)
     * 
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
        for (int i = 0; i < array.length; i++)
        {
            array[i] = array[i].substring(absRootPathLen);
        }

        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String,
     *      boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths,
            String documentPattern)
    {
        PatternFileFilter filter = new PatternFileFilter(documentPattern);

        String absParentPath = toAbsolutePath(path);
        int absParentPathLen = absParentPath.length() - 1;
        File f = new File(absParentPath);

        List<File> fileList = listPath(f, filter, includeSubPaths);

        String[] paths = new String[fileList.size()];
        for (int i = 0; i < fileList.size(); i++)
        {
            String thePath = ((File) fileList.get(i)).getPath();
            paths[i] = thePath.substring(absParentPathLen);
        }

        return paths;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths()
    {
        return getDocumentPaths("/", true, ".*\\.desc\\.xml");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script)
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPaths("/", false, scriptPaths);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new LocalFileSystemStoreScriptLoader();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        return new LocalFileSystemStoreTemplateLoader();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getBasePath()
     */
    public String getBasePath()
    {
        String fullPath = this.path;

        if (this.root != null)
        {
            if (!root.endsWith("/"))
            {
                root += "/";
            }

            if (root.startsWith("."))
            {
                // make relative to the web app real path
                fullPath = FrameworkHelper.getRealPath(this.root.substring(1)) + this.path;
            }
            else
            {
                fullPath = this.root + this.path;
            }
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
        if (file.exists())
        {
            if (file.isFile())
            {
                list.add(absPath);
            }
            else if (file.isDirectory())
            {
                // get all of the children
                String[] childDocumentPaths = file.list();
                for (int i = 0; i < childDocumentPaths.length; i++)
                {
                    String childAbsPath = absPath + File.separatorChar + childDocumentPaths[i];
                    gatherAbsolutePaths(childAbsPath, list);
                }
            }
        }
    }

    /**
     * Local File System Store implementation of a Script Loader
     * 
     * @author muzquiano
     */
    protected class LocalFileSystemStoreScriptLoader implements ScriptLoader
    {
        /**
         * @see org.alfresco.web.scripts.ScriptLoader#getScript(java.lang.String)
         */
        public ScriptContent getScript(String path)
        {
            ScriptContent sc = null;
            if (hasDocument(path))
            {
                sc = new LocalFileSystemStoreScriptContent(path);
            }
            return sc;
        }
    }

    /**
     * Local File System Store implementation of a Template Loader
     * 
     * @author muzquiano
     */
    private class LocalFileSystemStoreTemplateLoader implements TemplateLoader
    {
        /**
         * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
         */
        public void closeTemplateSource(Object templateSource)
                throws IOException
        {
            // nothing to do - we return a reader to fully retrieved in-memory
            // data
        }

        /**
         * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
         */
        public Object findTemplateSource(String name) throws IOException
        {
            LocalFileSystemStoreTemplateSource source = null;
            if (hasDocument(name))
            {
                source = new LocalFileSystemStoreTemplateSource(name);
            }
            return source;
        }

        /**
         * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
         */
        public long getLastModified(Object templateSource)
        {
            return ((LocalFileSystemStoreTemplateSource) templateSource).lastModified();
        }

        /**
         * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object,
         *      java.lang.String)
         */
        public Reader getReader(Object templateSource, String encoding)
                throws IOException
        {
            return ((LocalFileSystemStoreTemplateSource) templateSource).getReader(encoding);
        }
    }

    /**
     * Template Source - loads from a Local File System Store.
     * 
     * @author muzquiano
     */
    private class LocalFileSystemStoreTemplateSource
    {
        private String templatePath;

        private LocalFileSystemStoreTemplateSource(String path)
        {
            this.templatePath = path;
        }

        private long lastModified()
        {
            try
            {
                return LocalFileSystemStore.this.lastModified(templatePath);
            }
            catch (IOException e)
            {
                return -1;
            }
        }

        private Reader getReader(String encoding) throws IOException
        {
            Reader reader = null;

            File f = new File(toAbsolutePath(templatePath));
            if (f.exists())
            {
                reader = new FileReader(f);
            }

            return reader;
        }
    }

    /**
     * Script Content - loads from a Local File System Store.
     * 
     * @author muzquiano
     */
    private class LocalFileSystemStoreScriptContent implements ScriptContent
    {
        private String scriptPath;

        /**
         * Constructor
         * 
         * @param path Path to remote script content
         */
        private LocalFileSystemStoreScriptContent(String path)
        {
            this.scriptPath = path;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#getPath()
         */
        public String getPath()
        {
            return getBasePath() + '/' + this.scriptPath;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#getPathDescription()
         */
        public String getPathDescription()
        {
            return getBasePath() + '/' + this.scriptPath;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#getInputStream()
         */
        public InputStream getInputStream()
        {
            InputStream is = null;

            try
            {
                File f = new File(toAbsolutePath(scriptPath));
                if (f.exists())
                {
                    is = new FileInputStream(f);
                }
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException(
                        "Unable to load script: " + scriptPath, e);
            }

            return is;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#getReader()
         */
        public Reader getReader()
        {
            Reader reader = null;

            try
            {
                File f = new File(toAbsolutePath(scriptPath));
                if (f.exists())
                {
                    reader = new FileReader(f);
                }
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException(
                        "Unable to load script: " + scriptPath, e);
            }

            return reader;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#isSecure()
         */
        public boolean isSecure()
        {
            return false;
        }

        /**
         * @see org.alfresco.web.scripts.ScriptContent#isCachable()
         */
        public boolean isCachable()
        {
            return false;
        }
    }

    private class PatternFileFilter implements FileFilter
    {
        Pattern pattern;

        public PatternFileFilter(String pat)
        {

            // pat = pat.replaceAll("\\*", ".*");

            this.pattern = Pattern.compile(pat);
        }

        public boolean accept(File pathname)
        {
            if (pathname.isDirectory())
            {
                return false;
            }
            else
            {
                return pattern.matcher(pathname.getName()).matches();
            }
        }
    }

    private List<File> listPath(File path, FileFilter filter,
            boolean listChildren)
    {
        List<File> results = new ArrayList<File>();

        listPath(path, filter, results, listChildren);

        return results;
    }

    private void listPath(File path, FileFilter filter, List<File> results,
            boolean listChildren)
    {
        // list of files in this dir
        File files[] = path.listFiles(filter);
        if (files.length > 0)
        {
            // Sort with help of Collections API
            Arrays.sort(files);

            // add into the results
            for (int i = 0; i < files.length; i++)
            {
                results.add(files[i]);
            }
        }

        // dive down into the subdirectories?
        if (listChildren)
        {
            // list of all files
            files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                // walk through children if deemed to be thus
                if (files[i].isDirectory())
                {
                    // recursively descend dir tree
                    listPath(files[i], filter, results, listChildren);
                }
            }
        }
    }
}
