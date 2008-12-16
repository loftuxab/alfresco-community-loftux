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

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.resource.Resource;
import org.alfresco.web.framework.resource.ResourceProvider;
import org.alfresco.web.site.RequestContext;

/**
 * Provides an interface to resources
 * 
 * var id = resources.get("abc").id;
 * var type = resources.get("abc").type;
 * 
 * var downloadUrl = resources.get("abc").downloadUrl;
 * var metadataUrl = resources.get("def").metadataUrl;
 * 
 * @author muzquiano
 */
public final class ScriptResources extends ScriptBase
{
    private static final long serialVersionUID = -3378946227712931201L;
    
    final private ModelObject modelObject;
    
    /**
     * Instantiates a new resources object
     * 
     * @param context the request context
     * @param modelObject the model object
     */
    public ScriptResources(RequestContext context, ModelObject modelObject)
    {
        super(context);

        this.modelObject = modelObject;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractScriptableObject#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        return null;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Functions
        
    /**
     * Returns the model object
     * 
     * @return
     */
    public ModelObject getModelObject()
    {
        return this.modelObject;
    }
    
    public ScriptResource get(String id)
    {
        ScriptResource scriptResource = null;
        
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;                        
            
            // now add
            Resource resource = provider.getResource(id);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
            }
        }
        
        return scriptResource;        
    }
    
    public void remove(String id)
    {
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;                        
            provider.removeResource(id);
        }
    }
    
    public ScriptResource add(String id)
    {
        return add(id, null);
    }

    public ScriptResource add(String id, String type)
    {
        ScriptResource scriptResource = null;
        
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;                        
            
            // now add
            Resource resource = provider.addResource(id, type);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
            }
        }
        
        return scriptResource;
    }
}
