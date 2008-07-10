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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.tools.EncodingUtil;
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Configuration;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.site.AuthenticationUtil;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
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
    private static final String WEBSCRIPTS_REGISTRY = "webframework.webscripts.registry";
    
    protected ScriptFileSystem rootFileSystem;

    
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

    /**
     * @return  An array of all Component instances in the web application
     */
    public Object[] getComponents()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findComponents());
    }

    /**
     * @return  An array of all ComponentType instances in the web application
     */    
    public Object[] getComponentTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findComponentTypes());
    }

    /**
     * @return  An array of all Configuration instances in the web application
     */    
    public Object[] getConfigurations()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findConfigurations());
    }

    /**
     * @return  An array of all ContentAssociation instances in the web application
     */    
    public Object[] getContentAssociations()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findContentAssociations());
    }

    /**
     * @return  An array of all Page instances in the web application
     */    
    public Object[] getPages()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findPages());
    }

    /**
     * @return  An array of all PageType instances in the web application
     */    
    public Object[] getPageTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findPageTypes());
    }
    
    /**
     * @return  An array of all PageAssociation instances in the web application
     */    
    public Object[] getPageAssociations()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findPageAssociations());
    }

    /**
     * @return  An array of all Template instances in the web application
     */    
    public Object[] getTemplates()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findTemplates());
    }

    /**
     * @return  An array of all TemplateType instances in the web application
     */    
    public Object[] getTemplateTypes()
    {
        return ScriptHelper.toScriptModelObjectArray(context, getModel().findTemplateTypes());
    }

    /**
     * @return A map of all Component instances.  The map is keyed on
     *          on object id
     */
    public Scriptable getComponentsMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findComponents());
    }

    /**
     * @return A map of all ComponentType instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getComponentTypesMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findComponentTypes());
    }

    /**
     * @return A map of all Configuration instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getConfigurationsMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findConfigurations());
    }

    /**
     * @return A map of all Content Association instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getContentAssociationsMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findContentAssociations());
    }

    /**
     * @return A map of all Page instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getPagesMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findPages());
    }

    /**
     * @return A map of all PageAssociation instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getPageAssociationsMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findPageAssociations());
    }

    /**
     * @return A map of all Template instances.  The map is keyed on
     *          on object id
     */
    public Scriptable getTemplatesMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findTemplates());
    }

    /**
     * @return A map of all TemplateType instances.  The map is keyed on
     *          on object id
     */    
    public Scriptable getTemplateTypesMap()
    {
        return ScriptHelper.toScriptableMap(context, getModel().findTemplateTypes());
    }

    /**
     * Creates a new Component instance.
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator. The scope, region and sourceId parameters should be
     * explicitly set before the component is persisted!
     * 
     * @return A ScriptModelObject representing the new instance
     */
    public ScriptModelObject newComponent()
    {
        Component component = (Component) getModel().newComponent();
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new Component instance of the given component type
     * 
     * The id for the instance is generated using the Web Framework's Random
     * GUID generator. The scope, region and sourceId parameters should be
     * explicitly set before the component is persisted!
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String componentTypeId)
    {
        ParameterCheck.mandatoryString("componentTypeId", componentTypeId);

        Component component = (Component) getModel().newComponent();
        component.setComponentTypeId(componentTypeId);
        return ScriptHelper.toScriptModelObject(context, component);
    }

    /**
     * Creates a new Component instance of the given component type. The ID is
     * generated from the supplied scope, region and sourceId parameters.
     * 
     * @param scope         Scope - one of "global", "template" or "page"
     * @param regionId      The id of the region to bind too
     * @param sourceId      The source ID for the given scope
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String scope, String regionId, String sourceId)
    {
        ParameterCheck.mandatoryString("scope", scope);
        ParameterCheck.mandatoryString("regionId", regionId);
        ParameterCheck.mandatoryString("sourceId", sourceId);
        
        Component component = (Component) getModel().newComponent(scope, regionId, sourceId);
        component.setScope(scope);
        component.setRegionId(regionId);
        component.setSourceId(sourceId);
        return ScriptHelper.toScriptModelObject(context, component);
    }
    
    /**
     * Creates a new Component instance of the given component type. The ID is
     * generated from the supplied scope, region and sourceId parameters.
     * 
     * @param componentTypeId   The id of the ComponentType which describes 
     *                          the type of this component.
     * @param scope         Scope - one of "global", "template" or "page"
     * @param regionId      The id of the region to bind too
     * @param sourceId      The source ID for the given scope
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newComponent(String componentTypeId, String scope, String regionId, String sourceId)
    {
        ParameterCheck.mandatoryString("componentTypeId", componentTypeId);
        ParameterCheck.mandatoryString("scope", scope);
        ParameterCheck.mandatoryString("regionId", regionId);
        ParameterCheck.mandatoryString("sourceId", sourceId);
        
        Component component = (Component) getModel().newComponent(scope, regionId, sourceId);
        component.setComponentTypeId(componentTypeId);
        component.setScope(scope);
        component.setRegionId(regionId);
        component.setSourceId(sourceId);
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
        ModelObject modelObject = getModel().newComponentType();
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
        Configuration configuration = (Configuration) getModel().newConfiguration();
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
        ParameterCheck.mandatoryString("sourceId", sourceId);

        Configuration configuration = (Configuration) getModel().newConfiguration();
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
        Page page = (Page) getModel().newPage();
        return ScriptHelper.toScriptModelObject(context, page);
    }

    /**
     * Creates a new Page instance with the specified ID.
     * 
     * @param id  The id of the page instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage(String id)
    {
        ParameterCheck.mandatoryString("id", id);
        Page page = (Page) getModel().newPage(id);
        return ScriptHelper.toScriptModelObject(context, page);
    }
    
    /**
     * Creates a new Page instance with the specified ID.
     * 
     * @param id  The id of the page instance
     * @param title The title of the page instance
     * @param description The description of the page instance
     * 
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newPage(String id, String title, String description)
    {
        ParameterCheck.mandatoryString("id", id);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatoryString("description", description);
        
        Page page = (Page) getModel().newPage(id);
        page.setTitle(title);
        page.setDescription(description);
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
        TemplateInstance template = (TemplateInstance) getModel().newTemplate();
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
        ParameterCheck.mandatoryString("templateTypeId", templateTypeId);
        TemplateInstance template = (TemplateInstance) getModel().newTemplate();
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
     * @param title The name of the Template instance
     * @param description The description of the Template instance
     *  
     * @return A ScriptModelObject representing the new instance
     */    
    public ScriptModelObject newTemplate(String templateTypeId, String title, String description)
    {
        ParameterCheck.mandatoryString("templateTypeId", templateTypeId);
        ParameterCheck.mandatoryString("title", title);
        ParameterCheck.mandatoryString("description", description);
        
        TemplateInstance template = (TemplateInstance) getModel().newTemplate();
        template.setTemplateType(templateTypeId);
        template.setTitle(title);
        template.setDescription(description);
        return ScriptHelper.toScriptModelObject(context, template);
    }
    
    /**
     * Creates model objects based on a given preset id. The preset is looked up and
     * processed by the PresetManager bean. The various objects found in the preset
     * will be generated using the supplied name/value map of tokens.
     * 
     * @param presetId  ID of the preset to generate
     * @param tokens    Token name/value map
     */
    public void newPreset(String presetId, Scriptable tokens)
    {
        ParameterCheck.mandatoryString("presetId", presetId);
        Map<String, String> t = null;
        Object val = getScriptProcessor().unwrapValue(tokens);
        if (val instanceof Map)
        {
            t = (Map)val;
        }
        FrameworkHelper.getPresetsManager().constructPreset(context.getModel(), presetId, t);
    }

    /**
     * Searches for Component instances within the Web Application that 
     * match the provided constraints.  If a constraint is set to null, 
     * it is not considered as part of the search.
     * 
     * @param scope     The value of the "scope" property of the instance
     * @param regionId  The value of the "region" property of the instance
     * @param sourceId  The value of the "sourceId" property of the instance
     * @param componentTypeId   The value of the "componentTypeId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Component results of the search
     */
    public Object[] findComponents(String scope, String regionId, String sourceId, String componentTypeId)
    {
        Map<String, ModelObject> objects = getModel().findComponents(scope, regionId, sourceId, componentTypeId);
        return ScriptHelper.toScriptModelObjectArray(context, objects);
    }
    
    /**
     * Searchs for webscript components with the given family name.
     * 
     * @param family        the family
     * 
     * @return An array of webscripts that match the given family name
     */
    public Object[] findWebScripts(String family)
    {
        List<Description> values = new ArrayList<Description>(16);
        if (family != null)
        {
            Registry registry = (Registry)FrameworkHelper.getApplicationContext().getBean(WEBSCRIPTS_REGISTRY);
            for (WebScript webscript : registry.getWebScripts())
            {
                if (family.equals(webscript.getDescription().getFamily()))
                {
                    values.add(webscript.getDescription());
                }
            }
        }
        return values.toArray(new Object[values.size()]);
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
    public Object[] findPageAssociations(String sourceId, String destId, String associationType)
    {
        Map<String, ModelObject> objects = getModel().findPageAssociations(
                sourceId, destId, associationType);
        return ScriptHelper.toScriptModelObjectArray(context, objects);
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
    public Object[] findContentAssociations(String sourceId, String destId, String assocType, String formatId)
    {
        Map<String, ModelObject> objects = getModel().findContentAssociations(
                sourceId, destId, assocType, formatId);
        return ScriptHelper.toScriptModelObjectArray(context, objects);
    }

    /**
     * Provides a map of ScriptModelObjects that wrap Component instances.
     * The map is keyed by Component object id.
     * 
     * @param scope      The value of the "source" property of the instance 
     * @param regionId   The value of the "regionId" property of the instance
     * @param sourceId   The value of the "sourceId" property of the instance
     * @param componentTypeId  The value of the "componentTypeId" property of the instance
     * 
     * @return  An array of ScriptModelObject instances that wrap the
     *          Component results of the search
     */            
    public Scriptable findComponentsMap(String scope, String regionId, String sourceId, String componentTypeId)
    {
        Map<String, ModelObject> objects = getModel().findComponents(scope, regionId, sourceId, componentTypeId);
        return ScriptHelper.toScriptableMap(context, objects);
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
    public Scriptable findPageAssociationsMap(String sourceId, String destId, String associationType)
    {
        Map<String, ModelObject> objects = getModel().findPageAssociations(
                sourceId, destId, associationType);
        return ScriptHelper.toScriptableMap(context, objects);
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
    public Scriptable findContentAssociationsMap(String sourceId, String destId, String assocType, String formatId)
    {
        Map<String, ModelObject> objects = getModel().findContentAssociations(
                sourceId, destId, assocType, formatId);
        return ScriptHelper.toScriptableMap(context, objects);
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
        Page page = context.getModel().getPage(pageId);
        if (page != null)
        {
            Map<String, TemplateInstance> templatesMap = page.getTemplates(context);
            
            ScriptableMap<String, Serializable> map = new ScriptableMap<String, Serializable>(templatesMap.size());
            Iterator it = templatesMap.keySet().iterator();
            while (it.hasNext())
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
        ScriptModelObject scriptModelObject = null;
        
        Map<String, ModelObject> objects = getModel().findConfigurations(sourceId);
        if(objects.size() > 0)
        {
            ModelObject object = (ModelObject) objects.values().iterator().next();
            scriptModelObject = ScriptHelper.toScriptModelObject(context, object);
        }
        
        return scriptModelObject;
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
        Page page = (Page) context.getModel().getPage(pageId);
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
        Page page = (Page) context.getModel().getPage(pageId);
        if (page != null)
        {
            page.removeTemplateId(formatId);
            context.getModel().saveObject(page);
        }
    }

    
    // Create and Remove Associations

    public void bindComponent(String componentId, String scope, String regionId, String sourceId)
    {
        getModel().bindComponent(componentId, scope, regionId, sourceId);
    }
    
    public void bindComponent(ScriptModelObject componentObject, String scope, String regionId, String sourceId)
    {
        Component component = (Component) componentObject.getModelObject();
        getModel().bindComponent(component, scope, regionId, sourceId);
    }

    public void unbindComponent(String componentId)
    {
        getModel().unbindComponent(componentId);
    }
    
    public void unbindComponent(String scope, String regionId, String sourceId)
    {
        getModel().unbindComponent(scope, regionId, sourceId);
    }

    public void associateTemplate(String templateId, String pageId)
    {
        associateTemplate(templateId, pageId, null);
    }

    public void associateTemplate(String templateId, String pageId,
            String formatId)
    {
        getModel().associateTemplate(templateId, pageId, formatId);
    }

    public void unassociateTemplate(String pageId)
    {
        unassociateTemplate(pageId, null);
    }

    public void unassociateTemplate(String pageId, String formatId)
    {
        getModel().unassociateTemplate(pageId, formatId);
    }

    public void associatePage(String sourceId, String destId)
    {
        getModel().associatePage(sourceId, destId);
    }

    public void unassociatePage(String sourceId, String destId)
    {
        getModel().unassociatePage(sourceId, destId);
    }

    public void associateContent(String contentId, String pageId,
            String formatId)
    {
        getModel().associateContent(contentId, pageId, "content",
                formatId);
    }

    public void unassociateContent(String contentId, String pageId,
            String formatId)
    {
        getModel().unassociateContent(contentId, pageId, "content",
                formatId);
    }

    public void associateContentType(String contentTypeId, String pageId,
            String formatId)
    {
        getModel().associateContent(contentTypeId, pageId,
                "content-type", formatId);
    }

    public void unassociateContentType(String contentTypeId, String pageId,
            String formatId)
    {
        getModel().unassociateContent(contentTypeId, pageId,
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
    
    // also still in progress
    
    // returns the credential vault for the current user
    public ScriptCredentialVault getCredentialVault()
    {
        return new ScriptCredentialVault(getRequestContext());
    }
    
        
    public ScriptModelObject getChrome(String objectId)
    {
        ModelObject obj = getModel().getChrome(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getComponent(String objectId)
    {
        ModelObject obj = getModel().getComponent(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getComponent(String scope, String regionId, String sourceId)
    {
        ModelObject obj = getModel().getComponent(scope, regionId, sourceId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getComponentType(String objectId)
    {
        ModelObject obj = getModel().getComponentType(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getConfiguration(String objectId)
    {
        ModelObject obj = getModel().getConfiguration(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getContentAssociation(String objectId)
    {
        ModelObject obj = getModel().getContentAssociation(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getPage(String objectId)
    {
        ModelObject obj = getModel().getPage(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getPageType(String objectId)
    {
        ModelObject obj = getModel().getPageType(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getPageAssociation(String objectId)
    {
        ModelObject obj = getModel().getPageAssociation(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getTemplate(String objectId)
    {
        ModelObject obj = getModel().getTemplate(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    public ScriptModelObject getTemplateType(String objectId)
    {
        ModelObject obj = getModel().getTemplateType(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    public ScriptModelObject getTheme(String objectId)
    {
        ModelObject obj = getModel().getTheme(objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
    
    private static ScriptProcessor getScriptProcessor()
    {
        return (ScriptProcessor)FrameworkHelper.getApplicationContext().getBean("webframework.scriptprocessor");
    }
}
