package com.activiti.repo.dictionary.service;

import java.util.Collection;

import com.activiti.repo.dictionary.AspectDefinition;
import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2Class;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2References;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.dictionary.metamodel.MetaModelDAO;


/**
 * Data Dictionary Service Implementation
 * 
 * @author David Caruana
 */
public class DictionaryServiceImpl implements DictionaryService
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

    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.DictionaryService#getTypes()
     */
    public Collection getTypes()
    {
        Collection qnames = metaModelDAO.getTypes();
        Collection ddrefs = M2References.createQNameClassRefCollection(qnames);
        return ddrefs;
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.DictionaryService#getClass(com.activiti.repo.dictionary.ClassRef)
     */
    public ClassDefinition getClass(ClassRef classRef)
    {
        M2Class m2Class = metaModelDAO.getClass(classRef.getQName());
        return m2Class == null ? null : m2Class.getClassDefinition();
    }

    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.DictionaryService#getType(com.activiti.repo.dictionary.ClassRef)
     */
    public TypeDefinition getType(ClassRef typeRef)
    {
        M2Type m2Type = metaModelDAO.getType(typeRef.getQName());
        return m2Type == null ? null : (TypeDefinition)m2Type.getClassDefinition();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.DictionaryService#getAspect(com.activiti.repo.dictionary.ClassRef)
     */
    public AspectDefinition getAspect(ClassRef aspectRef)
    {
        M2Aspect m2Aspect = metaModelDAO.getAspect(aspectRef.getQName());
        return m2Aspect == null ? null : (AspectDefinition)m2Aspect.getClassDefinition();
    }
    
    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.DictionaryService#getProperty(com.activiti.repo.dictionary.PropertyRef)
     */
    public PropertyDefinition getProperty(PropertyRef propertyRef)
    {
        M2Property m2Property = metaModelDAO.getProperty(propertyRef.getClassRef().getQName(), propertyRef.getPropertyName());
        return m2Property == null ? null : m2Property.getPropertyDefinition();
    }
    
}
