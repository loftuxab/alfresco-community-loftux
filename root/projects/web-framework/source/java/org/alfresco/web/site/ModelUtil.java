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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.tools.XMLUtil;
import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.TemplateInstance;
import org.alfresco.web.site.model.TemplateType;
import org.dom4j.Document;

/**
 * @author muzquiano
 */
public class ModelUtil
{
    //////////////////////////
    // configurations
    public static Configuration[] findConfigurations(RequestContext context)
    {
        return findConfigurations(context, null);
    }

    public static Configuration[] findConfigurations(RequestContext context,
            String sourceId)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap,
                Configuration.PROP_SOURCE_ID, sourceId);

        // do the lookup
        ModelObject[] objects = findObjects(context, Configuration.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        Configuration[] array = new Configuration[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (Configuration) objects[i];
        }
        return array;
    }

    //////////////////////////
    // page associations
    public static PageAssociation[] findPageAssociations(RequestContext context)
    {
        return findPageAssociations(context, null, null, null);
    }

    public static PageAssociation[] findPageAssociations(
            RequestContext context, String sourceId, String destId,
            String associationType)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_SOURCE_ID, sourceId);
        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_DEST_ID, destId);
        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_ASSOC_TYPE, associationType);

        // do the lookup
        ModelObject[] objects = findObjects(context, PageAssociation.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        PageAssociation[] array = new PageAssociation[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (PageAssociation) objects[i];
        }
        return array;
    }

    public static void associatePage(RequestContext context, String sourceId,
            String destId)
    {
        associatePage(context, sourceId, destId, "child");
    }

    public static void associatePage(RequestContext context, String sourceId,
            String destId, String associationType)
    {
        // first call unassociate just to be safe
        unassociatePage(context, sourceId, destId, associationType);

        // create a new template association
        PageAssociation pageAssociation = context.getModel().newPageAssociation(
                context);
        pageAssociation.setSourceId(sourceId);
        pageAssociation.setDestId(destId);
        pageAssociation.setAssociationType(associationType);

        // save the object
        context.getModel().saveObject(context, pageAssociation);
    }

    public static void unassociatePage(RequestContext context, String sourceId,
            String destId)
    {
        unassociatePage(context, sourceId, destId, "child");
    }

    public static void unassociatePage(RequestContext context, String sourceId,
            String destId, String associationTypeId)
    {
        PageAssociation[] pageAssociations = findPageAssociations(context,
                sourceId, destId, associationTypeId);
        for (int i = 0; i < pageAssociations.length; i++)
            unassociatePage(context, pageAssociations[i].getId());
    }

    public static void unassociatePage(RequestContext context,
            String pageAssociationId)
    {
        PageAssociation pageAssociation = context.getModel().loadPageAssociation(
                context, pageAssociationId);
        context.getModel().removeObject(context, pageAssociation);
    }

    // object associations
    public static ContentAssociation[] findContentAssociations(
            RequestContext context)
    {
        return findContentAssociations(context, null, null, null, null);
    }

    public static ContentAssociation[] findContentAssociations(
            RequestContext context, String sourceId, String destId,
            String assocType, String formatId)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_SOURCE_ID, sourceId);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_DEST_ID, destId);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_ASSOC_TYPE, assocType);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_FORMAT_ID, formatId);

        // do the lookup
        ModelObject[] objects = findObjects(context,
                ContentAssociation.TYPE_NAME, propertyConstraintMap);

        // convert to return type
        ContentAssociation[] array = new ContentAssociation[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (ContentAssociation) objects[i];
        }
        return array;
    }

    public static void associateContent(RequestContext context,
            String sourceId, String destId, String assocType, String formatId)
    {
        // first call unassociate just to be safe
        unassociateContent(context, sourceId, destId, assocType, formatId);

        // create a new association
        ContentAssociation association = context.getModel().newContentAssociation(
                context);
        association.setSourceId(sourceId);
        association.setDestId(destId);
        association.setAssociationType(assocType);
        association.setFormatId(formatId);

        // save the object
        context.getModel().saveObject(context, association);
    }

    public static void unassociateContent(RequestContext context,
            String sourceId, String destId, String assocType, String formatId)
    {
        ContentAssociation[] associations = findContentAssociations(context,
                sourceId, destId, assocType, formatId);
        for (int i = 0; i < associations.length; i++)
            unassociateContent(context, associations[i].getId());
    }

    public static void unassociateContent(RequestContext context,
            String objectAssociationId)
    {
        ContentAssociation association = context.getModel().loadContentAssociation(
                context, objectAssociationId);
        context.getModel().removeObject(context, association);

    }

    // components
    public static Component[] findComponents(RequestContext context)
    {
        return findComponents(context, null, null, null, null);
    }

    public static Component[] findComponents(RequestContext context,
            String scope, String sourceId, String regionId,
            String componentTypeId)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap, Component.PROP_SCOPE,
                scope);
        addPropertyConstraint(propertyConstraintMap, Component.PROP_SOURCE_ID,
                sourceId);
        addPropertyConstraint(propertyConstraintMap, Component.PROP_REGION_ID,
                regionId);
        addPropertyConstraint(propertyConstraintMap,
                Component.PROP_COMPONENT_TYPE_ID, componentTypeId);

        // do the lookup
        ModelObject[] objects = findObjects(context, Component.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        Component[] array = new Component[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (Component) objects[i];
        }
        return array;
    }

    public static void associateComponent(RequestContext context,
            String componentId, String scope, String sourceId, String regionId)
    {
        // first unassociate any existing components with these bindings
        Component[] array = findComponents(context, scope, sourceId, regionId,
                null);
        if (array != null && array.length > 0)
        {
            for (int i = 0; i < array.length; i++)
            {
                Component a = (Component) array[i];
                unassociateComponent(context, a.getId());
            }
        }

        // get the component
        Component component = context.getModel().loadComponent(context,
                componentId);

        // bind it
        component.setScope(scope);
        component.setSourceId(sourceId);
        component.setRegionId(regionId);

        // save the object
        context.getModel().saveObject(context, component);
    }

    public static void unassociateComponent(RequestContext context,
            String componentId)
    {
        Component component = context.getModel().loadComponent(context,
                componentId);
        component.setScope("");
        component.setSourceId("");
        component.setRegionId("");
        context.getModel().saveObject(context, component);
    }

    // helpers (for non associations)
    public static TemplateInstance[] findTemplates(RequestContext context)
    {
        return findTemplates(context, null);
    }

    public static TemplateInstance[] findTemplates(RequestContext context,
            String templateType)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap,
                TemplateInstance.PROP_TEMPLATE_TYPE, templateType);

        // do the lookup
        ModelObject[] objects = findObjects(context, TemplateInstance.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        TemplateInstance[] array = new TemplateInstance[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (TemplateInstance) objects[i];
        }
        return array;
    }

    public static TemplateType[] findTemplateTypes(RequestContext context)
    {
        return findTemplateTypes(context, null);
    }

    public static TemplateType[] findTemplateTypes(RequestContext context,
            String uri)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap, TemplateType.PROP_URI, uri);

        // do the lookup
        ModelObject[] objects = findObjects(context, TemplateType.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        TemplateType[] array = new TemplateType[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (TemplateType) objects[i];
        }
        return array;
    }

    public static Component[] findComponents(RequestContext context,
            String componentTypeId)
    {
        return findComponents(context, null, null, null, componentTypeId);
    }

    public static ComponentType[] findComponentTypes(RequestContext context)
    {
        return findComponentTypes(context, null);
    }

    public static ComponentType[] findComponentTypes(RequestContext context,
            String uri)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap, ComponentType.PROP_URI,
                uri);

        // do the lookup
        ModelObject[] objects = findObjects(context, ComponentType.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        ComponentType[] array = new ComponentType[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (ComponentType) objects[i];
        }
        return array;
    }

    public static Page[] findPages(RequestContext context)
    {
        return findPages(context, null, null);
    }

    public static Page[] findPages(RequestContext context, String templateId,
            String rootPage)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap, Page.PROP_TEMPLATE_INSTANCE,
                templateId);
        addPropertyConstraint(propertyConstraintMap, Page.PROP_ROOT_PAGE,
                rootPage);

        // do the lookup
        ModelObject[] objects = findObjects(context, Page.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        Page[] array = new Page[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (Page) objects[i];
        }
        return array;
    }

    public static Endpoint[] findEndpoints(RequestContext context)
    {
        return findEndpoints(context, null);
    }

    public static Endpoint[] findEndpoints(RequestContext context,
            String endpointId)
    {
        // build property map
        HashMap propertyConstraintMap = new HashMap();
        addPropertyConstraint(propertyConstraintMap, Endpoint.PROP_ENDPOINT_ID,
                endpointId);

        // do the lookup
        ModelObject[] objects = findObjects(context, Endpoint.TYPE_NAME,
                propertyConstraintMap);

        // convert to return type
        Endpoint[] array = new Endpoint[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            array[i] = (Endpoint) objects[i];
        }
        return array;
    }

    public static Endpoint getEndpoint(RequestContext context, String endpointId)
    {
        Endpoint endpoint = null;
        Endpoint[] endpoints = findEndpoints(context, endpointId);
        if (endpoints != null && endpoints.length > 0)
            endpoint = endpoints[0];
        return endpoint;
    }

    public static void associateTemplate(RequestContext context,
            String templateId, String pageId)
    {
        associateTemplate(context, templateId, pageId, null);
    }

    public static void associateTemplate(RequestContext context,
            String templateId, String pageId, String formatId)
    {
        Page page = context.getModel().loadPage(context, pageId);
        page.setTemplateId(templateId, formatId);
        context.getModel().saveObject(context, page);
    }

    public static void unassociateTemplate(RequestContext context, String pageId)
    {
        unassociateTemplate(context, pageId, null);
    }

    public static void unassociateTemplate(RequestContext context,
            String pageId, String formatId)
    {
        Page page = context.getModel().loadPage(context, pageId);
        page.removeTemplateId(formatId);
        context.getModel().saveObject(context, page);
    }

    // extra

    public static Page getRootPage(RequestContext context)
    {
        // first check the site configuration
        Configuration siteConfiguration = context.getSiteConfiguration();
        if(siteConfiguration != null)
        {
            String rootPageId = siteConfiguration.getProperty("root-page");
            if(rootPageId != null)
            {
                Page page = context.getModel().loadPage(context, rootPageId);
                if(page != null)
                {
                    return page;
                }
            }
        }
        
        // Otherwise, do an exhaustive query for pages with the root-page property
        Page[] rootPages = ModelUtil.findPages(context, null, "true");
        if (rootPages != null && rootPages.length > 0)
        {
            return rootPages[0];
        }
        return null;
    }

    public static Configuration getSiteConfiguration(RequestContext context)
    {
        Configuration[] configurations = ModelUtil.findConfigurations(context,
                Configuration.VALUE_SOURCE_ID_SITE);
        if (configurations != null && configurations.length > 0)
        {
            return configurations[0];
        }
        return null;
    }

    // generic method

    protected static ModelObject[] findObjects(RequestContext context,
            String typeName, HashMap propertyConstraintMap)
    {
        ModelObject[] array = new ModelObject[] {};
        try
        {
            ModelObject[] objects = context.getModel().loadObjects(context,
                    typeName);
            if (objects != null && objects.length > 0)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < objects.length; i++)
                {
                    ModelObject object = (ModelObject) objects[i];
                    boolean success = true;

                    // walk the property map and make sure all matches are satisfied
                    if (propertyConstraintMap != null)
                    {
                        Iterator it = propertyConstraintMap.keySet().iterator();
                        while (it.hasNext())
                        {
                            String propertyName = (String) it.next();
                            Object propertyValue = propertyConstraintMap.get(propertyName);
                            if (propertyValue != null)
                            {
                                // constraints
                                if (propertyValue instanceof String)
                                {
                                    String currentValue = (String) object.getProperty(propertyName);
                                    if (!propertyValue.equals(currentValue))
                                    {
                                        success = false;
                                    }
                                }
                                if (propertyValue instanceof Boolean)
                                {
                                    boolean currentValue = object.getBooleanProperty(propertyName);
                                    if (currentValue != ((Boolean) propertyValue).booleanValue())
                                    {
                                        success = false;
                                    }
                                }
                            }
                        }
                    }

                    if (success)
                        arrayList.add(object);
                }

                array = new ModelObject[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (ModelObject) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return array;
    }

    public static void addPropertyConstraint(Map propertyConstraintMap,
            String propertyName, Object propertyValue)
    {
        if (propertyValue != null)
        {
            propertyConstraintMap.put(propertyName, propertyValue);
        }
    }

    // helpers

    public static String getFileStringContents(RequestContext context,
            String relativeFilePath)
    {
        IFile file = context.getFileSystem().getFile(relativeFilePath);
        if (file != null)
        {
            return file.readContents();
        }
        return null;
    }

    public static void writeDocument(RequestContext context,
            String relativePath, String name, Document xmlDocument)
    {
        writeDocument(context.getFileSystem(), relativePath, name, xmlDocument);
    }

    public static void writeDocument(IFileSystem fileSystem,
            String relativePath, String name, Document xmlDocument)
    {
        // convert to xml       
        String xml = XMLUtil.toXML(xmlDocument, true);

        // relative file path
        String relativeFilePath = relativePath + "/" + name;

        // check to see if a file already exists
        IFile file = fileSystem.getFile(relativeFilePath);
        if (file == null)
        {
            // no existing file, so create it
            file = fileSystem.createFile(relativeFilePath);
        }
        if (file != null)
        {
            file.writeBytes(xml.getBytes());
        }
    }

    public static Document readDocument(RequestContext context,
            String relativeFilePath)
    {
        return readDocument(context.getFileSystem(), relativeFilePath);
    }

    public static Document readDocument(IFileSystem fileSystem,
            String relativeFilePath)
    {
        IFile file = fileSystem.getFile(relativeFilePath);
        if (file == null)
            return null;
        return readDocument(file);
    }

    public static Document readDocument(IFile file)
    {
        Document doc = null;
        if(file.isFile())
        {
            try
            {
                doc = XMLUtil.parse(file.getInputStream());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return doc;
    }

}
