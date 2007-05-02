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
import org.alfresco.web.app.servlet.DownloadContentServlet;


/**
 * 
 * 
 * @author Roy Wetherall
 */
public class ContentData implements ScriptObject
{
    /** The name of the script extension */
    private static final String SCRIPT_OBJECT_NAME = "ContentData";
    
    private Node node;
    private String property;
    private String mimetype;    
    private String encoding;
    private long size;
    
    public ContentData(Node node, String property, String mimetype, String encoding)
    {
        this.node = node;
        this.property = property;
        this.mimetype = mimetype;
        this.encoding = encoding;
    }
    
    public ContentData(Node node, String property, String mimetype, String encoding, long size)
    {
        this.node = node;
        this.property = property;
        this.mimetype = mimetype;
        this.encoding = encoding;
        this.size = size;
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    public String getMimetype()
    {
        return this.mimetype;
    }
    
    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }
    
    public String getEncoding()
    {
        return encoding;
    }
    
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    public long getSize()
    {
        return this.size;
    }
    
    public String getUrl()
    {
        // TODO this will do for now ...
        String url = DownloadContentServlet.generateBrowserURL(
                                this.node.getNodeRef(), 
                                (String)this.node.getSession().getServiceRegistry().getNodeService().getProperty(this.node.getNodeRef(), ContentModel.PROP_NAME));
        return "/alfresco" + url+"?ticket=" + this.node.getSession().getTicket();
    }
    
    public String getGuestUrl()
    {
        // TODO this will do for now ...
        String url = DownloadContentServlet.generateBrowserURL(
                this.node.getNodeRef(), 
                (String)this.node.getSession().getServiceRegistry().getNodeService().getProperty(this.node.getNodeRef(), ContentModel.PROP_NAME));
        return "/alfresco" + url + "?guest=true";
    }
    
    public String getContent()
    {
        return null;
    }
    
    public void setContent(String content)
    {
        // TODO
    }
    
}
