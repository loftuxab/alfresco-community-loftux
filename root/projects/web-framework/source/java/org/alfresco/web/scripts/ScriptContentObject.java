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

import java.io.Serializable;

import org.alfresco.web.site.Content;
import org.alfresco.web.site.RequestContext;

/**
 * A read-only representation of a content object.
 * 
 * A content object is determined during construction of the request context
 * and is bound to the RequestContext.  Here, it is wrapped and then
 * provisioned with lightweight accessors so that it can be used within
 * Freemarker templates and JavaScript code.
 * 
 * The following uses are valid:
 * 
 * var id = content.id;
 * var typeId = content.typeId;
 * var endpointId = content.endpointId;
 * var timestamp = content.timestamp;
 * var isLoaded = content.isLoaded;
 * var statusCode = content.statusCode;
 * var statusMessage = content.statusMessage;
 * 
 * Properties of the content itself are stored on an associative
 * array named "properties":
 * 
 * var customData = content.properties["cm:customData"];
 * 
 * @author muzquiano
 */
public final class ScriptContentObject extends ScriptBase
{
    protected Content content;
    
    /**
     * Instantiates a new script content.
     * 
     * @param context the context
     * @param content the content
     */
    public ScriptContentObject(RequestContext context, Content content)
    {
        super(context);
        
        // store a reference to the content
        this.content = content;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    public ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            if (content.getProperties() != null)
            {
                this.properties = new ScriptableMap<String, Serializable>(content.getProperties());
            }
        }
        
        return this.properties;
    }    
    

    //------------------------------------------------------------
    // JavaScript Properties
    //

    public String getId()
    {
        return this.content.getId();
    }
    
    public String getTypeId()
    {
        return this.content.getTypeId();
    }
    
    public long getTimestamp()
    {
        return this.content.getTimestamp();
    }
    
    public Object getProperty(String propertyName)
    {
        return this.content.getProperty(propertyName);
    }
    
    public String getEndpointId()
    {
        return this.content.getEndpointId();
    }
    
    public boolean getIsLoaded()
    {
        return this.content.isLoaded();
    }

    public int getStatusCode()
    {
        return this.content.getStatusCode();
    }

    public String getStatusMessage()
    {
        return this.content.getStatusMessage();
    } 
    
    // --------------------------------------------------------------
    // JavaScript Functions
    //
    
}