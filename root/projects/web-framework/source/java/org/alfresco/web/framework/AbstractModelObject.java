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
package org.alfresco.web.framework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.FrameworkHelper;
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
 * @author muzquiano
 */
public abstract class AbstractModelObject implements ModelObject
{
    private static final String VERSION_UNKNOWN = "unknown";
    public static String PROP_ID = "id";
    public static String PROP_TITLE = "title";
    public static String PROP_DESCRIPTION = "description";
    public static String CONTAINER_PROPERTIES = "properties";
    
    // Note: These should note be final, they could be replaced in overrides
    // Specifically, the id is allowed to change
    protected Document document;
    protected ModelObjectKey key;
    protected String id;
    
    protected long modificationTime;
    protected String modelVersion;
    
    protected Map<String, Serializable> modelProperties;    
    protected Map<String, Serializable> customProperties;
    
    // cached values
    protected String title;
    protected String description;
    
    
    /**
     * Constructs a new model object
     * 
     * @param document the document
     */
    public AbstractModelObject(ModelObjectKey key, Document document)
    {
        this.key = key;
        this.document = document;
        this.id = key.getId();

        /**
         * The model version should be supplied with the serialized XML
         * but if it is not supplied, then we can assume it from one
         * of a number of places.
         * 
         * If we're unable to determine it, then we will set it to the
         * "unknown" flag
         */
        this.modelVersion = getProperty("model-version");
        if (this.modelVersion == null)
        {
            // allow configuration to specify
            this.modelVersion = FrameworkHelper.getConfig().getTypeDescriptor(this.getTypeId()).getVersion();
            if (this.modelVersion == null)
            {
                this.modelVersion = VERSION_UNKNOWN;
            }
        }        
    }
    
    /**
     * Constructor used by sentinel object
     */
    protected AbstractModelObject()
    {
        this.key = null;
        this.document = null;
        this.id = null;
    }

    /**
     * @return the key structure that represents this model object
     */
    public final ModelObjectKey getKey()
    {
        return this.key;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelVersion()
     */
    public final String getModelVersion()
    {
        return this.modelVersion;
    }


    ///////////////////////////////////////////////////////////////
    // common model properties
    ///////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getId()
     */
    public final String getId()
    {
        return this.id;
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTitle()
     */
    public final String getTitle()
    {
        if (this.title == null)
        {
            this.title = getProperty(PROP_TITLE);
        }
        return this.title;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setTitle(java.lang.String)
     */
    public final void setTitle(String title)
    {
        setProperty(PROP_TITLE, title);
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getDescription()
     */
    public final String getDescription()
    {
        if (this.description == null)
        {
            this.description = getProperty(PROP_DESCRIPTION);
        }
        return this.description;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setDescription(java.lang.String)
     */
    public final void setDescription(String value)
    {
        setProperty(PROP_DESCRIPTION, value);
        this.description = value;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#isSaved()
     */
    public final boolean isSaved()
    {
        return this.key.isSaved();
    }
    
    
    ///////////////////////////////////////////////////////////////
    // xml methods
    ///////////////////////////////////////////////////////////////    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getDocument()
     */
    public final Document getDocument()
    {
        return this.document;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#toXML()
     */
    public final String toXML()
    {
        return XMLUtil.toXML(document, true);
    }
    
    
    ///////////////////////////////////////////////////////////////
    // generic property accessors
    ///////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getBooleanProperty(java.lang.String)
     */
    public final boolean getBooleanProperty(String propertyName)
    {
        String val = getProperty(propertyName);
        return Boolean.parseBoolean(val);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getProperty(java.lang.String)
     */
    public final String getProperty(String propertyName)
    {
        if (isModelProperty(propertyName))
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
    public final void setProperty(String propertyName, String propertyValue)
    {
        if (isModelProperty(propertyName))
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
    public final void removeProperty(String propertyName)
    {
        if (isModelProperty(propertyName))
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
    protected final boolean isCustomProperty(String propertyName)
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
    protected final boolean isModelProperty(String propertyName)
    {
        return ModelHelper.isModelProperty(this, propertyName);
    }
    
    
    ////////////////////////////////////////////////////////////
    // Model Properties
    ////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelProperty(java.lang.String)
     */
    public final String getModelProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        return (String) getModelProperties().get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setModelProperty(java.lang.String, java.lang.String)
     */
    public final void setModelProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        // if the propertyValue is null, remove the property
        if (propertyValue == null)
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
    public final void removeModelProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
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
    public final String getCustomProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        return (String) getCustomProperties().get(propertyName);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setCustomProperty(java.lang.String, java.lang.String)
     */
    public final void setCustomProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        // if the propertyValue is null, remove the property
        if (propertyValue == null)
        {
            removeCustomProperty(propertyName);
            return;
        }
        
        // do the set
        Element properties = getDocument().getRootElement().element(CONTAINER_PROPERTIES);
        if (properties == null)
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
    public final void removeCustomProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
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
    public final Map<String, Serializable> getProperties()
    {
        Map<String, Serializable> properties = new HashMap<String, Serializable>(16, 1.0f);
        properties.putAll(getModelProperties());
        properties.putAll(getCustomProperties());
        return properties;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelProperties()
     */
    public final Map<String, Serializable> getModelProperties()
    {
    	if (this.modelProperties == null)
    	{
    		this.modelProperties = new HashMap<String, Serializable>(8, 1.0f);
    		
    		List elements = getDocument().getRootElement().elements();
	        for (int i = 0; i < elements.size(); i++)
	        {
	            Element el = (Element) elements.get(i);
	            String elementName = el.getName();
	            if (elementName != null)
	            {
	                if (!CONTAINER_PROPERTIES.equals(elementName))
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
    public final Map<String, Serializable> getCustomProperties()
    {
    	if (this.customProperties == null)
    	{
    		this.customProperties = new HashMap<String, Serializable>(4, 1.0f);
    		
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
    public final long getModificationTime()
    {
        return this.modificationTime;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setModificationTime(long)
     */
    public final void setModificationTime(long modificationTime)
    {
        this.modificationTime = modificationTime;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#touch()
     */
    public final void touch()
    {
        setModificationTime(System.currentTimeMillis());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTypeId()
     */
    public abstract String getTypeId();
        
       
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getTypeId() + ": " + getId() + ", " + toXML();
    }
    
    /**
     * Returns the ModelObjectPersister id that this object is bound to
     */
    public final String getPersisterId()
    {
        return this.key.getPersisterId();
    }
    
    /**
     * Returns the persistence storage path of this object 
     */
    public final String getStoragePath()
    {
        return this.key.getStoragePath();
    }
}
