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
package org.alfresco.web.site;

import java.util.HashMap;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.cache.CacheFactory;
import org.alfresco.web.site.cache.IContentCache;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.model.ModelObject;
import org.dom4j.Document;

/**
 * @author muzquiano
 */
public class DefaultModelManager extends AbstractModelManager
{
    // Internal Cache for Objects
    public HashMap cacheMap = null;

    public void cacheInvalidateAll(RequestContext context)
    {
        if (cacheMap != null)
            cacheMap.clear();
    }

    public IContentCache getCache(RequestContext context)
    {
        if (cacheMap == null)
            cacheMap = new HashMap();

        String cacheMapKey = context.getStoreId();
        IContentCache cache = (IContentCache) cacheMap.get(cacheMapKey);
        if (cache == null)
        {
            long timeout = 30 * 60 * 1000; // 30 minutes
            cache = CacheFactory.createADSCache(context.getFileSystem(),
                    timeout);
            cacheMap.put(cacheMapKey, cache);
        }
        return cache;
    }

    public void cachePut(RequestContext context, ModelObject obj)
    {
        String cacheKey = obj.getRelativeFilePath();
        getCache(context).put(cacheKey, obj);
    }

    public void cacheRemove(RequestContext context, ModelObject obj)
    {
        String cacheKey = obj.getRelativeFilePath();
        getCache(context).remove(cacheKey);
    }

    public ModelObject cacheGetByPath(RequestContext context,
            String relativePath)
    {
        ModelObject obj = (ModelObject) getCache(context).get(relativePath);
        return obj;
    }

    public ModelObject cacheGetById(RequestContext context, String id)
    {
        String relativePath = convertIDToRelativeFilePath(id);
        return cacheGetByPath(context, relativePath);
    }

    public void saveObject(RequestContext context, ModelObject obj)
    {
        Document xmlDocument = obj.getDocument();
        if (xmlDocument != null)
        {
            String relativePath = obj.getRelativePath();
            String fileName = obj.getFileName();

            context.getModelManager().putDocumentXML(context, relativePath,
                    fileName, xmlDocument);

            // make sure that the cache is in sync
            obj.touch();
            cachePut(context, obj);
        }
    }

    public ModelObject loadObject(RequestContext context, String id)
    {
        if (id == null)
            return null;

        // check the cache to see if we already have it
        ModelObject obj = cacheGetById(context, id);
        if (obj != null)
        {
            return obj;
        }

        try
        {
            String relativeFilePath = convertIDToRelativeFilePath(id);
            IFile file = getFile(context, relativeFilePath);
            if (file != null)
            {
                Document document = getDocumentXML(context, file);
                obj = convertDocumentToObject(document,
                        file.getModificationDate());

                // get the relative path for this type
                int u = relativeFilePath.lastIndexOf("/");
                String relativePath = relativeFilePath.substring(0, u);
                obj.setRelativePath(relativePath);

                // make sure that the cache is in sync
                cachePut(context, obj);
            }
            else
            {
                // we didn't find the file, we'll return null
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return (ModelObject) obj;
    }

    public ModelObject loadObject(RequestContext context, IFile file)
    {
        // get the ads object id for this node descriptor
        String id = convertToID(file);
        return loadObject(context, id);
    }

    public void removeObject(RequestContext context, ModelObject obj)
    {
        System.out.println("REMOVE OBJECT HIT!");
        Document xmlDocument = obj.getDocument();
        if (xmlDocument != null)
        {
            System.out.println("aaa");
            String relativePath = obj.getRelativePath();
            String fileName = obj.getFileName();

            System.out.println("rel1: " + relativePath);
            System.out.println("rel2: " + fileName);

            removeFile(context, relativePath, fileName);

            // make sure that the cache is in sync
            cacheRemove(context, obj);
        }
    }

    public ModelObject newObject(RequestContext context, String typeName)
    {
        String namespaces = "xmlns:adw='http://www.alfresco.org/adw/1.0' xmlns:alf='http://www.alfresco.org' xmlns:chiba='http://chiba.sourceforge.net/xforms' xmlns:ev='http://www.w3.org/2001/xml-events' xmlns:xf='http://www.w3.org/2002/xforms' xmlns:xhtml='http://www.w3.org/1999/xhtml' xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";
        String xml = "<adw:" + typeName + " " + namespaces + ">";
        xml += "</adw:" + typeName + ">";

        String id = newGUID(typeName);

        ModelObject obj = null;
        try
        {
            Document d = XMLUtil.parse(xml);
            XMLUtil.addChildValue(d.getRootElement(), "adw:id", id);
            XMLUtil.addChildValue(d.getRootElement(), "adw:name", id);
            XMLUtil.addChildValue(d.getRootElement(), "adw:description", id);

            obj = (ModelObject) convertDocumentToObject(d,
                    System.currentTimeMillis());

            // get the relative path for this type
            String relativePath = getConfiguration().getModelTypePath(typeName);
            obj.setRelativePath(relativePath);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return obj;
    }
}
