/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.Framework;
import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author muzquiano
 */
public abstract class AbstractModelObject implements ModelObject
{
    public static String PROP_ID = "id";
    public static String PROP_TITLE = "title";
    public static String PROP_DESCRIPTION = "description";
    
    public static String CONTAINER_PROPERTIES = "properties";
    
    protected Document document;
    protected String relativePath;
    protected String fileName;
    protected boolean isSaved;
    protected long modificationTime;
    
    public AbstractModelObject(Document document)
    {
        this.document = document;
        
        // model version
        String modelVersion = getProperty("model-version");
        if(modelVersion == null)
        {
            // use the declared version if none available
            modelVersion = Framework.getConfig().getModelTypeVersion(this.getTypeName());
            if(modelVersion == null)
            {
                modelVersion = "unknown";
            }
        }
        
    }
    
    public String getModelVersion()
    {
        return getProperty("model-version");
    }

    ///////////////////////////////////////////////////////////////
    // common model properties
    ///////////////////////////////////////////////////////////////

    protected String id;
    
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
    
    public String getTitle()
    {
        return getProperty(PROP_TITLE);
    }
    
    public void setTitle(String title)
    {
        setProperty(PROP_TITLE, title);
    }

    public String getDescription()
    {
        return getProperty(PROP_DESCRIPTION);
    }

    public void setDescription(String value)
    {
        setProperty(PROP_DESCRIPTION, value);
    }
    
    
    ///////////////////////////////////////////////////////////////
    // persistence methods
    ///////////////////////////////////////////////////////////////    

    public void save(RequestContext context)
    {
        context.getModel().saveObject(context, this);
    }

    // TODO
    public void reload(RequestContext context)
    {
    }

    public void remove(RequestContext context)
    {
        context.getModel().removeObject(context, this);
    }
    
    public boolean isSaved()
    {
        return this.isSaved;
    }
    
    
    ///////////////////////////////////////////////////////////////
    // xml methods
    ///////////////////////////////////////////////////////////////    

    public Document getDocument()
    {
        return this.document;
    }

    public String toXML()
    {
        return XMLUtil.toXML(document, true);
    }
    
    
    ///////////////////////////////////////////////////////////////
    // generic property accessors
    ///////////////////////////////////////////////////////////////

    public boolean getBooleanProperty(String propertyName)
    {
        String val = getProperty(propertyName);
        if (val == null)
            return false;
        return ("true".equals(val));
    }

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
     * @param propertyName
     * @return
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
     * @param propertyName
     * @return
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

    public String getModelProperty(String propertyName)
    {
        if (propertyName == null)
            return null;
        
        // do the get
        return (String) getDocument().getRootElement().elementTextTrim(
                propertyName);
    }

    public void setModelProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
            return;
        
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
    }

    public void removeModelProperty(String propertyName)
    {
        if (propertyName == null)
            return;

        // do the remove
        Element el = getDocument().getRootElement().element(propertyName);
        if (el != null)
        {
            getDocument().getRootElement().remove(el);
        }
    }

    
    
    
    ////////////////////////////////////////////////////////////
    // Custom Properties
    ////////////////////////////////////////////////////////////
    
    public String getCustomProperty(String propertyName)
    {
        if (propertyName == null)
            return null;
        
        // do the get
        Element properties = getDocument().getRootElement().element(CONTAINER_PROPERTIES);
        if(properties != null)
        {
            return (String) properties.elementTextTrim(propertyName);
        }
        
        return null;
    }

    public void setCustomProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
            return;
        
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
    }

    public void removeCustomProperty(String propertyName)
    {
        if (propertyName == null)
            return;
        
        // do the remove
        Element properties = getDocument().getRootElement().element("properties");
        if(properties != null)
        {
            Element el = properties.element(propertyName);
            if (el != null)
                properties.remove(el);
        }
    }

    
    
    public Map<String, Object> getProperties()
    {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(getModelProperties());
        properties.putAll(getCustomProperties());
        return properties;        
    }
    
    public Map<String, Object> getModelProperties()
    {
        Map<String, Object> map = new HashMap<String, Object>();

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
                    map.put(elementName, elementValue);
                }
            }
        }
        return map;
    }

    public Map<String, Object> getCustomProperties()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        
        Element properties = getDocument().getRootElement().element(CONTAINER_PROPERTIES);
        if(properties != null)
        {
            List elements = properties.elements();
            for (int i = 0; i < elements.size(); i++)
            {
                Element el = (Element) elements.get(i);
                String elementName = el.getName();
                String elementValue = el.getTextTrim();
                map.put(elementName, elementValue);
            }
        }
        return map;
    }
    


    public long getModificationTime()
    {
        return this.modificationTime;
    }

    public void setModificationTime(long modificationTime)
    {
        this.modificationTime = modificationTime;
    }

    public void touch()
    {
        setModificationTime(System.currentTimeMillis());
    }
    
    public String getRelativePath()
    {
        return relativePath;
    }
    
    public String getFileName()
    {
        return this.fileName;
    }
    
    public String getRelativeFilePath()
    {
        return getRelativePath() + "/" + getFileName();
    }

    public abstract String getTypeName();
    
    
    
    
    
    ////////////////////////////////////////////////////////
    //
    // TODO:  These should be refactored
    //
    ////////////////////////////////////////////////////////

    protected void setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
    }
    
    protected void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    protected void setSaved(boolean b)
    {
        this.isSaved = b;
    }
    

    
    
    
    
    
    
    
    
    
    
    
    //TODO
    /////////////////////////////////////////////////////////////////
    // backward compatibility for dynamic website project
    // this will be removed shortly
    /////////////////////////////////////////////////////////////////


    public String getSetting(String settingName)
    {
        if (settingName == null)
            return null;
        
        return getCustomProperty(settingName);
    }

    public void setSetting(String settingName, String settingValue)
    {
        setCustomProperty(settingName, settingValue);
    }

    public void removeSetting(String settingName)
    {
        removeCustomProperty(settingName);
    }
    public Map getSettings()
    {
        return getCustomProperties();
    }
    
    public String getName()
    {
        return getTitle();
    }

    public void setName(String value)
    {
        setTitle(value);
    }
    

}
