/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.connector.remote.Response;
import org.alfresco.connector.remote.ScriptRemote;
import org.alfresco.tools.EncodingUtil;
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.site.Framework;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.Endpoint;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.TemplateInstance;
import org.mozilla.javascript.Scriptable;

/**
 * The "site" object represents the starting point for working with
 * the web framework.
 * 
 * From this object, you can access the site model,
 * perform read and write updates to components, pages and model
 * configurations.
 * 
 * @author muzquiano
 */
public final class ScriptSite extends ScriptBase
{
    protected ScriptFileSystem rootFileSystem;
    protected ScriptFileSystem modelFileSystem;

    public ScriptSite(RequestContext context)
    {
        super(context);
    }

    // API

    public ScriptModelObject getRootPage()
    {
        ModelObject modelObject = context.getRootPage();
        return toScriptModelObject(context, modelObject);
    }

    public ScriptFileSystem getFileSystem()
    {
        if (rootFileSystem == null)
            rootFileSystem = new ScriptFileSystem(
                    getRequestContext().getFileSystem());
        return rootFileSystem;
    }

    public ScriptFileSystem getModelFileSystem()
    {
        if (modelFileSystem == null)
            modelFileSystem = new ScriptFileSystem(
                    getRequestContext().getModel().getFileSystem());
        return modelFileSystem;
    }

    public String callEndpoint(String endpointId, String uri)
    {
        Endpoint endpoint = ModelUtil.getEndpoint(context, endpointId);
        if (endpoint == null)
        {
            return null;
        }

        StringBuffer ep = new StringBuffer();
        ep.append(endpoint.getEndpointURL());

        String endpointString = ep.toString();
        String user = endpoint.getUsername();
        String pass = endpoint.getPassword();

        return callRemote(endpointString, user, pass, uri);
    }

    public String callRemote(String endpointString, String user, String pass,
            String uri)
    {
        ScriptRemote remote = new ScriptRemote(endpointString, null);
        remote.setUsernamePassword(user, pass);

        Response r = remote.call(uri);
        return r.getResponse();
    }

    public RequestContext getRequestContext()
    {
        return context;
    }

    //
    // Arrays
    //

