package com.activiti.repo.dictionary.bootstrap;

import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2Association;
import com.activiti.repo.dictionary.metamodel.M2ChildAssociation;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.dictionary.metamodel.MetaModelDAO;
import com.activiti.repo.ref.QName;
import com.activiti.repo.version.lightweight.LightWeightVersionStoreBase;


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
    private boolean createVersionModel = true;
    
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
        createPropertyTypes();
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
        metaModelDAO.save();
    }
    

    /**
     * Create bootstrap Property Type definitions.
     */
    private void createPropertyTypes()
    {
        M2PropertyType stringType = metaModelDAO.createPropertyType(PropertyTypeDefinition.STRING);
        M2PropertyType dateType = metaModelDAO.createPropertyType(PropertyTypeDefinition.DATE);
        M2PropertyType booleanType = metaModelDAO.createPropertyType(PropertyTypeDefinition.BOOLEAN);
        M2PropertyType qnameType = metaModelDAO.createPropertyType(PropertyTypeDefinition.QNAME);
        M2PropertyType idType = metaModelDAO.createPropertyType(PropertyTypeDefinition.GUID);
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
        M2Aspect referenceAspect = metaModelDAO.createAspect(QName.createQName("test", "referenceable"));
        M2Property idProp = referenceAspect.createProperty("id");
        idProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.GUID));
        idProp.setMandatory(true);
        idProp.setProtected(false);
        idProp.setMultiValued(false);
        
        // Create Test Base Type
        M2Type baseType = metaModelDAO.createType(QName.createQName("test", "base"));
        M2Property primaryTypeProp = baseType.createProperty("primaryType");
        primaryTypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.QNAME));
        primaryTypeProp.setMandatory(true);
        primaryTypeProp.setProtected(true);
        primaryTypeProp.setMultiValued(false);
        M2Property aspectsProp = baseType.createProperty("aspects");
        aspectsProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.QNAME));
        aspectsProp.setMandatory(true);
        aspectsProp.setProtected(true);
        aspectsProp.setMultiValued(true);

        // Create Test File Type
        M2Type fileType = metaModelDAO.createType(QName.createQName("test", "file"));
        fileType.setSuperClass(baseType);
        fileType.getDefaultAspects().add(referenceAspect);
        M2Property encodingProp = fileType.createProperty("encoding");
        encodingProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.STRING));
        encodingProp.setMandatory(true);
        encodingProp.setMultiValued(false);
        M2Property mimetypeProp = fileType.createProperty("mimetype");
        mimetypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.STRING));
        mimetypeProp.setMandatory(true);
        mimetypeProp.setMultiValued(false);
        
        // Create Test Folder Type
        M2Type folderType = metaModelDAO.createType(QName.createQName("test", "folder"));
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
                        LightWeightVersionStoreBase.LW_VERSION_STORE_NAMESPACE,
                        LightWeightVersionStoreBase.TYPE_VERSION));
        
        // Create verison number property
        M2Property versionNumber = versionType.createProperty(
                LightWeightVersionStoreBase.ATTR_VERSION_NUMBER.getLocalName());
        versionNumber.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.STRING));  // TODO Should be INT
        versionNumber.setMandatory(true);
        versionNumber.setMultiValued(false);
        
        // Create verison label property
        M2Property versionLabel = versionType.createProperty(
                LightWeightVersionStoreBase.ATTR_VERSION_LABEL.getLocalName());
        versionLabel.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.STRING));  
        versionLabel.setMandatory(true);
        versionLabel.setMultiValued(false);
        
        // Create created date property
        M2Property createdDate = versionType.createProperty(
                LightWeightVersionStoreBase.ATTR_VERSION_CREATED_DATE.getLocalName());
        createdDate.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.DATE));  
        createdDate.setMandatory(true);
        createdDate.setMultiValued(false);
        
        // Add the successor association
        M2Association successorAssoc = versionType.createAssociation(
                LightWeightVersionStoreBase.ASSOC_SUCCESSOR.getLocalName());
        successorAssoc.getRequiredToClasses().add(versionType);
        successorAssoc.setMandatory(false);
        successorAssoc.setMultiValued(true);
        
        // Create version history type
        M2Type versionHistoryType = metaModelDAO.createType(
                QName.createQName(
                        LightWeightVersionStoreBase.LW_VERSION_STORE_NAMESPACE, 
                        LightWeightVersionStoreBase.TYPE_VERSION_HISTORY));
        
        // Create versioned node id property
        M2Property nodeIdProperty = versionHistoryType.createProperty(
                LightWeightVersionStoreBase.ATTR_VERSIONED_NODE_ID.getLocalName());
        nodeIdProperty.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.GUID));
        nodeIdProperty.setMandatory(true);
        nodeIdProperty.setMultiValued(false);
        
        // Add the child assoc
        M2ChildAssociation versionChildAssoc = versionHistoryType.createChildAssociation(
                LightWeightVersionStoreBase.CHILD_VERSIONS.getLocalName());
        versionChildAssoc.getRequiredToClasses().add(versionType);
        versionChildAssoc.setMandatory(true);
        versionChildAssoc.setMultiValued(true);
        
        // Add the root version association
        M2Association rootVersionAssoc = versionHistoryType.createAssociation(
                LightWeightVersionStoreBase.ASSOC_ROOT_VERSION.getLocalName());
        rootVersionAssoc.getRequiredToClasses().add(versionType);
        rootVersionAssoc.setMandatory(true);
        rootVersionAssoc.setMultiValued(false);
        
    }
}
