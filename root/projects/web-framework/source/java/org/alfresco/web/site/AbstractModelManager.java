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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.tools.DataUtil;
import org.alfresco.tools.ObjectGUID;
import org.alfresco.tools.ReflectionHelper;
import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.Template;
import org.alfresco.web.site.model.TemplateType;
import org.dom4j.Document;

/**
 * @author muzquiano
 */
public abstract class AbstractModelManager
{
    public abstract void cacheInvalidateAll(RequestContext context);

    public abstract void saveObject(RequestContext context, ModelObject obj);

    public abstract ModelObject loadObject(RequestContext context, String id);

    public abstract ModelObject loadObject(RequestContext context, IFile file);

    public abstract void removeObject(RequestContext context, ModelObject obj);

    public abstract ModelObject newObject(RequestContext context, String tagName);

    // common methods
    public ModelObject convertDocumentToObject(Document document)
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

    public String convertIDToRelativeFilePath(String id)
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

    public String convertToID(IFile file)
    {
        String name = file.getName();
        if (name.endsWith(".xml"))
            name = name.substring(0, name.length() - 4);
        return name;
    }

    public String convertToRelativeFilePath(IFile file)
    {
        String id = convertToID(file);
        String path = convertIDToRelativeFilePath(id);
        return path;
    }

    public ModelObject convertDocumentToObject(Document document,
            long modificationTime)
    {
        ModelObject obj = convertDocumentToObject(document);
        obj.setModificationTime(modificationTime);
        return obj;
    }

    // GUID stuff

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

    //
    // The following Helper Functions are basically convenience methods
    // to the methods listed above
    //
    public Component loadComponent(RequestContext context, String id)
    {
        return (Component) loadObject(context, id);
    }

    public ComponentType loadComponentType(RequestContext context, String id)
    {
        return (ComponentType) loadObject(context, id);
    }

    public Configuration loadConfiguration(RequestContext context, String id)
    {
        return (Configuration) loadObject(context, id);
    }

    public ContentAssociation loadContentAssociation(RequestContext context,
            String id)
    {
        return (ContentAssociation) loadObject(context, id);
    }

    public Endpoint loadEndpoint(RequestContext context, String id)
    {
        return (Endpoint) loadObject(context, id);
    }

    public Page loadPage(RequestContext context, String id)
    {
        return (Page) loadObject(context, id);
    }

    public PageAssociation loadPageAssociation(RequestContext context, String id)
    {
        return (PageAssociation) loadObject(context, id);
    }

    public Template loadTemplate(RequestContext context, String id)
    {
        return (Template) loadObject(context, id);
    }

    public TemplateType loadTemplateType(RequestContext context, String id)
    {
        return (TemplateType) loadObject(context, id);
    }

    /*
     * 
     * INSTANTIATOR HELPER FUNCTIONS
     * 
     */

    public Component newComponent(RequestContext context)
    {
        return (Component) newObject(context, "component");
    }

    public ComponentType newComponentType(RequestContext context)
    {
        return (ComponentType) newObject(context, "component-type");
    }

    public Configuration newConfiguration(RequestContext context)
    {
        return (Configuration) newObject(context, "configuration");
    }

    public ContentAssociation newContentAssociation(RequestContext context)
    {
        return (ContentAssociation) newObject(context, "content-association");
    }

    public Endpoint newEndpoint(RequestContext context)
    {
        return (Endpoint) newObject(context, "endpoint");
    }

    public Page newPage(RequestContext context)
    {
        return (Page) newObject(context, "page");
    }

    public PageAssociation newPageAssociation(RequestContext context)
    {
        return (PageAssociation) newObject(context, "page-association");
    }

    public Template newTemplate(RequestContext context)
    {
        return (Template) newObject(context, "template");
    }

    public TemplateType newTemplateType(RequestContext context)
    {
        return (TemplateType) newObject(context, "template-type");
    }

    /////////////////////////////////////////////////////////////////////////
    //
    // File System Integration Functions
    //
    // These Functions work with the File System abtraction layer
    // and provide interaction with XML persisted data in either the local
    // disk (in the the case of a dedicated preview or runtime server)
    // or a virtualization server or alfresco scripting instance (in the
    // case of an avm remote accessible instance)
    //
    // All paths to these functions are relative to the mounted web
    // web application.
    //
    ////////////////////////////////////////////////////////////////////////

    public IFile getFile(RequestContext context, String relativePath)
    {
        return context.getFileSystem().getFile(relativePath);
    }

    public IFile[] getFiles(RequestContext context, String relativePath)
    {
        return context.getFileSystem().getFiles(relativePath);
    }

    public OutputStream getFileOutputStream(RequestContext context, IFile file)
            throws Exception
    {
        return context.getFileSystem().getOutputStream(file);
    }

    public InputStream getFileInputStream(RequestContext context, IFile file)
            throws Exception
    {
        return context.getFileSystem().getInputStream(file);
    }

    public IFile createFile(RequestContext context, String relativePath)
    {
        return context.getFileSystem().createFile(relativePath);
    }

    public void putDocumentXML(RequestContext context, String relativePath,
            String name, Document xmlDocument)
    {
        // convert to xml		
        String xml = XMLUtil.toXML(xmlDocument);

        // relative file path
        String relativeFilePath = relativePath + "/" + name;

        // check to see if a file already exists
        IFile existingFile = getFile(context, relativeFilePath);
        if (existingFile == null)
        {
            // no existing file, so create it
            existingFile = createFile(context, relativeFilePath);
        }
        if (existingFile != null)
        {
            // stream out to it
            try
            {
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        xml.getBytes());
                OutputStream os = getFileOutputStream(context, existingFile);
                DataUtil.copyStream(bais, os);
            }
            catch (Exception ex)
            {
                System.out.println("*** STREAM COPY FAILED");
                // TODO: How to handle this?
                ex.printStackTrace();
            }
        }
    }

    public Document getDocumentXML(RequestContext context,
            String relativeFilePath)
    {
        IFile file = getFile(context, relativeFilePath);
        if (file == null)
            return null;
        return getDocumentXML(context, file);
    }

    public Document getDocumentXML(RequestContext context, IFile file)
    {
        Document doc = null;
        try
        {
            doc = XMLUtil.parse(getFileInputStream(context, file));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return doc;
    }

    public void removeFile(RequestContext context, String relativePath,
            String name)
    {
        context.getFileSystem().deleteFile(relativePath, name);
    }

    public byte[] getDocumentBytes(RequestContext context, String relativePath)
    {
        IFile file = context.getFileSystem().getFile(relativePath);
        if (file == null)
            return null;

        byte[] array = null;
        try
        {
            InputStream is = getFileInputStream(context, file);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int data = 0;
            while ((data = is.read()) != -1)
            {
                baos.write(data);
            }
            baos.close();

            array = baos.toByteArray();
        }
        catch (Exception ex)
        {
        }
        return array;
    }

    public String getDocumentString(RequestContext context, String relativePath)
    {
        byte[] array = getDocumentBytes(context, relativePath);
        return new String(array);
    }

    public AbstractConfig getConfiguration()
    {
        return Framework.getConfig();
    }
}
