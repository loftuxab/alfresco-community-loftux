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

import org.alfresco.web.framework.exception.ResourceMetadataException;
import org.alfresco.web.framework.resource.Resource;
import org.alfresco.web.framework.resource.ResourceContent;
import org.alfresco.web.site.RequestContext;

/*
 * @author muzquiano
 */
public final class ScriptResource extends ScriptBase
{
    protected ScriptContentObject scriptContentObject = null;
    protected Resource resource;

    public ScriptResource(RequestContext context, Resource resource)
    {
        super(context);
        
        this.resource = resource;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        if (this.properties == null)
        {
        }
        
        return null;
    }
    
    // --------------------------------------------------------------
    // JavaScript Properties
    //

    public String getId()
    {
        return this.resource.getId();
    }
    
    public String getValue()
    {
        return this.resource.getValue();
    }
    
    public void setValue(Object value)
    {
        this.resource.setValue((String) value);
    }    

    public String getAttribute(String name)
    {
        return (String) this.resource.getAttribute(name);
    }

    public void setAttribute(String name, String value)
    {
        this.resource.setAttribute(name, value);
    }
    
    public String getType()
    {
        return (String) this.resource.getType();
    }
    
    public void setType(String type)
    {
        this.resource.setType(type);
    }
                
    public String getEndpoint()
    {
        return this.resource.getEndpoint();
    }
    
    public void setEndpoint(String endpoint)
    {
        this.resource.setEndpoint(endpoint);
    }
    
    public String getDownloadURI()
    {
        return this.resource.getDownloadURI(context);
    }

    public String getProxiedDownloadURI()
    {
        return this.resource.getProxiedDownloadURI(context);
    }
    
    public String getMetadataURI()
    {
        return this.resource.getMetadataURI(context);
    }    

    public String getProxiedMetadataURI()
    {
        return this.resource.getProxiedMetadataURI(context);
    }    
    
    public String getMetadata()
    {
        String metadata = null;
    
        try
        {
            metadata = this.resource.getMetadata(context);
        }
        catch (ResourceMetadataException rme)
        {
            rme.printStackTrace();
        }
        
        return metadata;
    }
    
    public ScriptContentObject getContent()
    {
        if (this.scriptContentObject == null)
        {
            ResourceContent resourceContent = this.resource.getContent(context);
            
            this.scriptContentObject = new ScriptContentObject(context, resourceContent);
        }
        
        return this.scriptContentObject;        
    }
}

