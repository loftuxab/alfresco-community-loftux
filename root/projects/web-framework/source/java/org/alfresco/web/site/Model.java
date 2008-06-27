package org.alfresco.web.site;

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
 * The primary interface used by the framework to retrieve objects from
 * the underlying storage mechanism.
 * 
 * @author muzquiano
 */
public interface Model
{
    /**
     * Retrieves a Chrome object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */
    public Chrome getChrome(String id);

    /**
     * Retrieves a Component object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public Component getComponent(String id);

    /**
     * Retrieves a Component object from storage
     * 
     * @param scopeId the scope
     * @param regionId the region id
     * @param sourceId the source id
     * @return the object
     */
    public Component getComponent(String scopeId, String regionId, String sourceId);
    
    /**
     * Retrieves a ComponentType object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public ComponentType getComponentType(String id);

    /**
     * Retrieves a Configuration object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public Configuration getConfiguration(String id);

    /**
     * Retrieves a ContentAssociation object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public ContentAssociation getContentAssociation(String id);

    /**
     * Retrieves a Page object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public Page getPage(String id);
    
    /**
     * Retrieves a PageType object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public PageType getPageType(String id);

    /**
     * Retrieves a PageAssociation object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public PageAssociation getPageAssociation(String id);

    /**
     * Retrieves a TemplateInstance object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public TemplateInstance getTemplate(String id);

    /**
     * Retrieves a TemplateType object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public TemplateType getTemplateType(String id);
    
    /**
     * Retrieves a Theme object from storage
     * 
     * @param id the id of a the object to be retrieved
     * @return the object
     */    
    public Theme getTheme(String id);

    /**
     * Creates a new Chrome object
     * 
     * @return the object
     */   
    public Chrome newChrome();

    /**
     * Creates a new Chrome object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */
    public Chrome newChrome(String objectId);
    
    /**
     * Creates a new Component object
     * 
     * @return the object
     */
    public Component newComponent();
    
    /**
     * Creates a new Component object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */    
    public Component newComponent(String objectId);

    /**
     * Creates a new Component object
     * 
     * @param scopeId the scope
     * @param regionId the region id
     * @param sourceId the source id
     * @return the object
     */
    public Component newComponent(String scopeId, String regionId, String sourceId);
    
    /**
     * Creates a new ComponentType object
     * 
     * @return the object
     */       
    public ComponentType newComponentType();

    /**
     * Creates a new ComponentType object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public ComponentType newComponentType(String objectId);

    /**
     * Creates a new Configuration object
     * 
     * @return the object
     */       
    public Configuration newConfiguration();
    
    /**
     * Creates a new Configuration object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public Configuration newConfiguration(String objectId);

    /**
     * Creates a new ContentAssociation object
     * 
     * @return the object
     */       
    public ContentAssociation newContentAssociation();
    
    /**
     * Creates a new ContentAssociation object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public ContentAssociation newContentAssociation(String objectId);

    /**
     * Creates a new Page object
     * 
     * @return the object
     */       
    public Page newPage();
    
    /**
     * Creates a new Page object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public Page newPage(String objectId);
    
    /**
     * Creates a new PageType object
     * 
     * @return the object
     */       
    public PageType newPageType();
    
    /**
     * Creates a new PageType object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public PageType newPageType(String objectId);

    /**
     * Creates a new PageAssociation object
     * 
     * @return the object
     */       
    public PageAssociation newPageAssociation();
    
    /**
     * Creates a new PageAssociation object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public PageAssociation newPageAssociation(String objectId);

    /**
     * Creates a new Template object
     * 
     * @return the object
     */       
    public TemplateInstance newTemplate();
    
    /**
     * Creates a new TemplateInstance object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public TemplateInstance newTemplate(String objectId);

    /**
     * Creates a new TemplateType object
     * 
     * @return the object
     */           
    public TemplateType newTemplateType();
    
    /**
     * Creates a new TemplateType object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public TemplateType newTemplateType(String objectId);
    
    /**
     * Creates a new Theme object
     * 
     * @return the object
     */           
    public Theme newTheme();
    
    /**
     * Creates a new Theme object with the given id
     * 
     * @param objectId the id to be assigned
     * 
     * @return the object
     */        
    public Theme newTheme(String objectId);

    /**
     * Saves a model object
     * 
     * @param object the object to be saved
     * 
     * @return true if the save completed successfully
     */
    public boolean saveObject(ModelObject object);

