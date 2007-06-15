/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.module.phpIntegration.lib;

import org.alfresco.model.ContentModel;
import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.servlet.DownloadContentServlet;
import org.apache.log4j.Logger;


/**
 * 
 * 
 * @author Roy Wetherall
 */
public class ContentData implements ScriptObject
{
    /** Logger **/
    private static Logger logger = Logger.getLogger(ContentData.class);
    
    /** The name of the script extension */
    private static final String SCRIPT_OBJECT_NAME = "ContentData";
    
    /** Node that this content resides on */
    private Node node;
    
    /** The content property */
    private String property;
    
    /** The mimetype */
    private String mimetype;
    
    /** The encoding */
    private String encoding;
    
    /** The content size */
    private long size;
    
    /** Content service */
    private ContentService contentService;
    
    /** Indicates that the content data is dirty and there are unsaved changes */
    private boolean isDirty;
    
    private String updatedContentString;
    private String updatedContentLocation;
    
    /**
     * Constructor
     * 
     * @param node      the node
     * @param property  the content property
     * @param mimetype  the mimetype
     * @param encoding  the encoding
     */
    public ContentData(Node node, String property, String mimetype, String encoding)
    {
        this.node = node;
        this.property = property;
        this.mimetype = mimetype;
        this.encoding = encoding;
        this.isDirty = false;
        
        this.contentService = this.node.getSession().getServiceRegistry().getContentService();
    }
    
    /**
     * Constructor
     * 
     * @param node      the node       
     * @param property  the content property
     * @param mimetype  the mimetype     
     * @param encoding  the encoding
     * @param size      the size
     */
    public ContentData(Node node, String property, String mimetype, String encoding, long size)
    {
        this.node = node;
        this.property = property;
        this.mimetype = mimetype;
        this.encoding = encoding;
        this.size = size;
        this.isDirty = false;
        
        this.contentService = this.node.getSession().getServiceRegistry().getContentService();
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Get the mimetype
     * 
     * @return  the mimetype
     */
    public String getMimetype()
    {
        return this.mimetype;
    }
    
    /**
     * Set the mimetype 
     * 
     * @param mimetype  the mimetype
     */
    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
        this.isDirty = true;
        this.node.contentUpdated();
    }
    
    /**
     * Get the encoding
     * 
     * @return  the encoding
     */
    public String getEncoding()
    {
        return encoding;
    }
    
    /**
     * Set the encoding
     * 
     * @param encoding  the encoding
     */
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
        this.isDirty = true;
        this.node.contentUpdated();
    }
    
    /**
     * Get the size of the content
     * 
     * @return  the size of the content
     */
    public long getSize()
    {
        return this.size;
    }
    
    /**
     * Gets the download url for the content.  This URL has the ticket already appended for the containing session.
     * 
     *  @return String  the download URL for the content
     */
    public String getUrl()
    {
        // TODO this will do for now ...
        String url = DownloadContentServlet.generateBrowserURL(
                                this.node.getNodeRef(), 
                                (String)this.node.getSession().getServiceRegistry().getNodeService().getProperty(this.node.getNodeRef(), ContentModel.PROP_NAME));
        return "/alfresco" + url+"?ticket=" + this.node.getSession().getTicket();
    }
    
    /**
     * Gets the guest download URL for the content.  This URL can only be used to access the repo as a guest.
     * 
     * @return String   the guest foenload URL for the content.
     */
    public String getGuestUrl()
    {
        // TODO this will do for now ...
        String url = DownloadContentServlet.generateBrowserURL(
                this.node.getNodeRef(), 
                (String)this.node.getSession().getServiceRegistry().getNodeService().getProperty(this.node.getNodeRef(), ContentModel.PROP_NAME));
        return "/alfresco" + url + "?guest=true";
    }
    
    /**
     * Get the content string
     * 
     * @return  the content string
     */
    public String getContent()
    {
        String content = null;
        
        if (this.updatedContentString != null)
        {
            content = this.updatedContentString;
        }
        else if (this.updatedContentLocation != null)
        {
            // TODO .. get the content out of the file
        }
        else if (this.node.isNewNode() == false)
        {
            ContentReader contentReader = this.contentService.getReader(this.node.getNodeRef(), QName.createQName(this.property));
            if (contentReader != null)
            {
                content = contentReader.getContentString();
            }
        }
        
        return content;
    }
    
    /**
     * Set the content string
     * 
     * @param content   the content string
     */
    public void setContent(String content)
    {
        // Set the content string
        this.updatedContentString = content;
        this.size = content.length();
        this.isDirty = true;
        this.node.contentUpdated();
    }
    
    public void writeContentFromFile(String file)
    {
        // TODO
    }
    
    public void readContentToFile(String file)
    {
        // TODO
    } 
    
    /**
     * Saves any changes made to the content data
     *
     */
    /*package*/ void onSave()
    {
        if (this.isDirty == true)
        {
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("ContentData.onSave() - Getting content writer (node=" + this.node.getId() + "; property=" + this.property);
            }
            
            // Get the content writer
            ContentWriter contentWriter = this.contentService.getWriter(this.node.getNodeRef(), QName.createQName(this.property), true);
            if (contentWriter == null)
            {
                throw new PHPProcessorException("Unable to get content writer for property " + this.property + " on node " + this.node.toString());
            }
            
            if (logger.isDebugEnabled() == true)
            {
                logger.debug("ContentData.onSave() - Setting encoding and mimetype (node=" + this.node.getId() + "; property=" + this.property);
            }
            
            // Set the encoding and mimetype
            contentWriter.setEncoding(this.encoding);
            contentWriter.setMimetype(this.mimetype);
            
            // Put the updated content, id it had been updated
            if (this.updatedContentString != null)
            {                
                if (logger.isDebugEnabled() == true)
                {
                    logger.debug("ContentData.onSave() - Putting text content (node=" + this.node.getId() + "; property=" + this.property);
                    logger.debug("ContentData.onSave() - updatedContentString=" + this.updatedContentString);
                }
                
                contentWriter.putContent(this.updatedContentString);
            }
            else if (this.updatedContentLocation != null)
            {
                // TODO .. handle loading content from the file location .... 
            }
            
            // Clear the content date since the content has now been updated
            this.updatedContentLocation = null;
            this.updatedContentString = null;
            this.isDirty = false;
        }
                
    }
}
