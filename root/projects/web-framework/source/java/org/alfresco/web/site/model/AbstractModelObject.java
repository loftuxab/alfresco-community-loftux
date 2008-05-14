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
package org.alfresco.web.site.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Abstract base class that can be extended to introduce custom model
 * objects into the framework.  Custom model objects must be registered
 * with the configuration file.  Once done, they can be loaded and
 * persisted along with other model objects.
 * 
 * All model classes extending from this class are expected to have
 * "id", "title" and "description" fields.
 * 
 * If no id field is provided, the id assumed from the file name.
 * 
 * @author muzquiano
 */
public abstract class AbstractModelObject implements ModelObject
{
    public static String PROP_ID = "id";
    public static String PROP_TITLE = "title";
    public static String PROP_DESCRIPTION = "description";
    public static String CONTAINER_PROPERTIES = "properties";
    protected Document document;
    protected String id;
    protected String relativePath;
    protected String fileName;
    protected boolean isSaved;
    protected long modificationTime;
    protected String modelVersion;
    protected Map<String, Object> modelProperties;    
    protected Map<String, Object> customProperties;
    
    /**
     * Constructs a new model object
     * 
     * @param document the document
     */
    public AbstractModelObject(Document document)
    {
        this.document = document;

        /**
         * The model version should be supplied with the serialized XML
         * but if it is not supplied, then we can assume it from one
         * of a number of places.
         * 
         * If we're unable to determine it, then we will set it to the
         * "unknown" flag
         */
        this.modelVersion = getProperty("model-version");
        if(this.modelVersion == null)
        {
            // allow configuration to specify
            this.modelVersion = FrameworkHelper.getConfig().getTypeDescriptor(this.getTypeName()).getVersion();
            if(this.modelVersion == null)
            {
                this.modelVersion = "unknown";
            }
        }        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelVersion()
     */
    public String getModelVersion()
    {
        return this.modelVersion;
    }

    ///////////////////////////////////////////////////////////////
    // common model properties
    ///////////////////////////////////////////////////////////////

    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getId()
     */
    public String getId()
    {
        if(this.id == null)
        {
            // if it is NOT on the object, then we can assume the file name
            String fileName = this.getFileName();

            // strip off the extension
            int i = fileName.lastIndexOf(".");
            if(i > -1)
            {
                this.id = fileName.substring(0,i);
            }            
        }
        return this.id;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTitle()
     */
    public String getTitle()
    {
        return getProperty(PROP_TITLE);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        setProperty(PROP_TITLE, title);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getDescription()
     */
    public String getDescription()
    {
        return getProperty(PROP_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setDescription(java.lang.String)
     */
    public void setDescription(String value)
    {
        setProperty(PROP_DESCRIPTION, value);
    }
    
    
    ///////////////////////////////////////////////////////////////
    // persistence methods
    ///////////////////////////////////////////////////////////////    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#save(org.alfresco.web.site.RequestContext)
     */
    public void save(RequestContext context)
    {
        context.getModel().saveObject(context, this);
    }

    // TODO
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#reload(org.alfresco.web.site.RequestContext)
     */
    public void reload(RequestContext context)
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#remove(org.alfresco.web.site.RequestContext)
     */
    public void remove(RequestContext context)
    {
        context.getModel().removeObject(context, this);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#isSaved()
     */
    public boolean isSaved()
    {
        return this.isSaved;
    }
    
    
    ///////////////////////////////////////////////////////////////
    // xml methods
    ///////////////////////////////////////////////////////////////    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getDocument()
     */
    public Document getDocument()
    {
        return this.document;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#toXML()
     */
    public String toXML()
    {
        return XMLUtil.toXML(document, true);
    }
    
    
    ///////////////////////////////////////////////////////////////
    // generic property accessors
    ///////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getBooleanProperty(java.lang.String)
     */
    public boolean getBooleanProperty(String propertyName)
    {
        String val = getProperty(propertyName);
        if (val == null)
            return false;
        return ("true".equals(val));
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getProperty(java.lang.String)
     */
    public String getProperty(String propertyName)
    {
        if (propertyName == null)
            return null;
        
        if(isModelProperty(propertyName))
        {
            return getModelProperty(propertyName);
        }
        else
        {
            return getCustomProperty(propertyName);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
            return;
        
        if(isModelProperty(propertyName))
        {
            setModelProperty(propertyName, propertyValue);
        }
        else
        {
            setCustomProperty(propertyName, propertyValue);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#removeProperty(java.lang.String)
     */
    public void removeProperty(String propertyName)
    {
        if (propertyName == null)
            return;
        
        if(isModelProperty(propertyName))
        {
            removeModelProperty(propertyName);
        }
        else
        {
            removeCustomProperty(propertyName);
        }
    }
    
    
    /**
     * Uses reflection to determine whether the given property name
     * is a custom property.  A custom property is a non-model-specific
     * property.  Custom properties are written under the <properties/>
     * container element in the XML.
     * 
     * @param propertyName the property name
     * 
     * @return true, if checks if is custom property
     */
    protected boolean isCustomProperty(String propertyName)
    {
        return (!isModelProperty(propertyName));        
    }
    
    /**
     * Uses reflection to determine whether the given property name
     * is a model property.  Model properties are written directly
     * under the root element of the XML document.
     * 
     * @param propertyName the property name
     * 
     * @return true, if checks if is model property
     */
    protected boolean isModelProperty(String propertyName)
    {
        if(propertyName == null)
        {
            return false;
        }

        return ModelHelper.isModelProperty(this, propertyName);
    }
    
    

    ////////////////////////////////////////////////////////////
    // Model Properties
    ////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelProperty(java.lang.String)
     */
    public String getModelProperty(String propertyName)
    {
        if (propertyName == null)
        {
            return null;
        }
        
        return (String) getModelProperties().get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setModelProperty(java.lang.String, java.lang.String)
     */
    public void setModelProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
        {
            return;
        }
        
        // if the propertyValue is null, remove the property
        if(propertyValue == null)
        {
            removeModelProperty(propertyName);
            return;
        }

        // do the set
        Element el = getDocument().getRootElement().element(propertyName);
        if (el == null)
        {
            el = getDocument().getRootElement().addElement(propertyName);
        }

        // put value
        el.setText(propertyValue);
        
        // update cache
        getModelProperties().put(propertyName, propertyValue);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#removeModelProperty(java.lang.String)
     */
    public void removeModelProperty(String propertyName)
    {
        if (propertyName == null)
        {
            return;
        }

        // do the remove
        Element el = getDocument().getRootElement().element(propertyName);
        if (el != null)
        {
            getDocument().getRootElement().remove(el);

        	// update the cache
        	getModelProperties().remove(propertyName);            
        }
    }

    
    
    
    ////////////////////////////////////////////////////////////
    // Custom Properties
    ////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getCustomProperty(java.lang.String)
     */
    public String getCustomProperty(String propertyName)
    {
        if (propertyName == null)
        {
            return null;
        }
        
        return (String) getCustomProperties().get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setCustomProperty(java.lang.String, java.lang.String)
     */
    public void setCustomProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
        {
            return;
        }
        
        // if the propertyValue is null, remove the property
        if(propertyValue == null)
        {
            removeCustomProperty(propertyName);
            return;
        }
        
        // do the set
        Element properties = getDocument().getRootElement().element(CONTAINER_PROPERTIES);
        if(properties == null)
        {
            properties = getDocument().getRootElement().addElement(CONTAINER_PROPERTIES);
        }
        
        Element el = properties.element(propertyName);
        if (el == null)
        {
            el = properties.addElement(propertyName);
        }

        // put value
        el.setText(propertyValue);
        
        // update the cache
        getCustomProperties().put(propertyName, propertyValue);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#removeCustomProperty(java.lang.String)
     */
    public void removeCustomProperty(String propertyName)
    {
        if (propertyName == null)
        {
            return;
        }
        
        // do the remove
        Element properties = getDocument().getRootElement().element("properties");
        if(properties != null)
        {
            Element el = properties.element(propertyName);
            if (el != null)
            {
            	properties.remove(el);
            	
            	// update the cache
            	getCustomProperties().remove(propertyName);
            }
        }
    }

    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getProperties()
     */
    public Map<String, Object> getProperties()
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(getModelProperties());
        properties.putAll(getCustomProperties());
        return properties;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelProperties()
     */
    public Map<String, Object> getModelProperties()
    {
    	if(this.modelProperties == null)
    	{
    		modelProperties = new HashMap<String, Object>(16);

    		List elements = getDocument().getRootElement().elements();
	        for (int i = 0; i < elements.size(); i++)
	        {
	            Element el = (Element) elements.get(i);
	            String elementName = el.getName();
	            if(elementName != null)
	            {
	                if(!CONTAINER_PROPERTIES.equals(elementName))
	                {
	                    String elementValue = el.getStringValue();
	                    this.modelProperties.put(elementName, elementValue);
	                }
	            }
	        }
    	}
        return this.modelProperties;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getCustomProperties()
     */
    public Map<String, Object> getCustomProperties()
    {
    	if (this.customProperties == null)
    	{
    		this.customProperties = new HashMap<String, Object>(8);
    		
    		Element properties = getDocument().getRootElement().element(CONTAINER_PROPERTIES);
	        if (properties != null)
	        {
	            List<Element> elements = properties.elements();
	            for (int i = 0; i < elements.size(); i++)
	            {
	                Element el = elements.get(i);
	                this.customProperties.put(el.getName(), el.getTextTrim());
	            }
	        }     
    	}
    	return this.customProperties;
    }
    


    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModificationTime()
     */
    public long getModificationTime()
    {
        return this.modificationTime;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setModificationTime(long)
     */
    public void setModificationTime(long modificationTime)
    {
        this.modificationTime = modificationTime;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#touch()
     */
    public void touch()
    {
        setModificationTime(System.currentTimeMillis());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getRelativePath()
     */
    public String getRelativePath()
    {
        return relativePath;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getFileName()
     */
    public String getFileName()
    {
        return this.fileName;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getRelativeFilePath()
     */
    public String getRelativeFilePath()
    {
        return getRelativePath() + "/" + getFileName();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTypeName()
     */
    public abstract String getTypeName();
    
    
    
    ////////////////////////////////////////////////////////
    //
    // TODO:  These should be refactored
    //
    ////////////////////////////////////////////////////////

    /**
     * Sets the relative path.
     * 
     * @param relativePath the new relative path
     */
    protected void setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
    }
    
    /**
     * Sets the file name.
     * 
     * @param fileName the new file name
     */
    protected void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    /**
     * Sets the saved.
     * 
     * @param b the new saved
     */
    protected void setSaved(boolean b)
    {
        this.isSaved = b;
    }
       
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.document.toString();
    }
}