    /**
     * Retrieves a model object
     * 
     * @param objectTypeId the type id of the model object
     * @param objectId the id of the model object
     * 
     * @return the object
     */
    public ModelObject getObject(String objectTypeId, String objectId);
    
    /**
     * Removes a model object from storage
     * 
     * @param object the model object
     * 
     * @return true if the delete succeeded
     */
    public boolean removeObject(ModelObject object);
    
    /**
     * Removes a model object from storage
     * 
     * @param objectTypeId the type id of the model object
     * @param objectId the id of the model object
     * 
     * @return
     */
    public boolean removeObject(String objectTypeId, String objectId);

    /**
     * Creates a new object
     * 
     * @param objectTypeId the type id of the model object to be created
     * 
     * @return the object
     */
    public ModelObject newObject(String objectTypeId);

    /**
     * Retrieves all of the objects in storage of a given type
     * 
     * @param objectTypeId the type id of the model object
     * 
     * @return A map of the model objects (keyed by object id)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId);

    /**
     * @return a map of all Chrome objects (keyed by object id)
     */
    public Map<String, ModelObject> findChrome();   

    /**
     * @return a map of all ComponentType objects (keyed by object id)
     */
    public Map<String, ModelObject> findComponentTypes();

    /**
     * @return a map of all Component objects (keyed by object id)
     */    
    public Map<String, ModelObject> findComponents();

    /**
     * @return a map of all Configuration objects (keyed by object id)
     */        
    public Map<String, ModelObject> findConfigurations();

    /**
     * @return a map of all ContentAssociation objects (keyed by object id)
     */            
    public Map<String, ModelObject> findContentAssociations();    

    /**
     * @return a map of all Template objects (keyed by object id)
     */            
    public Map<String, ModelObject> findTemplates();

    /**
     * @return a map of all TemplateType objects (keyed by object id)
     */            
    public Map<String, ModelObject> findTemplateTypes();    

    /**
     * @return a map of all Page objects (keyed by object id)
     */            
    public Map<String, ModelObject> findPages();

    /**
     * @return a map of all PageAssociation objects (keyed by object id)
     */            
    public Map<String, ModelObject> findPageAssociations();
    
    /**
     * @return a map of all PageType objects (keyed by object id)
     */            
    public Map<String, ModelObject> findPageTypes();
    
    /**
     * @return a map of all Theme objects (keyed by object id)
     */            
    public Map<String, ModelObject> findThemes();
    
    /**
     * Performs a filtered lookup of Chrome objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     * 
     * @param chromeType the chrome type
     *  
     * @return a map of Chrome objects (keyed by object id)
     */
    public Map<String, ModelObject> findChrome(String chromeType);    

    /**
     * Performs a filtered lookup of Configuration objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     * 
     * @param sourceId the source id to which the configuration is bound
     *  
     * @return a map of Configuration objects (keyed by object id)
     */    
    public Map<String, ModelObject> findConfigurations(String sourceId);

    /**
     * Performs a filtered lookup of PageAssociation objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     * 
     * @param sourceId the source id of the association
     * @param destId the dest id of the association
     * @param associationType the type of the association
     *  
     * @return a map of PageAssociation objects (keyed by object id)
     */        
    public Map<String, ModelObject> findPageAssociations(String sourceId, 
            String destId, String associationType);

    /**
     * Performs a filtered lookup of ContentAssociation objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     * 
     * @param sourceId the source id of the association
     * @param destId the dest id of the association
     * @param assocType the type of the association
     * @param formatId the format id of the association
     *  
     * @return a map of ContentAssociation objects (keyed by object id)
     */            
    public Map<String, ModelObject> findContentAssociations(String sourceId, String destId, String assocType, String formatId);

    /**
     * Performs a filtered lookup of Component objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     * 
     * @param scope the scope binding of the component
     * @param sourceId the sourceId binding of the component
     * @param regionId the regionId binding of the component
     * @param componentTypeId the component type id of the component
     * 
     * @return a map of Component objects (keyed by object id)
     */
    public Map<String, ModelObject> findComponents(String scope, String sourceId, String regionId, String componentTypeId);
    
    /**
     * Performs a filtered lookup of Component objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     *  
     * @param componentTypeId the component type id of the component
     * 
     * @return a map of Component objects (keyed by object id)
     */
    public Map<String, ModelObject> findComponents(String componentTypeId);
    
