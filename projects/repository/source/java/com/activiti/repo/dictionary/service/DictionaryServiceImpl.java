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
import com.activiti.repo.node.InvalidNodeRefException;


public class DictionaryServiceImpl implements DictionaryService
{

    private MetaModelDAO metaModelDAO;

    
    public void setMetaModelDAO(MetaModelDAO metaModelDAO)
    {
        this.metaModelDAO = metaModelDAO;
    }

    
    public Collection getTypes()
    {
        Collection qnames = metaModelDAO.getTypes();
        Collection ddrefs = M2References.createQNameClassRefCollection(qnames);
        return ddrefs;
    }


    public ClassDefinition getClass(ClassRef classRef)
    {
        M2Class m2Class = metaModelDAO.getClass(classRef.getQName());
        if (m2Class == null)
        {
            throw new InvalidNodeRefException(classRef);
        }
        
        return m2Class.getClassDefinition();
    }

    
    public TypeDefinition getType(ClassRef typeRef)
    {
        M2Type m2Type = metaModelDAO.getType(typeRef.getQName());
        if (m2Type == null)
        {
            throw new InvalidNodeRefException(typeRef);
        }
        
        return (TypeDefinition)m2Type.getClassDefinition();
    }


    public AspectDefinition getAspect(ClassRef aspectRef)
    {
        M2Aspect m2Aspect = metaModelDAO.getAspect(aspectRef.getQName());
        if (m2Aspect == null)
        {
            throw new InvalidNodeRefException(aspectRef);
        }
        
        return (AspectDefinition)m2Aspect.getClassDefinition();
    }
    
    
    public PropertyDefinition getProperty(PropertyRef propertyRef)
    {
        M2Property m2Property = metaModelDAO.getProperty(propertyRef.getClassRef().getQName(), propertyRef.getPropertyName());
        if (m2Property == null)
        {
            throw new InvalidNodeRefException(propertyRef);
        }
        
        return m2Property.getPropertyDefinition();
    }
    
}
