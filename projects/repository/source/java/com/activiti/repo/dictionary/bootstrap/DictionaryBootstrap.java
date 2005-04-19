package com.activiti.repo.dictionary.bootstrap;

import java.io.File;

import com.activiti.repo.dictionary.ClassRef;
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
import com.activiti.repo.dictionary.metamodel.emf.EMFMetaModelDAO;
import com.activiti.repo.dictionary.metamodel.emf.EMFNamespaceDAO;
import com.activiti.repo.dictionary.metamodel.emf.EMFResource;
import com.activiti.repo.ref.QName;
import com.activiti.repo.version.lightweight.VersionStoreBaseImpl;
import com.activiti.util.debug.CodeMonkey;

/**
 * Provides support for creating initial set of meta-data.
 * 
 * @author David Caruana
 */
public class DictionaryBootstrap
{
    // expected bootstrap types
    public static final QName TYPE_QNAME_BASE = QName.createQName(NamespaceService.ACTIVITI_URI, "base");
    public static final QName TYPE_QNAME_REFERENCE = QName.createQName(NamespaceService.ACTIVITI_URI, "reference");
    public static final QName TYPE_QNAME_CONTAINER = QName.createQName(NamespaceService.ACTIVITI_URI, "container");
    public static final QName TYPE_QNAME_CONTENT = QName.createQName(NamespaceService.ACTIVITI_URI, "content");
    public static final QName ASPECT_QNAME_CONTENT = QName.createQName(NamespaceService.ACTIVITI_URI, "aspect_content");
    public static final ClassRef TYPE_BASE = new ClassRef(TYPE_QNAME_BASE); 
    public static final ClassRef TYPE_REFERENCE = new ClassRef(TYPE_QNAME_REFERENCE); 
    public static final ClassRef TYPE_CONTAINER = new ClassRef(TYPE_QNAME_CONTAINER);
    public static final ClassRef TYPE_CONTENT = new ClassRef(TYPE_QNAME_CONTENT);
    public static final ClassRef ASPECT_CONTENT = new ClassRef(ASPECT_QNAME_CONTENT);
    
    // expected application types
    public static final QName TYPE_QNAME_FOLDER = QName.createQName(NamespaceService.ACTIVITI_URI, "folder");
    public static final QName TYPE_QNAME_FILE = QName.createQName(NamespaceService.ACTIVITI_URI, "file");
    public static final ClassRef TYPE_FOLDER = new ClassRef(TYPE_QNAME_FOLDER);
    public static final ClassRef TYPE_FILE = new ClassRef(TYPE_QNAME_FILE);

