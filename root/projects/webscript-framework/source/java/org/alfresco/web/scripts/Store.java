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

import java.io.IOException;
import java.io.InputStream;

import freemarker.cache.TemplateLoader;


/**
 * Store for holding Web Script Definitions and Implementations
 * 
 * @author davidc
 */
public interface Store
{
	/**
	 * Initialise Store (called once) 
	 */
	public void init();
    
    /**
     * Determines whether the store actually exists
     * 
     * @return  true => it does exist
     */
    public boolean exists();
    
    /**
     * Gets the base path of the store
     *  
     * @return base path
     */
    public String getBasePath();
    
    /**
     * Returns true if this store is considered secure - i.e. on the app-server classpath. Scripts in secure stores can
     * be run under the identity of a declared user (via the runas attribute) rather than the authenticated user.
     * 
     * @return true if this store is considered secure
     */
    public boolean isSecure();
    
    
    /**
     * Gets the paths of given document pattern within given path/sub-paths in this store
     * 
     * @param path
     *            start path
     * @param includeSubPaths
     *            if true, include sub-paths
     * @param documentPattern
     *            document name, allows wildcards, eg. *.ftl or my*.ftl
     * @return array of document paths
     * @throws IOException
     *             If an error occurs searching for documents
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern) throws IOException;
    
    /**
     * Gets the paths of all Web Script description documents in this store
     * 
     * @return array of description document paths
     * @throws IOException
     *             If an error occurs searching for documents
     */
    public String[] getDescriptionDocumentPaths() throws IOException;

    /**
     * Gets the paths of all implementation files for a given Web Script
     * 
     * @param script  web script
     * @return  array of implementation document paths
     * @throws IOException
     *             If an error occurs searching for documents
     */
    public String[] getScriptDocumentPaths(WebScript script) throws IOException;
    
    /**
     * Gets the paths of all documents in this store
     * 
     * @return array of all document paths
     */
    public String[] getAllDocumentPaths();

    /**
     * Gets the last modified timestamp for the document.
     * 
     * @param documentPath  document path to an existing document
     * @return  last modified timestamp
     * 
     * @throws IOException if the document does not exist in the store
     */
    public long lastModified(String documentPath)
        throws IOException;
    
    /**
     * Determines if the document exists.
     * 
     * @param documentPath
     *            document path
     * @return true => exists, false => does not exist
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public boolean hasDocument(String documentPath) throws IOException;

    /**
     * Gets a document
     * 
     * @param documentPath  document path
     * @return  input stream onto document
     * 
     * @throws IOException if the document does not exist in the store
     */
    public InputStream getDocument(String documentPath)
        throws IOException;
    
    /**
     * Creates a document.
     * 
     * @param documentPath  document path
     * @param content       content of the document to write
     * 
     * @throws IOException if the document already exists or the create fails
     */
    public void createDocument(String documentPath, String content)
        throws IOException;
    
    /**
     * Updates an existing document.
     * 
     * @param documentPath  document path
     * @param content       content to update the document with
     * 
     * @throws IOException if the document does not exist or the update fails
     */
    public void updateDocument(String documentPath, String content)
        throws IOException;
    
    /**
     * Removes an existing document.
     * 
     * @param documentPath  document path
     * @return  whether the operation succeeded
     * 
     * @throws IOException if the document does not exist or the remove fails
     */
    public boolean removeDocument(String documentPath)
        throws IOException;
    
    /**
     * Gets the template loader for this store
     * 
     * @return  template loader
     */
    public TemplateLoader getTemplateLoader();
    
    /**
     * Gets the script loader for this store
     * 
     * @return  script loader
     */
    public ScriptLoader getScriptLoader();    
}