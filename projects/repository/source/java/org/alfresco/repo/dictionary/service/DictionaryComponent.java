package org.alfresco.repo.dictionary.service;

import java.util.Collection;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyRef;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.dictionary.metamodel.M2Aspect;
import org.alfresco.repo.dictionary.metamodel.M2Class;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.M2PropertyType;
import org.alfresco.repo.dictionary.metamodel.M2References;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.dictionary.metamodel.MetaModelDAO;
import org.alfresco.repo.ref.QName;


/**
 * Data Dictionary Service Implementation
 * 
 * @author David Caruana
 */
public class DictionaryComponent implements DictionaryService
{

    /**
     * Meta Model DAO
     */
    private MetaModelDAO metaModelDAO;


    /**
     * Sets the Meta Model DAO
     * 
     * @param metaModelDAO  meta model DAO
     */
    public void setMetaModelDAO(MetaModelDAO metaModelDAO)
    {
        this.metaModelDAO = metaModelDAO;
    }

    /**
     * @see MetaModelDAO#getTypes()
     */
    public Collection<ClassRef> getTypes()
    {
        Collection<QName> qnames = metaModelDAO.getTypes();
        Collection<ClassRef> classRefs = M2References.createQNameClassRefCollection(qnames);
        return classRefs;
    }

    /**
     * @see MetaModelDAO#getClass(QName)
     */
    public ClassDefinition getClass(ClassRef classRef)
    {
        M2Class m2Class = metaModelDAO.getClass(classRef.getQName());
        return m2Class == null ? null : m2Class.getClassDefinition();
    }

    /**
     * @see MetaModelDAO#getType(QName)
     */
    public TypeDefinition getType(ClassRef typeRef)
    {
        M2Type m2Type = metaModelDAO.getType(typeRef.getQName());
        return m2Type == null ? null : (TypeDefinition)m2Type.getClassDefinition();
    }

    /**
     * @see MetaModelDAO#getAspect(QName)
     */
    public AspectDefinition getAspect(ClassRef aspectRef)
    {
        M2Aspect m2Aspect = metaModelDAO.getAspect(aspectRef.getQName());
        return m2Aspect == null ? null : (AspectDefinition)m2Aspect.getClassDefinition();
    }
    
    /**
     * @see MetaModelDAO#getProperty(QName, String)
     */
    public PropertyDefinition getProperty(PropertyRef propertyRef)
    {
        M2Property m2Property = metaModelDAO.getProperty(propertyRef.getClassRef().getQName(),
                propertyRef.getPropertyName());
        return m2Property == null ? null : m2Property.getPropertyDefinition();
    }

    public PropertyDefinition getProperty(QName property)
    {
        M2Property m2Property = metaModelDAO.getProperty(property);
        return m2Property == null ? null : m2Property.getPropertyDefinition();
    }

    public PropertyTypeDefinition getPropertyType(DictionaryRef propertyTypeRef)
    {
       M2PropertyType m2PropertyType = metaModelDAO.getPropertyType(propertyTypeRef.getQName());
       return m2PropertyType == null ? null : m2PropertyType.getPropertyTypeDefinition();
    }

    public Collection<ClassRef> getAspects()
    {
        Collection<QName> qnames = metaModelDAO.getAspects();
        Collection<ClassRef> classRefs = M2References.createQNameClassRefCollection(qnames);
        return classRefs;
    }
}
