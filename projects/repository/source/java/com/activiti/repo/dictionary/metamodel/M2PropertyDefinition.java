package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.ref.QName;


/**
 * Default Read-only Property Definition implementation
 * 
 * @author David Caruana
 */
public class M2PropertyDefinition implements PropertyDefinition
{
    /**
     * Property definition to wrap
     */
    private M2Property m2Property;
    
    
    /**
     * Construct read-only Property Definition
     * 
     * @param m2Property  property definition
     * @return  read-only property definition
     */
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


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getName()
     */
    public QName getName()
    {
        return getReference().getQName();
    }

    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getReference()
     */
    public PropertyRef getReference()
    {
        return m2Property.getReference();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getPropertyType()
     */
    public QName getPropertyType()
    {
        return m2Property.getType().getName();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getContainerClass()
     */
    public ClassRef getContainerClass()
    {
        return m2Property.getContainerClass().getReference();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#isMultiValued()
     */
    public boolean isMultiValued()
    {
        return m2Property.isMultiValued();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#isMandatory()
     */
    public boolean isMandatory()
    {
        return m2Property.isMandatory();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#isProtected()
     */
    public boolean isProtected()
    {
        return m2Property.isProtected();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#isIndexed()
     */
    public boolean isIndexed()
    {
        return m2Property.isIndexed();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#isStoredInIndex()
     */
    public boolean isStoredInIndex()
    {
        return m2Property.isStoredInIndex();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getIndexTokeniserClassName()
     */
    public String getIndexTokeniserClassName()
    {
        return m2Property.getIndexTokeniserClassName();
    }
    
}
