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

import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.ModelHelper;
import org.alfresco.web.site.model.ModelObject;

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

    // configuration

    public String newGUID()
    {
        return ModelHelper.newGUID();
    }

    public String newGUID(String typeName)
    {
        return ModelHelper.newGUID(typeName);
    }
    
    public FrameworkConfig getConfiguration()
    {
        return Framework.getConfig();
    }

    // helper methods
    
    /**
     * This is an exhaustive method to find a model object type with a
     * given id across the given model type paths.
     * 
     * @param id
     * @return
     */
    protected String convertIDToRelativeFilePath(String id)
    {
        String[] modelTypeIds = getConfiguration().getModelTypeIds();
        for(int i = 0; i < modelTypeIds.length; i++)
        {
            String relativeFilePath = convertIDToRelativeFilePath(modelTypeIds[i], id);
            IFile modelFile = getFileSystem().getFile(relativeFilePath);
            if(modelFile != null)
            {
                return relativeFilePath;
            }            
        }
        return null;
    }
    
    protected String convertIDToRelativeFilePath(String typeId, String id)
    {
        String modelTypePath = getConfiguration().getModelTypePath(typeId);
        if(modelTypePath != null)
        {
            return modelTypePath + "/" + id + ".xml";
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

    protected String getRelativePath(ModelObject object)
    {
        return object.getRelativePath();
    }

    protected String getRelativeFilePath(ModelObject object)
    {
        return object.getRelativeFilePath();
    }

}
