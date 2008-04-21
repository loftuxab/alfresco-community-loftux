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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.tools.ObjectGUID;
import org.alfresco.tools.ReflectionHelper;
import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.Framework;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.dom4j.Document;

/**
 * Static Utility class that provides reflection against the public
 * properties of a given object.
 * 
 * This class provides methods for checking whether a given property name
 * is declared as a model variable.
 * 
 * Model variables are defined on ModelObject derived types as variables
 * that begin with a "PROP_" prefix.
 * 
 * The code walks the class chain and picks out which variables are model
 * specific and which ones are custom.  The results are stored in look-up
 * tables so that subsequent lookups will simply hit the cache.  This is
 * perfectly fine since model definitions do not change at runtime.
 * 
 * @author muzquiano
 */
public class ModelHelper
{
    protected static Map<String, Map> classMap;
    
    protected static String MODEL_PROPERTY = "model";
    
    public static boolean isCustomProperty(ModelObject object, String propertyName)
    {
        return !(isModelProperty(object, propertyName));
    }

    public static boolean isModelProperty(ModelObject object, String propertyName)
    {
        if(object == null || propertyName == null)
        {
            return false;
        }
        
        Class modelClass = object.getClass();
        
        // our quick lookup cache
        if(classMap == null)
        {
            classMap = new HashMap<String, Map>();
        }
        
        // grab the cache of property keys
        Map<String, String> propertyMap = (Map) classMap.get(modelClass.getName());
        if(propertyMap == null)
        {
            // we need to build the property map cache
            propertyMap = new HashMap<String, String>();
            classMap.put(modelClass.getName(), propertyMap);

            // reflect on the class
            try
            {
                Class klass = modelClass;
                do
                {
                    Field[] fields = klass.getFields();
                    for(int i = 0; i < fields.length; i++)
                    {
                        // is it a declared property?
                        if(fields[i].getName().startsWith("PROP_"))
                        {
                            String fieldValue = (String) fields[i].get(object);
                            
                            // mark it
                            propertyMap.put(fieldValue, MODEL_PROPERTY);
                        }
                    }
                    
                    klass = klass.getSuperclass();
                }
                while(klass != null);
            }
            catch(IllegalAccessException iae)
            {
                iae.printStackTrace();
            }
        }
        
        // look up property in property map cache
        if(propertyMap != null)
        {
            // glean what kind of property it is from the cache
            String gleaned = (String) propertyMap.get(propertyName);
            if(MODEL_PROPERTY.equals(gleaned))
            {
                return true;
            }
            else
            {
                // either it is custom or it didn't exist
            }
        }
        
        return false;
    }
    
    public static ModelObject loadObject(IFileSystem fileSystem, String modelRelativeFilePath)
    {
        ModelObject obj = null;
        
        // Read the document from the model's file system
        IFile file = fileSystem.getFile(modelRelativeFilePath);
        if (file != null)
        {
            Document document = ModelUtil.readDocument(file);
            obj = convertDocumentToModelObject(document,
                    file.getModificationDate());
            
            // determine the type of this object
            String typeName = obj.getTypeName();
            
            // look up the relativePath for this bad boy
            String relativePath = Framework.getConfig().getModelTypePath(typeName);
            
            // splice this off the front and that's our filename
            String fileName = modelRelativeFilePath.substring(relativePath.length() + 1, modelRelativeFilePath.length());
            
            // get the relative path for this type
            /*
            int u = modelRelativeFilePath.lastIndexOf("/");
            String relativePath = modelRelativeFilePath.substring(0, u);
            String fileName = modelRelativeFilePath.substring(u+1, modelRelativeFilePath.length());
            */

            // set onto object
            obj.setRelativePath(relativePath);
            obj.setFileName(fileName);
        }
        
        return obj;
    }
    
    public static ModelObject newObject(String typeName)
    {
        // construct the xml
        String xml = "<" + typeName + "></" + typeName + ">";

        // constructs a new GUID (with prefix if available)
        String id = newGUID(typeName);

        // build the object
        ModelObject obj = null;
        try
        {
            Document d = XMLUtil.parse(xml);
            XMLUtil.addChildValue(d.getRootElement(), "id", id);
            XMLUtil.addChildValue(d.getRootElement(), "name", id);
            XMLUtil.addChildValue(d.getRootElement(), "description", id);

            obj = (ModelObject) convertDocumentToModelObject(d,
                    System.currentTimeMillis());

            // get the relative path for this type
            String modelRelativePath = Framework.getConfig().getModelTypePath(
                    typeName);
            obj.setRelativePath(modelRelativePath);
            obj.setFileName(id + ".xml");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return obj;        
    }
    
    public static boolean saveObject(IFileSystem fileSystem, ModelObject obj)
    {
        Document xmlDocument = obj.getDocument();
        if (xmlDocument != null)
        {
            String modelRelativePath = obj.getRelativePath();
            String modelFileName = obj.getFileName();
            
            // write the document to the model's file system
            ModelUtil.writeDocument(fileSystem, modelRelativePath,
                    modelFileName, xmlDocument);
            
            return true;
        }
        
        return false;
    }
    
    public static boolean removeObject(IFileSystem fileSystem, ModelObject obj)
    {
        Document xmlDocument = obj.getDocument();
        if (xmlDocument != null)
        {
            String modelRelativePath = obj.getRelativePath();
            String modelFileName = obj.getFileName();

            // delete the file from the model's file system
            fileSystem.deleteFile(modelRelativePath, modelFileName);
            
            return true;
        }
        
        return false;
    }
    
    public static String newGUID()
    {
        ObjectGUID guid = new ObjectGUID();
        return guid.toString();
    }

    public static String newGUID(String typeName)
    {
        // TODO: Is this necessary?
        int i = typeName.indexOf(":");
        if (i > -1)
        {
            typeName = typeName.substring(i + 1, typeName.length());
        }

        String prefix = Framework.getConfig().getModelTypePrefix(typeName);
        if (prefix != null && prefix.length() != 0)
        {
            return prefix + newGUID();
        }
        return newGUID();
    }
    
    
    protected static ModelObject convertDocumentToModelObject(Document document)
    {
        if (document == null)
            return null;

        String tagName = document.getRootElement().getName();
        int i = tagName.indexOf(":");
        if (i > -1)
            tagName = tagName.substring(i + 1, tagName.length());

        String implClassName = Framework.getConfig().getModelTypeClass(
                tagName);
        ModelObject siteObject = (ModelObject) ReflectionHelper.newObject(
                implClassName, new Class[] { Document.class },
                new Object[] { document });
        return siteObject;
    }

    protected static ModelObject convertDocumentToModelObject(Document document,
            long modificationTime)
    {
        ModelObject obj = convertDocumentToModelObject(document);
        obj.setModificationTime(modificationTime);
        return obj;
    }
    
    
}
