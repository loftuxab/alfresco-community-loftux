package com.activiti.repo.dictionary.bootstrap;

import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2ChildAssociation;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.dictionary.metamodel.MetaModelDAO;
import com.activiti.repo.ref.QName;

public class DictionaryBootstrap
{

    private MetaModelDAO metaModelDAO = null;
    
    
    public void setMetaModelDAO(MetaModelDAO metaModelDAO)
    {
        this.metaModelDAO = metaModelDAO;
    }
    
    
    public void bootstrap()
    {
        createPropertyTypes();
        createTestModel();
        metaModelDAO.save();
    }
    

    private void createPropertyTypes()
    {
        M2PropertyType stringType = metaModelDAO.createPropertyType(PropertyTypeDefinition.STRING);
        M2PropertyType dateType = metaModelDAO.createPropertyType(PropertyTypeDefinition.DATE);
        M2PropertyType qnameType = metaModelDAO.createPropertyType(PropertyTypeDefinition.QNAME);
        M2PropertyType idType = metaModelDAO.createPropertyType(PropertyTypeDefinition.GUID);
    }
    
    
    private void createTestModel()
    {
        // Create Referencable Aspect
        M2Aspect referenceAspect = metaModelDAO.createAspect(QName.createQName("test", "referenceable"));
        M2Property idProp = referenceAspect.createProperty("id");
        idProp.setType(metaModelDAO.getPropertyType(PropertyTypeDefinition.GUID));
        idProp.setMandatory(true);
        idProp.setProtected(false);
        idProp.setMultiValued(false);
        
        // Create Base Type
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

        // Create File Type
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
        
        // Create Folder Type
        M2Type folderType = metaModelDAO.createType(QName.createQName("test", "folder"));
        folderType.setSuperClass(baseType);
        folderType.getDefaultAspects().add(referenceAspect);
        M2ChildAssociation contentsAssoc = folderType.createChildAssociation("contents");
        contentsAssoc.getRequiredToClasses().add(fileType);
        contentsAssoc.setMandatory(false);
        contentsAssoc.setMultiValued(true);
    }
    
    
}
