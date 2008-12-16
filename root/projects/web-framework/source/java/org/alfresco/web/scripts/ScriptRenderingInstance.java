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
import java.util.Iterator;
import java.util.Map;

import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.WebFrameworkConstants;
import org.mozilla.javascript.Scriptable;

/**
 * Wraps the renderer instance and provisions properties about the currently
 * rendering item to the script writer.
 * 
 * The following is valid:
 * 
 * var object = instance.object;
 * var objectId = instance.id;
 * var user = instance.user;
 * var renderingProperties = instance.properties;
 */
public final class ScriptRenderingInstance extends ScriptBase
{
    final private RenderContext renderContext;
    
    /**
     * Instantiates a new script renderer instance.
     * 
     * @param context the render context
     */
    public ScriptRenderingInstance(RenderContext context)
    {
        super(context);
        
        this.renderContext = context;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableLinkedHashMap<String, Serializable>(
                    this.renderContext.getObject().getProperties());
        }
        
        return this.properties;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Gets the object.
     * 
     * @return the object
     */
    public ScriptModelObject getObject()
    {
        return new ScriptModelObject(this.renderContext, this.renderContext.getObject());
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return this.context.getId();
    }

    /**
     * Gets the html id
     * 
     * @return
     */
    public String getHtmlId()
    {
        return (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
    }
    
    /**
     * Returns the names of request parameters
     * 
     * @return array of names (String)
     */
    public String[] getParameterNames()
    {
        Map map = this.renderContext.getRequest().getParameterMap();        
        String[] names = new String[map.size()];
        
        int i = 0;
        Iterator it = map.keySet().iterator();
        while (it.hasNext())
        {
            names[i] = (String) it.next();
            i++;
        }
        
        return names;        
    }
    
    /**
     * Returns the value of a request parameter
     * 
     * @param name
     * @return
     */
    public Object getParameter(String name)
    {
        return (String) this.renderContext.getRequest().getParameter(name);
    }
    
    /**
     * Returns a scriptable map of name/value pairs
     * 
     * @return
     */
    public Scriptable getParameters()
    {
        Map map = this.renderContext.getRequest().getParameterMap();
        return ScriptHelper.toScriptableMap(map);
    }      
}
