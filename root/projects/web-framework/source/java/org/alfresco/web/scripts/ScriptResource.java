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

import org.alfresco.web.framework.resource.Resource;
import org.alfresco.web.site.RequestContext;
import org.mozilla.javascript.Scriptable;

/*
 * @author muzquiano
 */
public final class ScriptResource extends ScriptBase
{
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
        if(this.properties == null)
        {
            this.properties = new ScriptableLinkedHashMap<String, Serializable>()
            {
                // trap this method so that we can adjust the model object
                public void put(String name, Scriptable start, Object value)
                {
                	put(name, (Serializable)value);
                
            		// adding or updating an attribute on a resource
            		resource.setAttribute(name, (String)value);
                }

                // do not allow
                public void put(int index, Scriptable start, Object value)
                {
                }

                // trap this method so that we can adjust the model object
                public void delete(String name)
                {
                	remove(name);
                	
                	// removing an attribute on a resource
                	resource.removeAttribute(name);
                }

                // do not allow
                public void delete(int index)
                {
                }
            };

            // copy in resource attributes
            String[] names = this.resource.getAttributeNames();
            for(int i = 0; i < names.length; i++)
            {
            	String value = this.resource.getAttribute(names[i]);
            	this.properties.put(names[i], value);
	        }                     
        }
        
        return this.properties;
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

    public String getAttributeValue(String name)
    {
    	return (String) this.getProperties().get(name);
    }
        
    public String getType()
    {
    	return (String) this.getProperties().get("type");
    }
    
    public void setType(Object value)
    {
    	this.getProperties().put("type", (Serializable) value);
    }
                
    public String getEndpoint()
    {
    	return (String) this.getProperties().get("endpoint");
    }
    
    public void setEndpoint(Object value)
    {
    	this.getProperties().put("endpoint", (Serializable) value);
    }
    
    public String getDownloadURI()
    {
    	return this.resource.getDownloadURI(context.getRequest());
    }

    public String getProxiedDownloadURI()
    {
    	return this.resource.getProxiedDownloadURI(context.getRequest());
    }
    
    public String getMetadataURI()
    {
    	return this.resource.getMetadataURI(context.getRequest());
    }    

    public String getProxiedMetadataURI()
    {
    	return this.resource.getProxiedMetadataURI(context.getRequest());
    }    
    
    public String getMetadata()
    {
    	return this.resource.getMetadata(context.getRequest());    	
    }
}

