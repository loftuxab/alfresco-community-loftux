package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.ref.QName;


public class M2PropertyDefinition implements PropertyDefinition
{

    private M2Property m2Property;
    
    
    public static M2PropertyDefinition create(M2Property m2Property)
    {
        return new M2PropertyDefinition(m2Property);
    }
    
    
    /*package*/ M2PropertyDefinition(M2Property m2Property)
    {
        this.m2Property = m2Property;
        
        // Force load-on-demand of related entities
        this.m2Property.getType();
        this.m2Property.getContainerClass();
    }


    public QName getName()
    {
        return getReference().getQName();
    }

    
    public PropertyRef getReference()
    {
        return m2Property.getReference();
    }


    public QName getPropertyType()
    {
        return m2Property.getType().getName();
    }


    public ClassRef getContainerClass()
    {
        return m2Property.getContainerClass().getReference();
    }


    public boolean isMultiValued()
    {
        return m2Property.isMultiValued();
    }


    public boolean isMandatory()
    {
        return m2Property.isMandatory();
    }


    public boolean isProtected()
    {
        return m2Property.isProtected();
    }


    public boolean isIndexed()
    {
        return m2Property.isIndexed();
    }


    public boolean isStoredInIndex()
    {
        return m2Property.isStoredInIndex();
    }


    public String getIndexTokeniserClassName()
    {
        return m2Property.getIndexTokeniserClassName();
    }
    
}
