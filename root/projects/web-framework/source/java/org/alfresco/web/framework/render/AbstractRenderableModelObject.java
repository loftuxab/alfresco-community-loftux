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
package org.alfresco.web.framework.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.framework.AbstractModelObject;
import org.alfresco.web.framework.ModelPersisterInfo;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Abstract base class for a renderable model object.
 * 
 * A renderable model object is one that has renderer processors
 * defined on it for one or more render modes.
 * 
 * @author muzquiano
 */
public abstract class AbstractRenderableModelObject extends AbstractModelObject implements Renderable
{
    private static final String PROP_PROCESSOR_ID = "id";
    public static String PROP_PROCESSOR = "processor";
    public static String ATTR_RENDER_MODE = "mode";
    
    // concurrent cache of RenderMode to Processor properties
    private final Map<RenderMode, HashMap<String, String>> processorPropertyCache =
        new ConcurrentHashMap<RenderMode, HashMap<String,String>>(8);
    
    // the render modes available to this renderable object
    private final RenderMode[] renderModes;
    
    
    /**
     * Constructs a new model object
     * 
     * @param document the document
     */
    public AbstractRenderableModelObject(String id, ModelPersisterInfo info, Document document)
    {
        super(id, info, document);
        
        // first cache the render modes available for this renderable object
        List<Element> processorElements = getDocument().getRootElement().elements(PROP_PROCESSOR);
        this.renderModes = new RenderMode[processorElements.size()];
        for (int i = 0; i < processorElements.size(); i++)
        {
            Element processorElement = processorElements.get(i);
            this.renderModes[i] = RenderMode.valueOf(processorElement.attributeValue(ATTR_RENDER_MODE).toUpperCase());
            
            // for each render mode, retrieve all processor properties
            HashMap<String, String> props = new HashMap<String, String>(4);
            List children = XMLUtil.getChildren(processorElement);
            for (int n = 0; n < children.size(); n++)
            {
                Element child = (Element)children.get(n);
                String name = child.getName();
                String value = XMLUtil.getChildValue(processorElement, name);
                
                props.put(name, value);
            }
            
            // store the properties in the cache against the rendermode
            this.processorPropertyCache.put(this.renderModes[i], props);
        }
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getProcessorId()
     */
    public String getProcessorId()
    {
        return getProcessorId(null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getProcessorId(java.lang.String)
     */
    public String getProcessorId(RenderMode mode)
    {
        return getProcessorProperty(mode, PROP_PROCESSOR_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getProcessorProperty(java.lang.String)
     */
    public String getProcessorProperty(String propertyName)
    {
        return getProcessorProperty(null, propertyName);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getProcessorProperty(java.lang.String, java.lang.String)
     */
    public String getProcessorProperty(RenderMode mode, String propertyName)
    {
        if (mode == null)
        {
            mode = RenderMode.VIEW;
        }
        
        return this.processorPropertyCache.get(mode).get(propertyName);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getProcessorProperties()
     */
    public Map<String, String> getProcessorProperties()
    {
        return getProcessorProperties(null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getProcessorProperties(java.lang.String)
     */
    public Map<String, String> getProcessorProperties(RenderMode renderMode)
    {
        if (renderMode == null)
        {
            renderMode = RenderMode.VIEW;
        }
        
        // return a clone of the available properties - as it can be modified later
        return (Map<String, String>)this.processorPropertyCache.get(renderMode).clone();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#setProcessorProperty(java.lang.String, java.lang.String)
     */
    public void setProcessorProperty(String propertyName, String propertyValue)
    {
        setProcessorProperty(null, propertyName, propertyValue);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#setProcessorProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public void setProcessorProperty(RenderMode renderMode, String propertyName, String propertyValue)
    {
        if (renderMode == null)
        {
            renderMode = RenderMode.VIEW;
        }
        
        Element processorElement = getProcessorElement(renderMode);
        if (processorElement == null)
        {
            processorElement = getDocument().getRootElement().addElement(PROP_PROCESSOR);
            processorElement.addAttribute(ATTR_RENDER_MODE, renderMode.toString());
        }
        XMLUtil.setChildValue(processorElement, propertyName, propertyValue);
        
        // update cache
        this.processorPropertyCache.get(renderMode).put(propertyName, propertyValue);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#removeProcessor()
     */
    public void removeProcessor()
    {
        removeProcessor(RenderMode.VIEW);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#removeProcessor(java.lang.String)
     */
    public void removeProcessor(RenderMode renderMode)
    {
        if (renderMode == null)
        {
            renderMode = RenderMode.VIEW;
        }
        
        Element processorElement = this.getProcessorElement(renderMode);
        if (processorElement != null)
        {
            processorElement.getParent().remove(processorElement);
        }                
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Renderable#getRenderModes()
     */
    public RenderMode[] getRenderModes()
    {
        return this.renderModes;
    }
    
    
    /**
     * Gets the processor element.
     * 
     * @param renderMode the render mode
     * 
     * @return the processor element
     */
    private Element getProcessorElement(RenderMode renderMode)
    {
        if (renderMode == null)
        {
            renderMode = RenderMode.VIEW;
        }
        
        Element result = null;
        
        List<Element> processorElements = getDocument().getRootElement().elements(PROP_PROCESSOR);
        for (int i = 0; i < processorElements.size(); i++)
        {
            Element processorElement = processorElements.get(i);
            String _renderMode = processorElement.attributeValue(ATTR_RENDER_MODE);
            if (renderMode.toString().equals(_renderMode))
            {
                result = processorElement;
                break;
            }
        }
        return result;
    }    
}
