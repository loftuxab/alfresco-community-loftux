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
import java.util.List;

import org.alfresco.web.site.filesystem.IFile;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.ComponentType;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.Template;
import org.alfresco.web.site.model.TemplateType;

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
        Configuration[] array = new Configuration[] {};
        try
        {
            // load all of the component association objects
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "configuration");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    Configuration configuration = (Configuration) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _sourceId = configuration.getSourceId();

                    if (sourceId != null && !sourceId.equalsIgnoreCase(_sourceId))
                        okay = false;

                    if (okay)
                        arrayList.add(configuration);
                }

                array = new Configuration[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (Configuration) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        PageAssociation[] array = new PageAssociation[] {};
        try
        {
            // load all of the component association objects
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "page-association");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    PageAssociation pageAssociation = (PageAssociation) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _sourceId = pageAssociation.getSourceId();
                    String _destId = pageAssociation.getDestId();
                    String _associationType = pageAssociation.getAssociationType();
                    //				String _orderId = pageAssociation.getOrderId();

                    if (sourceId != null && !sourceId.equalsIgnoreCase(_sourceId))
                        okay = false;
                    if (destId != null && !destId.equalsIgnoreCase(_destId))
                        okay = false;
                    if (associationType != null && !associationType.equalsIgnoreCase(_associationType))
                        okay = false;
                    //				if(orderId != null && !orderId.equalsIgnoreCase(_orderId))
                    //					okay = false;
                    if (okay)
                        arrayList.add(pageAssociation);
                }

                array = new PageAssociation[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (PageAssociation) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        PageAssociation pageAssociation = context.getModelManager().newPageAssociation(
                context);
        pageAssociation.setSourceId(sourceId);
        pageAssociation.setDestId(destId);
        pageAssociation.setAssociationType(associationType);

        // save the object
        context.getModelManager().saveObject(context, pageAssociation);
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
        PageAssociation pageAssociation = context.getModelManager().loadPageAssociation(
                context, pageAssociationId);
        context.getModelManager().removeObject(context, pageAssociation);
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
        ContentAssociation[] array = new ContentAssociation[] {};
        try
        {
            // load all of the component association objects
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "content-association");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    ContentAssociation association = (ContentAssociation) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _sourceId = association.getSourceId();
                    String _destId = association.getDestId();
                    String _assocType = association.getAssociationType();
                    String _formatId = association.getFormatId();

                    if (sourceId != null && !sourceId.equalsIgnoreCase(_sourceId))
                        okay = false;
                    if (destId != null && !destId.equalsIgnoreCase(_destId))
                        okay = false;
                    if (assocType != null && !assocType.equalsIgnoreCase(_assocType))
                        okay = false;
                    if (formatId != null && !formatId.equalsIgnoreCase(_formatId))
                        okay = false;
                    if (okay)
                        arrayList.add(association);
                }

                array = new ContentAssociation[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (ContentAssociation) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return array;
    }

    public static void associateContent(RequestContext context,
            String sourceId, String destId, String assocType, String formatId)
    {
        // first call unassociate just to be safe
        unassociateContent(context, sourceId, destId, assocType, formatId);

        // create a new association
        ContentAssociation association = context.getModelManager().newContentAssociation(
                context);
        association.setSourceId(sourceId);
        association.setDestId(destId);
        association.setAssociationType(assocType);
        association.setFormatId(formatId);

        // save the object
        context.getModelManager().saveObject(context, association);
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
        ContentAssociation association = context.getModelManager().loadContentAssociation(
                context, objectAssociationId);
        context.getModelManager().removeObject(context, association);

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
        Component[] array = new Component[] {};
        try
        {
            // load all of the component association objects
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "component");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    Component component = (Component) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _scope = component.getScope();
                    String _sourceId = component.getSourceId();
                    String _regionId = component.getRegionId();
                    String _componentTypeId = component.getComponentTypeId();

                    if (scope != null && !scope.equalsIgnoreCase(_scope))
                        okay = false;
                    if (sourceId != null && !sourceId.equalsIgnoreCase(_sourceId))
                        okay = false;
                    if (regionId != null && !regionId.equalsIgnoreCase(_regionId))
                        okay = false;
                    if (componentTypeId != null && !componentTypeId.equalsIgnoreCase(_componentTypeId))
                        okay = false;
                    if (okay)
                        arrayList.add(component);
                }

                array = new Component[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (Component) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return array;
    }

    public static void associateComponent(RequestContext context,
            String componentId, String scope, String sourceId, String regionId,
            boolean x)
    {
        // get the component
        Component component = context.getModelManager().loadComponent(context,
                componentId);

        // bind it
        component.setScope(scope);
        component.setSourceId(sourceId);
        component.setRegionId(regionId);

        // save the object
        context.getModelManager().saveObject(context, component);
    }

    public static void unassociateComponent(RequestContext context,
            String componentId)
    {
        Component component = context.getModelManager().loadComponent(context,
                componentId);
        component.setScope("");
        component.setSourceId("");
        component.setRegionId("");
        context.getModelManager().saveObject(context, component);
    }

    // helpers (for non associations)
    public static Template[] findTemplates(RequestContext context)
    {
        return findTemplates(context, null);
    }

    public static Template[] findTemplates(RequestContext context,
            String templateType)
    {
        Template[] array = new Template[] {};
        try
        {
            // load all of the component association objects
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "template");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    Template template = (Template) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _templateType = template.getTemplateType();

                    if (templateType != null && !templateType.equalsIgnoreCase(_templateType))
                        okay = false;
                    if (okay)
                        arrayList.add(template);
                }

                array = new Template[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (Template) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        TemplateType[] array = new TemplateType[] {};
        try
        {
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "template-type");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    TemplateType templateType = (TemplateType) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _uri = templateType.getURI();

                    if (uri != null && !uri.equalsIgnoreCase(_uri))
                        okay = false;
                    if (okay)
                        arrayList.add(templateType);
                }

                array = new TemplateType[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (TemplateType) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return array;

    }

    public static Component[] findComponents(RequestContext context,
            String componentTypeId)
    {
        Component[] array = new Component[] {};
        try
        {
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "component");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    Component component = (Component) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _componentTypeId = component.getComponentTypeId();

                    if (componentTypeId != null && !componentTypeId.equalsIgnoreCase(_componentTypeId))
                        okay = false;
                    if (okay)
                        arrayList.add(component);
                }

                array = new Component[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (Component) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return array;
    }

    public static ComponentType[] findComponentTypes(RequestContext context)
    {
        return findComponentTypes(context, null);
    }

    public static ComponentType[] findComponentTypes(RequestContext context,
            String uri)
    {
        ComponentType[] array = new ComponentType[] {};
        try
        {
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "component-type");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    ComponentType componentType = (ComponentType) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _uri = componentType.getURI();

                    if (uri != null && !uri.equalsIgnoreCase(_uri))
                        okay = false;
                    if (okay)
                        arrayList.add(componentType);
                }

                array = new ComponentType[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (ComponentType) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        Page[] array = new Page[] {};
        try
        {
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "page");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    Page page = (Page) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _templateId = page.getTemplateId();
                    boolean _rootPage = page.getRootPage();

                    if (templateId != null && !templateId.equalsIgnoreCase(_templateId))
                        okay = false;
                    if (rootPage != null && !"".equals(rootPage))
                    {
                        if ("true".equals(rootPage) && !_rootPage)
                            okay = false;
                        if ("false".equals(rootPage) && _rootPage)
                            okay = false;
                    }
                    if (okay)
                        arrayList.add(page);
                }

                array = new Page[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (Page) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        Endpoint[] array = new Endpoint[] {};
        try
        {
            String relativeDirectory = context.getConfig().getModelTypePath(
                    "endpoint");
            IFile[] files = context.getModelManager().getFiles(context,
                    relativeDirectory);
            if (files != null)
            {
                List arrayList = new ArrayList();
                for (int i = 0; i < files.length; i++)
                {
                    // this will load from the cache, potentially
                    Endpoint endpoint = (Endpoint) context.getModelManager().loadObject(
                            context, files[i]);

                    boolean okay = true;
                    String _endpointId = endpoint.getEndpointId();

                    if (endpointId != null && !endpointId.equalsIgnoreCase(_endpointId))
                        okay = false;
                    if (okay)
                        arrayList.add(endpoint);
                }

                array = new Endpoint[arrayList.size()];
                for (int j = 0; j < arrayList.size(); j++)
                    array[j] = (Endpoint) arrayList.get(j);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        Page page = context.getModelManager().loadPage(context, pageId);
        page.setTemplateId(templateId, formatId);
        context.getModelManager().saveObject(context, page);
    }

    public static void unassociateTemplate(RequestContext context, String pageId)
    {
        unassociateTemplate(context, pageId, null);
    }

    public static void unassociateTemplate(RequestContext context,
            String pageId, String formatId)
    {
        Page page = context.getModelManager().loadPage(context, pageId);
        page.removeTemplateId(formatId);
        context.getModelManager().saveObject(context, page);
    }

    // extra

    public static Page getRootPage(RequestContext context)
    {
        Page[] rootPages = ModelUtil.findPages(context, null, "true");
        if (rootPages != null && rootPages.length > 0)
            return rootPages[0];
        return null;
    }

    public static Configuration getSiteConfiguration(RequestContext context)
    {
        Configuration[] configurations = ModelUtil.findConfigurations(context,
                "site");
        if (configurations != null && configurations.length > 0)
            return configurations[0];
        return null;
    }
}