    public Object[] getComponents()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findComponents(context));
    }

    public Object[] getComponentTypes()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findComponentTypes(context));
    }

    public Object[] getConfigurations()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findConfigurations(context));
    }

    public Object[] getContentAssociations()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findContentAssociations(context));
    }

    public Object[] getEndpoints()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findEndpoints(context));
    }

    public Object[] getPages()
    {
        return toScriptModelObjectArray(context, ModelUtil.findPages(context));
    }

    public Object[] getPageAssociations()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findPageAssociations(context));
    }

    public Object[] getTemplates()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findTemplates(context));
    }

    public Object[] getTemplateTypes()
    {
        return toScriptModelObjectArray(context,
                ModelUtil.findTemplateTypes(context));
    }

    //
    // Maps
    //

    public Scriptable getComponentsMap()
    {
        return toScriptableMap(context, ModelUtil.findComponents(context));
    }

    public Scriptable getComponentTypesMap()
    {
        return toScriptableMap(context, ModelUtil.findComponentTypes(context));
    }

    public Scriptable getConfigurationsMap()
    {
        return toScriptableMap(context, ModelUtil.findConfigurations(context));
    }

    public Scriptable getContentAssociationsMap()
    {
        return toScriptableMap(context,
                ModelUtil.findContentAssociations(context));
    }

    public Scriptable getEndpointsMap()
    {
        return toScriptableMap(context, ModelUtil.findEndpoints(context));
    }

    public Scriptable getPagesMap()
    {
        return toScriptableMap(context, ModelUtil.findPages(context));
    }

    public Scriptable getPageAssociationsMap()
    {
        return toScriptableMap(context, ModelUtil.findPageAssociations(context));
    }

    public Scriptable getTemplatesMap()
    {
        return toScriptableMap(context, ModelUtil.findTemplates(context));
    }

    public Scriptable getTemplateTypesMap()
    {
        return toScriptableMap(context, ModelUtil.findTemplateTypes(context));
    }

    //
    // Instantiators
    //

    public ScriptModelObject newComponent()
    {
        Component component = (Component) Framework.getModel().newComponent(
                context);
        return toScriptModelObject(context, component);
    }

    public ScriptModelObject newComponent(String componentTypeId)
    {
        ParameterCheck.mandatory("componentTypeId", componentTypeId);

        Component component = (Component) Framework.getModel().newComponent(
                context);
        component.setComponentTypeId(componentTypeId);
        return toScriptModelObject(context, component);
    }

    public ScriptModelObject newComponent(String componentTypeId, String name,
            String description)
    {
        ParameterCheck.mandatory("componentTypeId", componentTypeId);
        ParameterCheck.mandatory("name", name);
        ParameterCheck.mandatory("description", description);

        Component component = (Component) Framework.getModel().newComponent(
                context);
        component.setComponentTypeId(componentTypeId);
        component.setName(name);
        component.setDescription(description);
        return toScriptModelObject(context, component);
    }

    public ScriptModelObject newComponentType()
    {
        ModelObject modelObject = Framework.getModel().newComponentType(context);
        return toScriptModelObject(context, modelObject);
    }

    public ScriptModelObject newConfiguration()
    {
        Configuration configuration = (Configuration) Framework.getModel().newConfiguration(
                context);
        return toScriptModelObject(context, configuration);
    }

    public ScriptModelObject newConfiguration(String sourceId)
    {
        ParameterCheck.mandatory("sourceId", sourceId);

        Configuration configuration = (Configuration) Framework.getModel().newConfiguration(
                context);
        configuration.setSourceId(sourceId);
        return toScriptModelObject(context, configuration);
    }

    public ScriptModelObject newEndpoint()
    {
        ModelObject modelObject = Framework.getModel().newEndpoint(context);
        return toScriptModelObject(context, modelObject);
    }

    public ScriptModelObject newPage()
    {
        Page page = (Page) Framework.getModel().newPage(context);
        return toScriptModelObject(context, page);
    }

    public ScriptModelObject newPage(String name, String description)
    {
        ParameterCheck.mandatory("name", name);
        ParameterCheck.mandatory("description", description);

        Page page = (Page) Framework.getModel().newPage(context);
        return toScriptModelObject(context, page);
    }

    public ScriptModelObject newTemplate()
    {
        TemplateInstance template = (TemplateInstance) Framework.getModel().newTemplate(context);
        return toScriptModelObject(context, template);
    }

    public ScriptModelObject newTemplate(String templateType)
    {
        TemplateInstance template = (TemplateInstance) Framework.getModel().newTemplate(context);
        template.setTemplateType(templateType);
        return toScriptModelObject(context, template);
    }

    public ScriptModelObject newTemplate(String templateType, String name,
            String description)
    {
        TemplateInstance template = (TemplateInstance) Framework.getModel().newTemplate(context);
        template.setTemplateType(templateType);
        template.setName(name);
        template.setDescription(description);
        return toScriptModelObject(context, template);
    }

    // Lookups

    public Object[] findComponents(String scopeId, String sourceId,
            String regionId, String componentTypeId)
    {
        Component[] components = ModelUtil.findComponents(context, scopeId,
                sourceId, regionId, componentTypeId);
        return toScriptModelObjectArray(context, components);
    }

    public Scriptable findComponentsMap(String scopeId, String sourceId,
            String regionId, String componentTypeId)
    {
        Component[] components = ModelUtil.findComponents(context, scopeId,
                sourceId, regionId, componentTypeId);
        return toScriptableMap(context, components);
    }

    public Object[] findChildPageAssociations(String sourceId, String destId)
    {
        return findPageAssociations(sourceId, destId, "child");
    }

    public Object[] findPageAssociations(String sourceId, String destId,
            String associationType)
    {
        PageAssociation[] associations = ModelUtil.findPageAssociations(
                context, sourceId, destId, associationType);
        return toScriptModelObjectArray(context, associations);
    }

    public Scriptable findPageAssociationsMap(String sourceId, String destId,
            String associationType)
    {
        PageAssociation[] associations = ModelUtil.findPageAssociations(
                context, sourceId, destId, associationType);
        return toScriptableMap(context, associations);
    }

    public Object[] findContentAssociations(String sourceId, String destId,
            String assocType, String formatId)
    {
        ContentAssociation[] associations = ModelUtil.findContentAssociations(
                context, sourceId, destId, assocType, formatId);
        return toScriptModelObjectArray(context, associations);
    }

    public Scriptable findContentAssociationsMap(String sourceId,
            String destId, String assocType, String formatId)
    {
        ContentAssociation[] associations = ModelUtil.findContentAssociations(
                context, sourceId, destId, assocType, formatId);
        return toScriptableMap(context, associations);
    }

    public Scriptable findTemplatesMap(String pageId)
	{
		Page page = context.getModel().loadPage(context, pageId);
		if(page != null)
		{
			Map<String, TemplateInstance> templatesMap = page.getTemplates(context);
			
			ScriptableMap<String, Serializable> map = new ScriptableMap<String, Serializable>();
			Iterator it = templatesMap.keySet().iterator();
			while(it.hasNext())
			{
				String formatId = (String) it.next();
				TemplateInstance template = templatesMap.get(formatId);
				
				ScriptModelObject scriptModelObject = toScriptModelObject(context, template);
				map.put(formatId, scriptModelObject);
			}
			return map;	
		}
		return null;
	}

    public ScriptModelObject findConfiguration(String sourceId)
    {
        Configuration[] configurations = ModelUtil.findConfigurations(context,
                sourceId);
        if (configurations != null && configurations.length > 0)
            return toScriptModelObject(context, configurations[0]);
        return null;
    }

    public ScriptModelObject findTemplate(String pageId)
    {
        return findTemplate(pageId, null);
    }

    public ScriptModelObject findTemplate(String pageId, String formatId)
    {
        Page page = (Page) context.getModel().loadPage(context, pageId);
        if (page != null)
        {
            TemplateInstance t = page.getTemplate(context, formatId);
            if (t != null)
                return this.toScriptModelObject(context, t);
        }
        return null;
    }

    public void removeTemplate(String pageId, String formatId)
    {
        Page page = (Page) context.getModel().loadPage(context, pageId);
        if (page != null)
        {
            page.removeTemplateId(formatId);
            page.save(context);
        }
    }

    // Create and Remove Associations

    public void associateComponent(String componentId, String scope,
            String sourceId, String regionId)
    {
        ModelUtil.associateComponent(context, componentId, scope, sourceId,
                regionId);
    }

    public void unassociateComponent(String componentId)
    {
        ModelUtil.unassociateComponent(context, componentId);
    }

    public void associateTemplate(String templateId, String pageId)
    {
        associateTemplate(templateId, pageId, null);
    }

    public void associateTemplate(String templateId, String pageId,
            String formatId)
    {
        ModelUtil.associateTemplate(context, templateId, pageId, formatId);
    }

    public void unassociateTemplate(String pageId)
    {
        unassociateTemplate(pageId, null);
    }

    public void unassociateTemplate(String pageId, String formatId)
    {
        ModelUtil.unassociateTemplate(context, pageId, formatId);
    }

    public void associatePage(String sourceId, String destId)
    {
        ModelUtil.associatePage(context, sourceId, destId);
    }

    public void unassociatePage(String sourceId, String destId)
    {
        ModelUtil.unassociatePage(context, sourceId, destId);
    }

    public void associateContent(String contentId, String pageId,
            String formatId)
    {
        ModelUtil.associateContent(context, contentId, pageId, "content",
                formatId);
    }

    public void unassociateContent(String contentId, String pageId,
            String formatId)
    {
        ModelUtil.unassociateContent(context, contentId, pageId, "content",
                formatId);
    }

    public void associateContentType(String contentTypeId, String pageId,
            String formatId)
    {
        ModelUtil.associateContent(context, contentTypeId, pageId,
                "content-type", formatId);
    }

    public void unassociateContentType(String contentTypeId, String pageId,
            String formatId)
    {
        ModelUtil.unassociateContent(context, contentTypeId, pageId,
                "content-type", formatId);
    }

    // extra

    public ScriptModelObject findEndpoint(String endpointId)
    {
        Endpoint[] endpoints = ModelUtil.findEndpoints(context, endpointId);
        if (endpoints != null && endpoints.length > 0)
        {
            return toScriptModelObject(context, endpoints[0]);
        }
        return null;
    }

    // helper methods
    public String encode(String input)
    {
        return EncodingUtil.encode(input);
    }

    public String encode(String input, String encoding)
    {
        return EncodingUtil.encode(input, encoding);
    }

    public String decode(String input)
    {
        return EncodingUtil.decode(input);
    }

    public String decode(String input, String encoding)
    {
        return EncodingUtil.decode(input, encoding);
    }
}
