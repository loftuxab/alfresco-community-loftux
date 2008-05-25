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

import org.alfresco.tools.EncodingUtil;
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.site.AuthenticationUtil;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.ModelUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.Component;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.ContentAssociation;
import org.alfresco.web.site.model.ModelObject;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.PageAssociation;
import org.alfresco.web.site.model.TemplateInstance;
import org.mozilla.javascript.Scriptable;

/**
 * A read-only root-scoped Java object for working with the Web Framework 
 * and the Web Framework object model.
 * 
 * Using this object, you can query the Web Framework object model,
 * perform read and write operations and configure your web application.
 * 
 * Among the things that you can work against in the Web Framework
 * object model are components, page, templates, configurations and associations.
 * 
 * @author muzquiano
 */
public final class ScriptSiteData extends ScriptBase
{
    protected ScriptFileSystem rootFileSystem;
    protected ScriptFileSystem modelFileSystem;

    /**
     * Constructs a new ScriptSite object around the provided request context
     * 
     * @param context   The RequestContext instance for the current request
     */
    public ScriptSiteData(RequestContext context)
    {
        super(context);
    }
    
    // no properties
    public ScriptableMap buildProperties()
    {
        return null;
    }
    

    // --------------------------------------------------------------
    // JavaScript Properties
    //

    
    /**
     * Provides access to the root page for the web application.  If no
     * root page is defined, null will be returned.
     * 
     * @return  The root page to the web application.
     */
    public ScriptModelObject getRootPage()
    {
        ModelObject modelObject = context.getRootPage();
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }
    
    /**
     * Provides access to the site configuration object for the web
     * application.  If a site configuration is not defined, null
     * will be returned.
     * 
     * @return The configuration object for the site
     */
    public ScriptModelObject getSiteConfiguration()
    {
        ModelObject modelObject = context.getSiteConfiguration();
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }
    
    /**
     * Provides access to the web application file system abstraction.
     * This is file system mounted to the root of the web application.
     * 
     * @return  The File System abstraction
     */
    public ScriptFileSystem getFileSystem()
    {
        if (rootFileSystem == null)
        {
            rootFileSystem = new ScriptFileSystem(
                    getRequestContext().getFileSystem());
        }
        return rootFileSystem;
    }
    
    // --------------------------------------------------------------
    // JavaScript Functions
    //
    

    /**
     * Provides access to the file system for the current model.
     * 
     * Model files are stored in a root folder - the file system paths
     * returned by this file system are relative to that root folder.
     * 
     * @return The model File System abstraction
     */
    public ScriptFileSystem getModelFileSystem()
    {
        if (modelFileSystem == null)
        {
            modelFileSystem = new ScriptFileSystem(
                    getRequestContext().getModel().getFileSystem());
        }
        return modelFileSystem;
    }

