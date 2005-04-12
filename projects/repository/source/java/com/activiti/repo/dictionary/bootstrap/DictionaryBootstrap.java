package com.activiti.repo.dictionary.bootstrap;

import com.activiti.repo.dictionary.DictionaryException;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2Association;
import com.activiti.repo.dictionary.metamodel.M2ChildAssociation;
import com.activiti.repo.dictionary.metamodel.M2NamespacePrefix;
import com.activiti.repo.dictionary.metamodel.M2NamespaceURI;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.dictionary.metamodel.MetaModelDAO;
import com.activiti.repo.dictionary.metamodel.NamespaceDAO;
import com.activiti.repo.ref.QName;
import com.activiti.repo.version.lightweight.VersionStoreBaseImpl;


/**
 * Dictionary Bootsrap.
 * 
 * Provides support for creating initial set of meta-data.
 * 
 * @author David Caruana
 *
 */
public class DictionaryBootstrap
{

    /**
     * Namespace DAO
     */
    private NamespaceDAO namespaceDAO = null;
    
    /**
     * Meta Model DAO
     */
    private MetaModelDAO metaModelDAO = null;

    /**
     * Create meta-model definitions during bootstrap
     */
    private boolean createMetaModel = false;
    
    /**
     * Create test model definitions during bootstrap
     */
    private boolean createTestModel = false;

    /**
     * Create light weight version store model defintions during bootstrap
     */
    private boolean createVersionModel = false;

    
    /**
     * Sets the Namespace DAO to boostrap with
     * 
     * @param namespaceDAO  the namespace DAO
     */
    public void setNamespaceDAO(NamespaceDAO namespaceDAO)
    {
        this.namespaceDAO = namespaceDAO;
    }
    
    /**
     * Sets the Meta Model DAO to boostrap with
     * 
     * @param metaModelDAO  the meta model DAO
     */
    public void setMetaModelDAO(MetaModelDAO metaModelDAO)
    {
        this.metaModelDAO = metaModelDAO;
    }

    /**
     * Sets whether to create meta model definitions during bootstrap
     *   
     * @param createMetaModel
     */
    public void setCreateMetaModel(boolean createMetaModel)
    {
        this.createMetaModel = createMetaModel;
    }

    /**
     * Sets whether to create test model definitions during bootstrap
     * 
     * @param createTestModel
     */
    public void setCreateTestModel(boolean createTestModel)
    {
        this.createTestModel = createTestModel;
    }
    
    /**
     * Sets whether to create the light weight version store model
     * during bootstrap
     * 
     * @param createVersionModel
     */
    public void setCreateVersionModel(boolean createVersionModel)
    {
        this.createVersionModel = createVersionModel;
    }

    
    /**
     * Create bootstrap meta-data definitions. 
     */
    public void bootstrap()
    {
        if (namespaceDAO == null)
        {
            throw new DictionaryException("Namespace DAO has not been provided");
        }
        if (metaModelDAO == null)
        {
            throw new DictionaryException("Meta Model DAO has not been provided");
        }
        
        // Create core definitions
        createNamespaces();
        createPropertyTypes();
        
        // Create optional definitions
        if (createMetaModel)
        {
            createMetaModel();
        }
        if (createTestModel)
        {
            createTestModel();
        }
        if (createVersionModel)
        {
            createVersionModel();
        }
    }
    
    
    /**
     * Create bootstrap Namespace definitions
     */
    private void createNamespaces()
    {
        // Default Namespace
        M2NamespaceURI defaultURI = namespaceDAO.createURI(NamespaceService.DEFAULT_URI);
        M2NamespacePrefix defaultPrefix = namespaceDAO.createPrefix(NamespaceService.DEFAULT_PREFIX);
        defaultPrefix.setURI(defaultURI);
        
        // Activiti Namespace
        M2NamespaceURI activitiURI = namespaceDAO.createURI(NamespaceService.ACTIVITI_URI);
        M2NamespacePrefix activitiPrefix = namespaceDAO.createPrefix(NamespaceService.ACTIVITI_PREFIX);
        activitiPrefix.setURI(activitiURI);
        
        // Activiti Test Namespace
        M2NamespaceURI activitiTestURI = namespaceDAO.createURI(NamespaceService.ACTIVITI_TEST_URI);
        M2NamespacePrefix activitiTestPrefix = namespaceDAO.createPrefix(NamespaceService.ACTIVITI_TEST_PREFIX);
        activitiTestPrefix.setURI(activitiTestURI);
    }
    
    
    /**
     * Create bootstrap Property Type definitions.
     */
    private void createPropertyTypes()
    {
        M2PropertyType ANY = metaModelDAO.createPropertyType(PropertyTypeDefinition.ANY);
        M2PropertyType TEXT = metaModelDAO.createPropertyType(PropertyTypeDefinition.TEXT);
        M2PropertyType CONTENT = metaModelDAO.createPropertyType(PropertyTypeDefinition.CONTENT);
        M2PropertyType INT = metaModelDAO.createPropertyType(PropertyTypeDefinition.INT);
        M2PropertyType LONG = metaModelDAO.createPropertyType(PropertyTypeDefinition.LONG);
        M2PropertyType FLOAT = metaModelDAO.createPropertyType(PropertyTypeDefinition.FLOAT);
        M2PropertyType DOUBLE = metaModelDAO.createPropertyType(PropertyTypeDefinition.DOUBLE);
        M2PropertyType DATE = metaModelDAO.createPropertyType(PropertyTypeDefinition.DATE);
        M2PropertyType DATETIME = metaModelDAO.createPropertyType(PropertyTypeDefinition.DATETIME);
        M2PropertyType BOOLEAN = metaModelDAO.createPropertyType(PropertyTypeDefinition.BOOLEAN);
        M2PropertyType NAME = metaModelDAO.createPropertyType(PropertyTypeDefinition.NAME);
        M2PropertyType GUID = metaModelDAO.createPropertyType(PropertyTypeDefinition.GUID);
        M2PropertyType CATEGORY = metaModelDAO.createPropertyType(PropertyTypeDefinition.CATEGORY);
    }
    

