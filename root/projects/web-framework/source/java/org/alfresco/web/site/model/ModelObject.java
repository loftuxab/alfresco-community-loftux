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

import org.alfresco.web.site.RequestContext;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author muzquiano
 */
public abstract class ModelObject implements IModelObject
{
    public ModelObject(Document document)
    {
        this.document = document;
    }

    // common properties

    public String getId()
    {
        return getProperty("id");
    }

    public String getName()
    {
        return getProperty("name");
    }

    public void setName(String value)
    {
        setProperty("name", value);
    }

    public String getDescription()
    {
        return getProperty("description");
    }

    public void setDescription(String value)
    {
        setProperty("description", value);
    }

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

    public Document getDocument()
    {
        return this.document;
    }

    public String toXML()
    {
        return this.document.asXML();
    }

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

        return (String) getDocument().getRootElement().elementTextTrim(
                propertyName);
    }

    public void setProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
            return;

        Element el = getDocument().getRootElement().element(propertyName);
        if (el == null)
            el = getDocument().getRootElement().addElement("adw:" + propertyName);

        // put value
        el.setText(propertyValue);
    }

    public void removeProperty(String propertyName)
    {
        if (propertyName == null)
            return;

        Element el = getDocument().getRootElement().element(propertyName);
        if (el != null)
            getDocument().getRootElement().remove(el);
    }

    // support for name value pairs

    public String getSetting(String settingName)
    {
        if (settingName == null)
            return null;

        List elements = getDocument().getRootElement().elements("setting");
        for (int i = 0; i < elements.size(); i++)
        {
            Element el = (Element) elements.get(i);
            String _settingName = (String) el.elementTextTrim("name");
            if (_settingName.equals(settingName))
                return (String) el.elementTextTrim("value");
        }
        return null;
    }

    public void setSetting(String settingName, String settingValue)
    {
        removeSetting(settingName);

        // create a new setting
        Element el = getDocument().getRootElement().addElement("adw:setting");
        Element nameElement = el.addElement("adw:name");
        nameElement.setText(settingName);
        Element valueElement = el.addElement("adw:value");
        valueElement.setText(settingValue);
    }

    public void removeSetting(String settingName)
    {
        List elements = getDocument().getRootElement().elements("setting");
        for (int i = 0; i < elements.size(); i++)
        {
            Element el = (Element) elements.get(i);
            String _settingName = (String) el.elementTextTrim("name");
            if (_settingName.equals(settingName))
                getDocument().getRootElement().remove(el);
        }
    }

    public Map getSettings()
    {
        Map map = new HashMap();

        List elements = getDocument().getRootElement().elements("setting");
        for (int i = 0; i < elements.size(); i++)
        {
            Element el = (Element) elements.get(i);
            String settingName = el.elementTextTrim("name");
            String settingValue = el.elementTextTrim("value");
            map.put(settingName, settingValue);
        }
        return map;
    }

    public Map getProperties()
    {
        Map map = new HashMap();

        List elements = getDocument().getRootElement().elements();
        for (int i = 0; i < elements.size(); i++)
        {
            Element el = (Element) elements.get(i);
            String elementName = el.getName();
            if (elementName != null && elementName.equalsIgnoreCase("setting"))
            {
                String propertyValue = el.getStringValue();
                map.put(elementName, propertyValue);
            }
        }
        return map;
    }

    protected Document document;
    long modificationTime;

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

    //

    protected String relativePath;

    public String getRelativePath()
    {
        return relativePath;
    }

    public void setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
    }

    public String getFileName()
    {
        return this.getId() + ".xml";
    }

    public String getRelativeFilePath()
    {
        return getRelativePath() + "/" + getFileName();
    }
    
    public abstract String getTypeName();

}