    /**
     * @return  An array of all Component instances in the web application
     */
    public Object[] getComponents()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findComponents(context));
    }

    /**
     * @return  An array of all ComponentType instances in the web application
     */    
    public Object[] getComponentTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findComponentTypes(context));
    }

    /**
     * @return  An array of all Configuration instances in the web application
     */    
    public Object[] getConfigurations()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findConfigurations(context));
    }

    /**
     * @return  An array of all ContentAssociation instances in the web application
     */    
    public Object[] getContentAssociations()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findContentAssociations(context));
    }

    /**
     * @return  An array of all Page instances in the web application
     */    
    public Object[] getPages()
    {
        return ScriptHelper.toScriptModelObjectArray(context, ModelUtil.findPages(context));
    }

    /**
     * @return  An array of all PageType instances in the web application
     */    
    public Object[] getPageTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, ModelUtil.findPageTypes(context));
    }
    
    /**
     * @return  An array of all PageAssociation instances in the web application
     */    
    public Object[] getPageAssociations()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findPageAssociations(context));
    }

    /**
     * @return  An array of all Template instances in the web application
     */    
    public Object[] getTemplates()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findTemplates(context));
    }

    /**
     * @return  An array of all TemplateType instances in the web application
     */    
    public Object[] getTemplateTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context,
                ModelUtil.findTemplateTypes(context));
    }

    /**
     * @return A map of all Component instances.  The map is keyed on
     *          on object id
     */
    public Scriptable getComponentsMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findComponents(context));
    }

    /**
     * @return A map of all ComponentType instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getComponentTypesMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findComponentTypes(context));
    }

    /**
     * @return A map of all Configuration instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getConfigurationsMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findConfigurations(context));
    }

    /**
     * @return A map of all Content Association instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getContentAssociationsMap()
    {
        return ScriptHelper.toScriptableMap(context,
                ModelUtil.findContentAssociations(context));
    }

    /**
     * @return A map of all Page instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getPagesMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findPages(context));
    }

    /**
     * @return A map of all PageAssociation instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getPageAssociationsMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findPageAssociations(context));
    }

    /**
     * @return A map of all Template instances.  The map is keyed on
     *          on object id
     */
    public Scriptable getTemplatesMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findTemplates(context));
    }

    /**
     * @return A map of all TemplateType instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getTemplateTypesMap()
    {
        return ScriptHelper.toScriptableMap(context, ModelUtil.findTemplateTypes(context));
    }

    /**
     * Creates a new Component instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newComponent()
    {
        Component component = (Component) FrameworkHelper.getModel().newComponent(
                context);
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new Component instance of the given component type
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String componentTypeId)
    {
        ParameterCheck.mandatory("componentTypeId", componentTypeId);

        Component component = (Component) FrameworkHelper.getModel().newComponent(
                context);
        component.setComponentTypeId(componentTypeId);
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new Component instance of the given component type
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component.
     * @param title The name of the component instance
     * @param description The description of the Component instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String componentTypeId, String title,
            String description)
    {
        ParameterCheck.mandatory("componentTypeId", componentTypeId);
        ParameterCheck.mandatory("title", title);
        ParameterCheck.mandatory("description", description);

        Component component = (Component) FrameworkHelper.getModel().newComponent(
                context);
        component.setComponentTypeId(componentTypeId);
        component.setTitle(title);
        component.setDescription(description);
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new ComponentType instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newComponentType()
    {
        ModelObject modelObject = FrameworkHelper.getModel().newComponentType(context);
        return ScriptHelper.toScriptModelObject(context, modelObject);
    }

    /**
     * Creates a new Configuration instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newConfiguration()
    {
        Configuration configuration = (Configuration) FrameworkHelper.getModel().newConfiguration(
                context);
        return ScriptHelper.toScriptModelObject(context, configuration);
    }

    /**
     * Creates a new Configuration instance that is bound to the given sourceId.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     *
     * @param sourceId The value to assign to the sourceId property
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newConfiguration(String sourceId)
    {
        ParameterCheck.mandatory("sourceId", sourceId);

        Configuration configuration = (Configuration) FrameworkHelper.getModel().newConfiguration(
                context);
        configuration.setSourceId(sourceId);
        return ScriptHelper.toScriptModelObject(context, configuration);
    }

    /**
     * Creates a new Page instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage()
    {
        Page page = (Page) FrameworkHelper.getModel().newPage(context);
        return ScriptHelper.toScriptModelObject(context, page);
    }

    /**
     * Creates a new Page instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param title  The title of the new instance
     * @param description   The description of the new instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage(String title, String description)
    {
        ParameterCheck.mandatory("title", title);
        ParameterCheck.mandatory("description", description);

        Page page = (Page) FrameworkHelper.getModel().newPage(context);
        return ScriptHelper.toScriptModelObject(context, page);
    }
    
    /**
     * Creates a new Template instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplate()
    {
        TemplateInstance template = (TemplateInstance) FrameworkHelper.getModel().newTemplate(context);
        return ScriptHelper.toScriptModelObject(context, template);
    }

    /**
     * Creates a new Template instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param templateTypeId  The id of the TemplateType object that describes
     *                      the type of this template
     *                      
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newTemplate(String templateTypeId)
    {
        TemplateInstance template = (TemplateInstance) FrameworkHelper.getModel().newTemplate(context);
        template.setTemplateType(templateTypeId);
        return ScriptHelper.toScriptModelObject(context, template);
    }

    /**
     * Creates a new Component instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator.
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component.
     * @param title The name of the TemplateType instance
     * @param description The description of the TemplateType instance
     *  
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplate(String templateTypeId, String title,
            String description)
    {
        ParameterCheck.mandatory("templateTypeId", templateTypeId);
        ParameterCheck.mandatory("title", title);
        ParameterCheck.mandatory("description", description);
        
        TemplateInstance template = (TemplateInstance) FrameworkHelper.getModel().newTemplate(context);
        template.setTemplateType(templateTypeId);
        template.setTitle(title);
        template.setDescription(description);
        return ScriptHelper.toScriptModelObject(context, template);
    }

    /**
     * Searches for Component instances within the Web Application that 
     * match the provided constraints.  If a constraint is set to null, 
     * it is not considered as part of the search.
     * 
     * @param scopeId   The value of the "scopeId" property of the instance
     * @param sourceId  The value of the "sourceId" property of the instance
     * @param regionId  The value of the "regionId" property of the instance
     * @param componentTypeId   The value of the "componentTypeId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Component results of the search
     */
    public Object[] findComponents(String scopeId, String sourceId,
            String regionId, String componentTypeId)
    {
        Component[] components = ModelUtil.findComponents(context, scopeId,
                sourceId, regionId, componentTypeId);
        return ScriptHelper.toScriptModelObjectArray(context, components);
    }

    /**
     * Searches for PageAssociation instances within the Web Application that 
     * are of association type 'child' and which match the specified 
     * constraints. If a constraint is set to null, it is not considered as 
     * part of the search.
     * 
     * @param scopeId   The value of the "scopeId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          PageAssociation results of the search
     */    
    public Object[] findChildPageAssociations(String sourceId, String destId)
    {
        return findPageAssociations(sourceId, destId, "child");
    }

    /**
     * Searches for PageAssociation instances within the Web Application that 
     * are of association type 'child' and which match the specified
     * constraints.  If a constraint is set to null, it is not considered as
     * part of the search.
     * 
     * @param scopeId   The value of the "scopeId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param associationType   The value of the "associationType" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          PageAssociation results of the search
     */        
    public Object[] findPageAssociations(String sourceId, String destId,
            String associationType)
    {
        PageAssociation[] associations = ModelUtil.findPageAssociations(
                context, sourceId, destId, associationType);
        return ScriptHelper.toScriptModelObjectArray(context, associations);
    }

    /**
     * Searches for ContentAssociation instances within the Web Application that 
     * match the specified constraints.  If a constraint is set to null, 
     * it is not considered as part of the search.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param assocType  The value of the "assocType" property of the instance
     * @param formatId  The value of the "formatId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          ContentAssociation results of the search
     */        
    public Object[] findContentAssociations(String sourceId, String destId,
            String assocType, String formatId)
    {
        ContentAssociation[] associations = ModelUtil.findContentAssociations(
                context, sourceId, destId, assocType, formatId);
        return ScriptHelper.toScriptModelObjectArray(context, associations);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap Component instances.
     * The map is keyed by Component object id.
     * 
     * @param scopeId   The value of the "sourceId" property of the instance 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param regionId  The value of the "regionId" property of the instance
     * @param componentTypeId  The value of the "componentTypeId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Component results of the search
     */            
    public Scriptable findComponentsMap(String scopeId, String sourceId,
            String regionId, String componentTypeId)
    {
        Component[] components = ModelUtil.findComponents(context, scopeId,
                sourceId, regionId, componentTypeId);
        return ScriptHelper.toScriptableMap(context, components);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap PageAssociation instances.
     * The map is keyed by PageAssociation object id.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param associationType  The value of the "associationType" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          PageAssociation results of the search
     */            
    public Scriptable findPageAssociationsMap(String sourceId, String destId,
            String associationType)
    {
        PageAssociation[] associations = ModelUtil.findPageAssociations(
                context, sourceId, destId, associationType);
        return ScriptHelper.toScriptableMap(context, associations);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap ContentAssociation instances.
     * The map is keyed by ContentAssociation object id.
     * 
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param destId  The value of the "destId" property of the instance
     * @param assocType  The value of the "assocType" property of the instance
     * @param formatId  The value of the "formatId" property of the instance 
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          ContentAssociation results of the search
     */            
    public Scriptable findContentAssociationsMap(String sourceId,
            String destId, String assocType, String formatId)
    {
        ContentAssociation[] associations = ModelUtil.findContentAssociations(
                context, sourceId, destId, assocType, formatId);
        return ScriptHelper.toScriptableMap(context, associations);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap Template instances.
     * The map is keyed by Template object id.
     * 
     * @param pageId  The value of the "pageId" property of the instance 
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Template instance results of the search
     */                    
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
                
                ScriptModelObject scriptModelObject = ScriptHelper.toScriptModelObject(context, template);
                map.put(formatId, scriptModelObject);
            }
            return map; 
        }
        return null;
    }

    /**
     * Looks up Configuration instances and returns the first instance
     * that is found for the matching constraints.
     * 
     * @param sourceId The value of the "sourceId" property
     * 
     * @return A ScriptModelObject instance that wraps the Configuration instance
     */
    public ScriptModelObject findConfiguration(String sourceId)
    {
        Configuration[] configurations = ModelUtil.findConfigurations(context,
                sourceId);
        if (configurations != null && configurations.length > 0)
        {
            return ScriptHelper.toScriptModelObject(context, configurations[0]);
        }
        return null;
    }

    /**
     * Looks up Template instances and returns the first instance
     * that is found for the matching constraints.
     * 
     * @param pageId The value of the "pageId" property
     * 
     * @return A ScriptModelObject instance that wraps the Template instance
     */
    public ScriptModelObject findTemplate(String pageId)
    {
        return findTemplate(pageId, null);
    }

    /**
     * Looks up Template instances and returns the first instance
     * that is found for the matching constraints.
     * 
     * @param pageId The value of the "pageId" property
     * @param formatId The value of the "formatId" property
     * 
     * @return A ScriptModelObject instance that wraps the Template instance
     */
    public ScriptModelObject findTemplate(String pageId, String formatId)
    {
        Page page = (Page) context.getModel().loadPage(context, pageId);
        if (page != null)
        {
            TemplateInstance t = page.getTemplate(context, formatId);
            if (t != null)
            {
                return ScriptHelper.toScriptModelObject(context, t);
            }
        }
        return null;
    }

    /**
     * Looks up the given Page and unbinds any Template instances that
     * are bound to the page (keyed by formatId)
     * 
     * If you would like to remove the default Template instance,
     * set formatId to null
     * 
     * @param pageId    The id of the Page
     * @param formatId  The format
     */
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

    public void associateComponent(String componentId, String scopeId,
            String sourceId, String regionId)
    {
        ModelUtil.associateComponent(context, componentId, scopeId, sourceId,
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
    
    // methods that are still in progress
    public void logout()
    {
        AuthenticationUtil.logout(getRequestContext());
    }        
}