    /**
     * Create boostrap definitions that describe the Dictionary M2 meta-model.
     */
    private void createMetaModel()
    {
        // TODO: Implement...
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * Create a simple test model.
     */
    private void createTestModel()
    {
        // Create Test Referencable Aspect
        M2Aspect referenceAspect = metaModelDAO.createAspect(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "referenceable"));
        M2Property idProp = referenceAspect.createProperty("id");
        idProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.GUID));
        idProp.setMandatory(true);
        idProp.setProtected(false);
        idProp.setMultiValued(false);
        
        // Create Test Base Type
        M2Type baseType = metaModelDAO.createType(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "base"));
        M2Property primaryTypeProp = baseType.createProperty("primaryType");
        primaryTypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.NAME));
        primaryTypeProp.setMandatory(true);
        primaryTypeProp.setProtected(true);
        primaryTypeProp.setMultiValued(false);
        M2Property aspectsProp = baseType.createProperty("aspects");
        aspectsProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.NAME));
        aspectsProp.setMandatory(false);
        aspectsProp.setProtected(true);
        aspectsProp.setMultiValued(true);

        // Create Test File Type
        M2Type fileType = metaModelDAO.createType(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file"));
        fileType.setSuperClass(baseType);
        fileType.getDefaultAspects().add(referenceAspect);
        M2Property encodingProp = fileType.createProperty("encoding");
        encodingProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        encodingProp.setMandatory(true);
        encodingProp.setMultiValued(false);
        M2Property mimetypeProp = fileType.createProperty("mimetype");
        mimetypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        mimetypeProp.setMandatory(true);
        mimetypeProp.setMultiValued(false);
        
        // Create Test Folder Type
        M2Type folderType = metaModelDAO.createType(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "folder"));
        folderType.setSuperClass(baseType);
        folderType.getDefaultAspects().add(referenceAspect);
        M2ChildAssociation contentsAssoc = folderType.createChildAssociation("contents");
        contentsAssoc.getRequiredToClasses().add(fileType);
        contentsAssoc.setMandatory(false);
        contentsAssoc.setMultiValued(true);
    }
    
    /**
     * Create the model to support the light weight version store implementation     
     */
    private void createVersionModel()
    {
        // TODO these type should extend the base type
        
        // Create version type
        M2Type versionType = metaModelDAO.createType(
                QName.createQName(
                        VersionStoreBaseImpl.LW_VERSION_STORE_NAMESPACE,
                        VersionStoreBaseImpl.TYPE_VERSION));
        
        // Create verison number property
        M2Property versionNumber = versionType.createProperty(
                VersionStoreBaseImpl.ATTR_VERSION_NUMBER.getLocalName());
        versionNumber.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.INT));
        versionNumber.setMandatory(true);
        versionNumber.setMultiValued(false);
        
        // Create verison label property
        M2Property versionLabel = versionType.createProperty(
                VersionStoreBaseImpl.ATTR_VERSION_LABEL.getLocalName());
        versionLabel.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));  
        versionLabel.setMandatory(true);
        versionLabel.setMultiValued(false);
        
        // Create created date property
        M2Property createdDate = versionType.createProperty(
                VersionStoreBaseImpl.ATTR_VERSION_CREATED_DATE.getLocalName());
        createdDate.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.DATE));  
        createdDate.setMandatory(true);
        createdDate.setMultiValued(false);
        
        // Add the successor association
        M2Association successorAssoc = versionType.createAssociation(
                VersionStoreBaseImpl.ASSOC_SUCCESSOR.getLocalName());
        successorAssoc.getRequiredToClasses().add(versionType);
        successorAssoc.setMandatory(false);
        successorAssoc.setMultiValued(true);
        
        // Create version history type
        M2Type versionHistoryType = metaModelDAO.createType(
                QName.createQName(
                        VersionStoreBaseImpl.LW_VERSION_STORE_NAMESPACE, 
                        VersionStoreBaseImpl.TYPE_VERSION_HISTORY));
        
        // Create versioned node id property
        M2Property nodeIdProperty = versionHistoryType.createProperty(
                VersionStoreBaseImpl.ATTR_VERSIONED_NODE_ID.getLocalName());
        nodeIdProperty.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.GUID));
        nodeIdProperty.setMandatory(true);
        nodeIdProperty.setMultiValued(false);
        
        // Add the child assoc
        M2ChildAssociation versionChildAssoc = versionHistoryType.createChildAssociation(
                VersionStoreBaseImpl.CHILD_VERSIONS.getLocalName());
        versionChildAssoc.getRequiredToClasses().add(versionType);
        versionChildAssoc.setMandatory(true);
        versionChildAssoc.setMultiValued(true);
        
        // Add the root version association
        M2Association rootVersionAssoc = versionHistoryType.createAssociation(
                VersionStoreBaseImpl.ASSOC_ROOT_VERSION.getLocalName());
        rootVersionAssoc.getRequiredToClasses().add(versionType);
        rootVersionAssoc.setMandatory(true);
        rootVersionAssoc.setMultiValued(false);
        
    }
}
