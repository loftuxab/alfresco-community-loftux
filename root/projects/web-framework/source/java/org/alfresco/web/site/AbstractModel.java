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

import org.alfresco.tools.ObjectGUID;
import org.alfresco.tools.ReflectionHelper;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.ModelObject;
import org.dom4j.Document;

/**
 * @author muzquiano
 */
public abstract class AbstractModel
{
    protected IFileSystem fileSystem;

    public AbstractModel(IFileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
    }

    public IFileSystem getFileSystem()
    {
        return this.fileSystem;
    }

    // guids

    public String newGUID()
    {
        ObjectGUID guid = new ObjectGUID();
        return guid.toString();
    }

    public String newGUID(String typeName)
    {
        // TODO: Is this necessary?
        int i = typeName.indexOf(":");
        if (i > -1)
            typeName = typeName.substring(i + 1, typeName.length());

        String prefix = this.getConfiguration().getModelTypePrefix(typeName);
        if (prefix != null && !"".equals(prefix))
            return prefix + newGUID();
        return newGUID();
    }

    // configuration

    public AbstractConfig getConfiguration()
    {
        return Framework.getConfig();
    }

    // helper methods
    protected ModelObject convertDocumentToModelObject(Document document)
    {
        if (document == null)
            return null;

        String tagName = document.getRootElement().getName();
        int i = tagName.indexOf(":");
        if (i > -1)
            tagName = tagName.substring(i + 1, tagName.length());

        String implClassName = this.getConfiguration().getModelTypeClass(
                tagName);
        ModelObject siteObject = (ModelObject) ReflectionHelper.newObject(
                implClassName, new Class[] { Document.class },
                new Object[] { document });
        return siteObject;
    }

    protected String convertIDToRelativeFilePath(String id)
    {
        String prefix = id.substring(0, 3);

        // TODO: Improve how this is done (use hashtable)
        String[] ids = getConfiguration().getModelTypeIds();
        for (int i = 0; i < ids.length; i++)
        {
            String modelTypeId = (String) ids[i];

            String modelPrefix = getConfiguration().getModelTypePrefix(
                    modelTypeId);
            if (modelPrefix != null && modelPrefix.equals(prefix))
            {
                // match
                String relativePath = getConfiguration().getModelTypePath(
                        modelTypeId);
                return relativePath + "/" + id + ".xml";
            }
        }
        return null;
    }

    protected String convertToID(IFile file)
    {
        String name = file.getName();
        if (name.endsWith(".xml"))
            name = name.substring(0, name.length() - 4);
        return name;
    }

    protected String convertToRelativeFilePath(IFile file)
    {
        String id = convertToID(file);
        String path = convertIDToRelativeFilePath(id);
        return path;
    }

    protected ModelObject convertDocumentToModelObject(Document document,
            long modificationTime)
    {
        ModelObject obj = convertDocumentToModelObject(document);
        obj.setModificationTime(modificationTime);
        return obj;
    }

    protected String getRelativePath(ModelObject object)
    {
        return object.getRelativePath();
    }

    protected String getRelativeFilePath(ModelObject object)
    {
        return object.getRelativeFilePath();
    }

}