    /**
     * Performs a filtered lookup of Template objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter.
     * 
     * @param templateType the template type id of the template
     * 
     * @return a map of Template objects (keyed by object id)
     */
    public Map<String, ModelObject> findTemplates(String templateType);
    
    /**
     * Performs a filtered lookup of TemplateType objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter. 
     * 
     * @param uri the uri property of the template type
     * 
     * @return a map of TemplateType objects (keyed by object id)
     */
    public Map<String, ModelObject> findTemplateTypes(String uri);    
    
    /**
     * Performs a filtered lookup of ComponentType objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter. 
     * 
     * @param uri the uri property of the component type
     * 
     * @return a map of ComponentType objects (keyed by object id)
     */
    public Map<String, ModelObject> findComponentTypes(String uri);
    
    /**
     * Performs a filtered lookup of Page objects
     * 
     * The resultset is filtered against the provided arguments.
     * If an argument is null, it is not included in the filter. 
     * 
     * @param templateId the template id property
     * @param pageTypeId the page type property
     * 
     * @return a map of Page objects (keyed by object id)
     */
    public Map<String, ModelObject> findPages(String templateId, String pageTypeId);

    
    
    /**
     * Associates a destination page to a source page
     * The association type is assumed to be "child"
     * 
     * @param sourceId the id of the source (parent) page
     * @param destId the id of the destination (child) page
     */
    public void associatePage(String sourceId, String destId);
    
    /**
     * Associates a destination page to a source page
     *  
     * @param sourceId the id of the source page
     * @param destId the id of the destination page
     * @param associationType the association type (i.e. "child")
     */
    public void associatePage(String sourceId, String destId, String associationType);
    
    /**
     * Unassociates a destination page from a source page
     * The association type is assumed to be "child"
     * 
     * @param sourceId the id of the source page
     * @param destId the id of the destination page
     */
    public void unassociatePage(String sourceId, String destId);
    
    /**
     * Unassociates a destination page from a source page
     * 
     * @param sourceId the id of the source page
     * @param destId the id of the destination page
     * @param associationTypeId the association type (i.e. "child")
     */
    public void unassociatePage(String sourceId, String destId, String associationTypeId);
    
    /**
     * Unassociates a destination page from a source page
     * 
     * @param pageAssociationId the id of the page association object
     */
    public void unassociatePage(String pageAssociationId);
    
    /**
     * Associates a source content item to a destination page
     *  
     * @param sourceId the content item id
     * @param destId the page id
     * @param assocType the association type
     * @param formatId the format id
     */    
    public void associateContent(String sourceId, String destId, String assocType, String formatId);
    
    /**
     * Unassociates a source content item from a destination page
     * 
     * @param sourceId the content item id
     * @param destId the page id
     * @param assocType the association type
     * @param formatId the format id
     */
    public void unassociateContent(String sourceId, String destId, String assocType, String formatId);
    
    /**
     * Unassociates a source content item from a destination page
     * 
     * @param objectAssociationId the id of the content association object
     */
    public void unassociateContent(String objectAssociationId);
    
    /**
     * Associates a template to a page using the default format
     * 
     * @param templateId the id of the template
     * @param pageId the id of the page
     */
    public void associateTemplate(String templateId, String pageId);
    
    /**
     * Associates a template to a page for a given format
     * 
     * @param templateId the id of the template
     * @param pageId the id of the page
     * @param formatId the id of the format
     */
    public void associateTemplate(String templateId, String pageId, String formatId);
    
    /**
     * Unassociates a template from a page using the default format
     * 
     * @param pageId the id of the page
     */
    public void unassociateTemplate(String pageId);
    
    /**
     * Unassociates a template from a page for a given format
     * @param pageId
     * @param formatId
     */
    public void unassociateTemplate(String pageId, String formatId);
    
    /**
     * Binds a component into a page, template or site
     * 
     * @param componentId the component id
     * @param scope the scope id
     * @param sourceId the source id
     * @param regionId the region id
     */
    public void bindComponent(String componentId, String scope, String sourceId, String regionId);
    
    /**
     * Unbinds a component from a page, template or site
     * 
     * @param componentId the component id
     */
    public void unbindComponent(String componentId);
    
    /**
     * Returns the model object manager instance that this model is using
     * 
     * @return
     */
    public ModelObjectManager getObjectManager();
}
