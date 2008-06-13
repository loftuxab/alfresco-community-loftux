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
package org.alfresco.web.framework.model;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.web.framework.AbstractModelObject;
import org.alfresco.web.framework.ModelObjectKey;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;

/**
 * TemplateInstance model object
 * 
 * @author muzquiano
 */
public class TemplateInstance extends AbstractModelObject
{
    public static String TYPE_ID = "template-instance";
    public static String PROP_TEMPLATE_TYPE = "template-type";
    
    protected Map<String, Component> components = new HashMap<String, Component>(16, 1.0f);
    
    /**
     * Instantiates a new template instance for a given XML document
     * 
     * @param document the document
     */
    public TemplateInstance(ModelObjectKey key, Document document)
    {
        super(key, document);
    }

    /**
     * Gets the template type.
     * 
     * @return the template type
     */
    public String getTemplateType()
    {
        return getProperty(PROP_TEMPLATE_TYPE);
    }

    /**
     * Sets the template type.
     * 
     * @param templateType the new template type
     */
    public void setTemplateType(String templateType)
    {
        setProperty(PROP_TEMPLATE_TYPE, templateType);
    }

    /**
     * Gets the template type.
     * 
     * @param context the context
     * 
     * @return the template type
     */
    public TemplateType getTemplateType(RequestContext context)
    {
        // either 'global', template or page
        return context.getModel().getTemplateType(getTemplateType());
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }
    
    /**
     * Returns the components that were bound to this template during the
     * template rendering.  This is useful to determine what other components
     * are configured on the current page.
     * 
     * If no rendering components are set, null will be returned
     * 
     * @return  An array of Component objects
     */
    public Component[] getRenderingComponents()
    {
        if (this.components.size() == 0)
        {
            return null;
        }
        else
        {
            return this.components.values().toArray(new Component[this.components.size()]);
        }
    }
    
    /**
     * Indicates that the given component is being rendered as part of
     * the rendering execution for this Template Instance object
     *  
     * @param component The component that is being rendered
     */
    public void setRenderingComponent(Component component)
    {
        this.components.put(component.getId(), component);        
    }
    
    /**
     * Resets the rendering components
     */
    public void resetRenderingComponents()
    {
        this.components.clear();
    }
}
