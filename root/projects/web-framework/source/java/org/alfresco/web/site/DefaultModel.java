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

import org.alfresco.web.framework.ModelObject;
import org.alfresco.web.framework.ModelObjectManager;
import org.alfresco.web.framework.model.Chrome;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.ComponentType;
import org.alfresco.web.framework.model.Configuration;
import org.alfresco.web.framework.model.ContentAssociation;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.PageAssociation;
import org.alfresco.web.framework.model.PageType;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.TemplateType;
import org.alfresco.web.framework.model.Theme;

/**
 * Default implementation of the model.
 * 
 * @author muzquiano
 */
public class DefaultModel extends AbstractModel
{ 
    /**
     * Instantiates a new default model.
     * 
     * @param manager the ModelObjectManager
     */
    public DefaultModel(ModelObjectManager manager)
    {
        super(manager);
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getChrome(java.lang.String)
     */
    public Chrome getChrome(String objectId)
    {
        return (Chrome) getObject(Chrome.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getComponent(java.lang.String)
     */
    public Component getComponent(String objectId)
    {
        return (Component) getObject(Component.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getComponent(java.lang.String, java.lang.String, java.lang.String)
     */
    public Component getComponent(String scopeId, String regionId, String sourceId)
    {
        String componentId = Component.generateId(scopeId, regionId, sourceId);
        return getComponent(componentId);
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getComponentType(java.lang.String)
     */
    public ComponentType getComponentType(String objectId)
    {
        return (ComponentType) getObject(ComponentType.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getConfiguration(java.lang.String)
     */
    public Configuration getConfiguration(String objectId)
    {
        return (Configuration) getObject(Configuration.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getContentAssociation(java.lang.String)
     */
    public ContentAssociation getContentAssociation(String objectId)
    {
        return (ContentAssociation) getObject(ContentAssociation.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getPage(java.lang.String)
     */
    public Page getPage(String objectId)
    {
        return (Page) getObject(Page.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getPageType(java.lang.String)
     */
    public PageType getPageType(String objectId)
    {
        return (PageType) getObject(PageType.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getPageAssociation(java.lang.String)
     */
    public PageAssociation getPageAssociation(String objectId)
    {
        return (PageAssociation) getObject(PageAssociation.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getTemplate(java.lang.String)
     */
    public TemplateInstance getTemplate(String objectId)
    {
        return (TemplateInstance) getObject(TemplateInstance.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getTemplateType(java.lang.String)
     */
    public TemplateType getTemplateType(String objectId)
    {
        return (TemplateType) getObject(TemplateType.TYPE_ID, objectId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getTheme(java.lang.String)
     */
    public Theme getTheme(String objectId)
    {
        return (Theme) getObject(Theme.TYPE_ID, objectId);
    }
    
    // instantiation

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newChrome()
     */
    public Chrome newChrome()
    {
        return (Chrome) newObject(Chrome.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newChrome(java.lang.String)
     */
    public Chrome newChrome(String objectId)
    {
        return (Chrome) newObject(Chrome.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newComponent()
     */
    public Component newComponent()
    {
        return (Component) newObject(Component.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newComponent(java.lang.String)
     */
    public Component newComponent(String objectId)
    {
        return (Component) newObject(Component.TYPE_ID, objectId);
    }
    
    /**
     * Creates a new Component object
     * 
     * @param scopeId the scope
     * @param regionId the region id
     * @param sourceId the source id
     * @return the object
     */
    public Component newComponent(String scopeId, String regionId, String sourceId)
    {
        String componentId = Component.generateId(scopeId, regionId, sourceId);
        return newComponent(componentId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newComponentType()
     */
    public ComponentType newComponentType()
    {
        return (ComponentType) newObject(ComponentType.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newComponentType(java.lang.String)
     */
    public ComponentType newComponentType(String objectId)
    {
        return (ComponentType) newObject(ComponentType.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newConfiguration()
     */
    public Configuration newConfiguration()
    {
        return (Configuration) newObject(Configuration.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newConfiguration(java.lang.String)
     */
    public Configuration newConfiguration(String objectId)
    {
        return (Configuration) newObject(Configuration.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newContentAssociation()
     */
    public ContentAssociation newContentAssociation()
    {
        return (ContentAssociation) newObject(ContentAssociation.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newContentAssociation(java.lang.String)
     */
    public ContentAssociation newContentAssociation(String objectId)
    {
        return (ContentAssociation) newObject(ContentAssociation.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newPage()
     */
    public Page newPage()
    {
        return (Page) newObject(Page.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newPage(java.lang.String)
     */
    public Page newPage(String objectId)
    {
        return (Page) newObject(Page.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newPageType()
     */
    public PageType newPageType()
    {
        return (PageType) newObject(PageType.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newPageType(java.lang.String)
     */
    public PageType newPageType(String objectId)
    {
        return (PageType) newObject(PageType.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newPageAssociation()
     */
    public PageAssociation newPageAssociation()
    {
        return (PageAssociation) newObject(PageAssociation.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newPageAssociation(java.lang.String)
     */
    public PageAssociation newPageAssociation(String objectId)
    {
        return (PageAssociation) newObject(PageAssociation.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newTemplate()
     */
    public TemplateInstance newTemplate()
    {
        return (TemplateInstance) newObject(TemplateInstance.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newTemplate(java.lang.String)
     */
    public TemplateInstance newTemplate(String objectId)
    {
        return (TemplateInstance) newObject(TemplateInstance.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newTemplateType()
     */
    public TemplateType newTemplateType()
    {
        return (TemplateType) newObject(TemplateType.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newTemplateType(java.lang.String)
     */
    public TemplateType newTemplateType(String objectId)
    {
        return (TemplateType) newObject(TemplateType.TYPE_ID, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newTheme()
     */
    public Theme newTheme()
    {
        return (Theme) newObject(Theme.TYPE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newTheme(java.lang.String)
     */
    public Theme newTheme(String objectId)
    {
        return (Theme) newObject(Theme.TYPE_ID, objectId);
    }
    
    // generics

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#saveObject(org.alfresco.web.framework.ModelObject)
     */
    public boolean saveObject(ModelObject object)
    {
        return this.getObjectManager().saveObject(object);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getObject(java.lang.String, java.lang.String)
     */
    public ModelObject getObject(String objectTypeId, String objectId)
    {
        return this.getObjectManager().getObject(objectTypeId, objectId);
    }
                
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#removeObject(org.alfresco.web.framework.ModelObject)
     */
    public boolean removeObject(ModelObject object)
    {
        return this.getObjectManager().removeObject(object);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#removeObject(java.lang.String, java.lang.String)
     */
    public boolean removeObject(String objectTypeId, String objectId)
    {
        return this.getObjectManager().removeObject(objectTypeId, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#newObject(java.lang.String)
     */
    public ModelObject newObject(String objectTypeId)
    {
        return this.getObjectManager().newObject(objectTypeId);
    }

    /**
     * New object.
     * 
     * @param objectTypeId the object type id
     * @param objectId the object id
     * 
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId, String objectId)
    {
        return this.getObjectManager().newObject(objectTypeId, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#getAllObjects(java.lang.String)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId)
    {
        return this.getObjectManager().getAllObjects(objectTypeId);
    }
    
    
    ///////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findChrome()
     */
    public Map<String, ModelObject> findChrome()
    {
        return findChrome(null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findChrome(java.lang.String)
     */
    public Map<String, ModelObject> findChrome(String chromeType)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, Chrome.PROP_CHROME_TYPE, chromeType);

        // do the lookup
        return findObjects(Chrome.TYPE_ID, propertyConstraintMap);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findConfigurations()
     */
    public Map<String, ModelObject> findConfigurations()
    {
        return findConfigurations(null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findConfigurations(java.lang.String)
     */
    public Map<String, ModelObject> findConfigurations(String sourceId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, Configuration.PROP_SOURCE_ID, sourceId);

        // do the lookup
        return findObjects(Configuration.TYPE_ID, propertyConstraintMap);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findPageAssociations()
     */
    public Map<String, ModelObject> findPageAssociations()
    {
        return findPageAssociations(null, null, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findPageAssociations(java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> findPageAssociations(String sourceId, 
            String destId, String associationType)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_SOURCE_ID, sourceId);
        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_DEST_ID, destId);
        addPropertyConstraint(propertyConstraintMap,
                PageAssociation.PROP_ASSOC_TYPE, associationType);

        // do the lookup
        return findObjects(PageAssociation.TYPE_ID, propertyConstraintMap);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findContentAssociations()
     */
    public Map<String, ModelObject> findContentAssociations()
    {
        return findContentAssociations(null, null, null, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findContentAssociations(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> findContentAssociations(
            String sourceId, String destId, String assocType, 
            String formatId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_SOURCE_ID, sourceId);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_DEST_ID, destId);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_ASSOC_TYPE, assocType);
        addPropertyConstraint(propertyConstraintMap,
                ContentAssociation.PROP_FORMAT_ID, formatId);

        // do the lookup
        return findObjects(ContentAssociation.TYPE_ID, propertyConstraintMap);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findComponents()
     */
    public Map<String, ModelObject> findComponents()
    {
        return findComponents(null, null, null, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findComponents(java.lang.String)
     */
    public Map<String, ModelObject> findComponents(String componentTypeId)
    {
        return findComponents(null, null, null, componentTypeId);
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findComponents(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> findComponents(String scope, String sourceId,
            String regionId, String componentTypeId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, Component.PROP_SCOPE,
                scope);
        addPropertyConstraint(propertyConstraintMap, Component.PROP_SOURCE_ID,
                sourceId);
        addPropertyConstraint(propertyConstraintMap, Component.PROP_REGION_ID,
                regionId);
        addPropertyConstraint(propertyConstraintMap,
                Component.PROP_COMPONENT_TYPE_ID, componentTypeId);

        // do the lookup
        return findObjects(Component.TYPE_ID, propertyConstraintMap);
    }

    // helpers (for non associations)
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findTemplates()
     */
    public Map<String, ModelObject> findTemplates()
    {
        return findTemplates(null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findTemplates(java.lang.String)
     */
    public Map<String, ModelObject> findTemplates(String templateType)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap,
                TemplateInstance.PROP_TEMPLATE_TYPE, templateType);

        // do the lookup
        return findObjects(TemplateInstance.TYPE_ID, propertyConstraintMap);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findTemplateTypes()
     */
    public Map<String, ModelObject> findTemplateTypes()
    {
        return findTemplateTypes(null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findTemplateTypes(java.lang.String)
     */
    public Map<String, ModelObject> findTemplateTypes(String uri)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, TemplateType.PROP_URI, uri);

        // do the lookup
        return findObjects(TemplateType.TYPE_ID, propertyConstraintMap);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findComponentTypes()
     */
    public Map<String, ModelObject> findComponentTypes()
    {
        return findComponentTypes(null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findComponentTypes(java.lang.String)
     */
    public Map<String, ModelObject> findComponentTypes(String uri)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, ComponentType.PROP_URI,
                uri);

        // do the lookup
        return findObjects(ComponentType.TYPE_ID, propertyConstraintMap);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findPages()
     */
    public Map<String, ModelObject> findPages()
    {
        return findPages(null, null, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findPages(java.lang.String, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> findPages(String templateId, String rootPage,
            String pageTypeId)
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();
        addPropertyConstraint(propertyConstraintMap, Page.PROP_TEMPLATE_INSTANCE,
                templateId);
        addPropertyConstraint(propertyConstraintMap, Page.PROP_ROOT_PAGE,
                rootPage);
        addPropertyConstraint(propertyConstraintMap, Page.PROP_PAGE_TYPE_ID,
                pageTypeId);

        // do the lookup
        return findObjects(Page.TYPE_ID, propertyConstraintMap);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findPageTypes()
     */
    public Map<String, ModelObject> findPageTypes()
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();

        // do the lookup
        return findObjects(PageType.TYPE_ID, propertyConstraintMap);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#findThemes()
     */
    public Map<String, ModelObject> findThemes()
    {
        // build property map
        Map<String, Object> propertyConstraintMap = newPropertyConstraintMap();

        // do the lookup
        return findObjects(Theme.TYPE_ID, propertyConstraintMap);
    }
    
    
    
    
    
    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#associatePage(java.lang.String, java.lang.String)
     */
    public void associatePage(String sourceId, String destId)
    {
        associatePage(sourceId, destId, "child");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#associatePage(java.lang.String, java.lang.String, java.lang.String)
     */
    public void associatePage(String sourceId, String destId, String associationType)
    {
        // first call unassociate just to be safe
        unassociatePage(sourceId, destId, associationType);

        // create a new template association
        PageAssociation pageAssociation = newPageAssociation();
        pageAssociation.setSourceId(sourceId);
        pageAssociation.setDestId(destId);
        pageAssociation.setAssociationType(associationType);

        // save the object
        saveObject(pageAssociation);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociatePage(java.lang.String, java.lang.String)
     */
    public void unassociatePage(String sourceId, String destId)
    {
        unassociatePage(sourceId, destId, "child");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociatePage(java.lang.String, java.lang.String, java.lang.String)
     */
    public void unassociatePage(String sourceId, String destId, String associationTypeId)
    {
        Map<String, ModelObject> objects = findPageAssociations(sourceId, destId, associationTypeId);
        Iterator it = objects.keySet().iterator();
        while(it.hasNext())
        {
            String pageAssociationId = (String) it.next();
            unassociatePage(pageAssociationId);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociatePage(java.lang.String)
     */
    public void unassociatePage(String pageAssociationId)
    {
        removeObject(Page.TYPE_ID, pageAssociationId);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#associateContent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void associateContent(String sourceId, String destId, String assocType, String formatId)
    {
        // first call unassociate just to be safe
        unassociateContent(sourceId, destId, assocType, formatId);

        // create a new association
        ContentAssociation association = newContentAssociation();
        association.setSourceId(sourceId);
        association.setDestId(destId);
        association.setAssociationType(assocType);
        association.setFormatId(formatId);

        // save the object
        saveObject(association);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociateContent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void unassociateContent(String sourceId, String destId, String assocType, String formatId)
    {
        Map<String, ModelObject> objects = findContentAssociations(sourceId, destId, assocType, formatId);
        Iterator it = objects.keySet().iterator();
        while(it.hasNext())
        {
            String associationId = (String) it.next();
            unassociateContent(associationId);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociateContent(java.lang.String)
     */
    public void unassociateContent(String objectAssociationId)
    {
        removeObject(ContentAssociation.TYPE_ID, objectAssociationId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#associateTemplate(java.lang.String, java.lang.String)
     */
    public void associateTemplate(String templateId, String pageId)
    {
        associateTemplate(templateId, pageId, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#associateTemplate(java.lang.String, java.lang.String, java.lang.String)
     */
    public void associateTemplate(String templateId, String pageId, String formatId)
    {
        Page page = getPage(pageId);
        page.setTemplateId(templateId, formatId);
        saveObject(page);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociateTemplate(java.lang.String)
     */
    public void unassociateTemplate(String pageId)
    {
        unassociateTemplate(pageId, null);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unassociateTemplate(java.lang.String, java.lang.String)
     */
    public void unassociateTemplate(String pageId, String formatId)
    {
        Page page = getPage(pageId);
        page.removeTemplateId(formatId);
        saveObject(page);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#bindComponent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void bindComponent(String componentId, String scope, String sourceId, String regionId)
    {
        // first unassociate any existing components with these bindings
        Map<String, ModelObject> objects = findComponents(scope, sourceId, regionId, null);
        Iterator it = objects.keySet().iterator();
        while(it.hasNext())
        {
            String id = (String) it.next();
            unbindComponent(id);
        }
        
        // get the component
        Component component = getComponent(componentId);

        // bind it
        component.setScope(scope);
        component.setSourceId(sourceId);
        component.setRegionId(regionId);

        // save the object
        saveObject(component);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.Model#unbindComponent(java.lang.String)
     */
    public void unbindComponent(String componentId)
    {
        Component component = getComponent(componentId);
        component.setScope("");
        component.setSourceId("");
        component.setRegionId("");
        saveObject(component);
    }
    
    /**
     * Creates a new property constraint map.
     * 
     * The map describes property constraints that should be applied
     * when filtering the resultset.
     * 
     * @return the map< string, object>
     */
    protected Map<String, Object> newPropertyConstraintMap()
    {
        return new HashMap<String, Object>(8, 1.0f);        
    }
    
    /**
     * Filtering function that looks up objects of a given type id
     * and then applies the provided property constraint map.
     * 
     * @param typeName the type name
     * @param propertyConstraintMap the property constraint map
     * 
     * @return the map
     */
    protected Map<String, ModelObject> findObjects(String objectTypeId, Map<String, Object> propertyConstraintMap)
    {
        Map<String, ModelObject> objectsMap = null;        
        try
        {
            objectsMap = getAllObjects(objectTypeId);
            
            List<String> toRemove = new ArrayList<String>(16);

            Iterator objectsIt = objectsMap.keySet().iterator();
            while(objectsIt.hasNext())
            {
                boolean success = true;
                
                String objectKey = (String) objectsIt.next();
                ModelObject object = (ModelObject) objectsMap.get(objectKey);

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

                if (!success)
                {
                    toRemove.add(objectKey);
                }
            }
            
            // remove anything we no longer want to keep
            for(int i = 0; i < toRemove.size(); i++)
            {
                String objectKey = (String) toRemove.get(i);
                objectsMap.remove(objectKey);
            }            
        }
        catch (Exception ex)
        {
            FrameworkHelper.getLogger().fatal(ex);
        }
        
        return objectsMap;
    }

    /**
     * Adds the property constraint.
     * 
     * @param propertyConstraintMap the property constraint map
     * @param propertyName the property name
     * @param propertyValue the property value
     */
    protected void addPropertyConstraint(Map propertyConstraintMap,
            String propertyName, Object propertyValue)
    {
        if (propertyValue != null)
        {
            propertyConstraintMap.put(propertyName, propertyValue);
        }
    }
        
}
