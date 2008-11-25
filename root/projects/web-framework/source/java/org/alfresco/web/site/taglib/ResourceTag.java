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
package org.alfresco.web.site.taglib;

import javax.servlet.jsp.JspException;

import org.alfresco.tools.ObjectGUID;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.resource.Resource;
import org.alfresco.web.framework.resource.ResourceProvider;
import org.alfresco.web.framework.resource.TransientResourceImpl;

/**
 * Binds in the Download URL of a resource
 * 
 * <alf:resource id="resourceId" />
 * 
 * @author muzquiano
 */
public class ResourceTag extends TagBase
{
    private String id = null;
    private String target = null;
    private String type = null;
    private String endpoint = null;
    private String value = null;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }
    
    public void setTarget(String target)
    {
        this.target = target;
    }
    
    public String getTarget()
    {
        return this.target;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return this.type;
    }
    
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    public String getEndpoint()
    {
        return this.endpoint;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return this.value;
    }
    
    public int doStartTag() throws JspException
    {
        RenderContext context = getRenderContext();

        ModelObject object = context.getObject();
        if (object != null)
        {
            try
            {                
                // if the resource is identified by ID
                // and the object is a resource provider
                if (getId() != null && object instanceof ResourceProvider)
                {
                    ResourceProvider provider = (ResourceProvider) object;
                    Resource resource = provider.getResource(this.id);
                
                    String uri = resource.getProxiedDownloadURI(this.getRequestContext());                
                    if ("metadata".equalsIgnoreCase(this.target))
                    {
                        uri = resource.getProxiedMetadataURI(this.getRequestContext());
                    }
                
                    this.getOut().write(uri);
                }
                else if (getType() != null)
                {                    
                    // allow for creation of transient, run-time identified resources
                    
                    // generate a temporary resource id
                    String id = new ObjectGUID().toString();
                    
                    // create a resource
                    TransientResourceImpl resource = new TransientResourceImpl(id, getType());
                                                            
                    // set the value
                    String v = getValue();
                    if (v == null)
                    {
                        v = "";
                    }
                    resource.setValue(v);
                    
                    // set the endpoint
                    String ep = getEndpoint();
                    if (ep == null)
                    {
                        ep = "alfresco";
                    }
                    resource.setEndpoint(ep);
                    
                    // construct the proxied uri
                    String uri = resource.getProxiedDownloadURI(this.getRequestContext());                
                    if ("metadata".equalsIgnoreCase(this.target))
                    {
                        uri = resource.getProxiedMetadataURI(this.getRequestContext());
                    }
                
                    this.getOut().write(uri);
                }
            }
            catch (Throwable t)
            {
                throw new JspException(t);
            }
        }
        return SKIP_BODY;
    }
    
    public void release()
    {
        this.id = null;
        
        super.release();
    }
    
}