    // test types
    public static final QName TEST_TYPE_QNAME_FOLDER = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "folder");
    public static final QName TEST_TYPE_QNAME_FILE = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file");
    public static final ClassRef TEST_TYPE_FOLDER = new ClassRef(TEST_TYPE_QNAME_FOLDER);
    public static final ClassRef TEST_TYPE_FILE = new ClassRef(TEST_TYPE_QNAME_FILE);

    private NamespaceDAO namespaceDAO = null;
    private MetaModelDAO metaModelDAO = null;

    /**
     * @param namespaceDAO  the namespace DAO to bootstrap with
     */
    public void setNamespaceDAO(NamespaceDAO namespaceDAO)
    {
        this.namespaceDAO = namespaceDAO;
    }
    
    /**
     * @param metaModelDAO  the meta model DAO to bootstrap with
     */
    public void setMetaModelDAO(MetaModelDAO metaModelDAO)
    {
        this.metaModelDAO = metaModelDAO;
    }
    
    /**
     * Run this to generate the bootstrap dictionary model to a temp file, which can
     * be copied to the default resource location
     * 
     * @see com.activiti.repo.dictionary.metamodel.emf.EMFResource#DEFAULT_RESOURCEURI
     * @see #bootstrapModel()
     */
    public static void main(String[] args) throws Exception
    {
        // Construct Bootstrap Service
        EMFResource resource = new EMFResource();
        File tempFile = File.createTempFile("dictionary", ".xml");
        resource.setURI(tempFile.getAbsolutePath());
        resource.initCreate();

        // Construct EMF Namespace DAO
        EMFNamespaceDAO namespaceDao = new EMFNamespaceDAO();
        namespaceDao.setResource(resource);
        namespaceDao.init();
        
        // Construct EMF Meta Model DAO
        EMFMetaModelDAO metaModelDao = new EMFMetaModelDAO();
        metaModelDao.setResource(resource);
        metaModelDao.init();

        // Construct Bootstrap Service
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        bootstrap.setNamespaceDAO(namespaceDao);
        bootstrap.setMetaModelDAO(metaModelDao);

        // Create test Bootstrap definitions
        bootstrap.bootstrapModel();
        
        // save it
        resource.save();
        
        // report
        System.out.print("Generated dictionary model: \n" +
                "   file: " + tempFile + "\n" +
                "   default bootstrap location is: " + EMFResource.DEFAULT_RESOURCEURI);
    }

    /**
     * Loads the dictionary model into memory
     */
    public void bootstrapModel()
    {
        CodeMonkey.todo("Read model from a configuration file"); // TODO
        if (namespaceDAO == null)
        {
            throw new DictionaryException("Namespace DAO has not been provided");
        }
        if (metaModelDAO == null)
        {
            throw new DictionaryException("Meta Model DAO has not been provided");
        }
        createNamespaces();
        createPropertyTypes();
        createMetaModel();
        createVersionModel();
    }
    
    /**
     * Loads the test dictionary model into memory
     */
    public void bootstrapTestModel()
    {
        if (namespaceDAO == null)
        {
            throw new DictionaryException("Namespace DAO has not been provided");
        }
        if (metaModelDAO == null)
        {
            throw new DictionaryException("Meta Model DAO has not been provided");
        }
        createNamespaces();
        createPropertyTypes();
        createTestModel();
        CodeMonkey.todo("Load up additional (user-defined) types here"); // TODO
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
//        // Create Test Referencable Aspect
//        M2Aspect referenceAspect = metaModelDAO.createAspect(QName.createQName(NamespaceService.ACTIVITI_URI, "referenceable"));
//        M2Property idProp = referenceAspect.createProperty("id");
//        idProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.GUID));
//        idProp.setMandatory(true);
//        idProp.setProtected(false);
//        idProp.setMultiValued(false);
        CodeMonkey.issue("We persist the node ID as a native property - is Referencable required?"); // TODO
        
        // Create content aspect
        M2Aspect contentAspect = metaModelDAO.createAspect(ASPECT_QNAME_CONTENT);
        M2Property encodingProp = contentAspect.createProperty("encoding");
        encodingProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        encodingProp.setMandatory(true);
        encodingProp.setMultiValued(false);
        M2Property mimetypeProp = contentAspect.createProperty("mimetype");
        mimetypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        mimetypeProp.setMandatory(true);
        mimetypeProp.setMultiValued(false);

        // Create Test Base Type
        M2Type baseType = metaModelDAO.createType(TYPE_QNAME_BASE);
//        M2Property primaryTypeProp = baseType.createProperty("primaryType");
//        primaryTypeProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.NAME));
//        primaryTypeProp.setMandatory(true);
//        primaryTypeProp.setProtected(true);
//        primaryTypeProp.setMultiValued(false);
//        M2Property aspectsProp = baseType.createProperty("aspects");
//        aspectsProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.NAME));
//        aspectsProp.setMandatory(false);
//        aspectsProp.setProtected(true);
//        aspectsProp.setMultiValued(true);
        CodeMonkey.issue("We persist type and aspects as native properties - are these properties required?");

        // Create Reference Type
        M2Type referenceType = metaModelDAO.createType(TYPE_QNAME_REFERENCE);
        referenceType.setSuperClass(baseType);
        M2Property referenceProp = referenceType.createProperty("reference");
        referenceProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT));
        referenceProp.setMandatory(true);
        referenceProp.setMultiValued(false);

        // Create Content Type
        CodeMonkey.todo("Need to add description to the type definition"); // TODO
        M2Type contentType = metaModelDAO.createType(TYPE_QNAME_CONTENT);
        contentType.setSuperClass(baseType);
        contentType.getDefaultAspects().add(contentAspect);
        
        // Create Container Type
        M2Type containerType = metaModelDAO.createType(TYPE_QNAME_CONTAINER);
        containerType.setSuperClass(baseType);
        M2ChildAssociation contentsAssoc = containerType.createChildAssociation("*");
        contentsAssoc.getRequiredToClasses().add(baseType);
        contentsAssoc.setMandatory(false);
        contentsAssoc.setMultiValued(true);

        // Create File Type
        M2Type fileType = metaModelDAO.createType(TYPE_QNAME_FILE);
        fileType.setSuperClass(contentType);
        
        // Create Folder Type
        M2Type folderType = metaModelDAO.createType(TYPE_QNAME_FOLDER);
        folderType.setSuperClass(baseType);
        M2ChildAssociation filesAssoc = containerType.createChildAssociation("*");
        filesAssoc.getRequiredToClasses().add(fileType);
        filesAssoc.setMandatory(false);
        filesAssoc.setMultiValued(false);
        M2ChildAssociation foldersAssoc = containerType.createChildAssociation("*");
        foldersAssoc.getRequiredToClasses().add(fileType);
        foldersAssoc.setMandatory(false);
        foldersAssoc.setMultiValued(false);
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
        M2Type baseType = metaModelDAO.createType(TYPE_QNAME_BASE);
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
        M2Type fileType = metaModelDAO.createType(TEST_TYPE_QNAME_FILE);
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
        M2Type folderType = metaModelDAO.createType(TEST_TYPE_QNAME_FOLDER);
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
