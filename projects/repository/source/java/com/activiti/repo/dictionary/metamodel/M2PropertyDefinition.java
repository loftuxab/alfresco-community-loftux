package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryRef;
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
    
    private PropertyRef propertyRef;
    
    
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
    
    public String toString()
    {
        return getReference().toString();
    }

    /**
     * @see M2PropertyDefinition#getQName(M2Property)
     */
    public QName getQName()
    {
        return M2PropertyDefinition.getQName(m2Property);
    }
    
    /**
     * Builds a fully qualified property name.
     * <p>
     * A property name is not qualified - rather the property is qualified by the
     * class/aspect that it belongs to.
     * 
     * @param m2Property the property for which we want a qualified name
     * @return Returns a fully qualified name of the property supplied
     */
    public static QName getQName(M2Property m2Property)
    {
        QName classQName = m2Property.getContainerClass().getQName();
        String classNamespaceUri = classQName.getNamespaceURI();
        String classLocalName = classQName.getLocalName();
        String localName = classLocalName + DictionaryRef.NAME_SEPARATOR + m2Property.getName();
        QName qname = QName.createQName(classNamespaceUri, localName);
        return qname;
    }
    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getReference()
     */
    public PropertyRef getReference()
    {
        if (propertyRef == null)
        {
            ClassRef classRef = m2Property.getContainerClass().getClassDefinition().getReference();
            propertyRef = new PropertyRef(classRef, m2Property.getName());
        }
        return propertyRef;
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getPropertyType()
     */
    public QName getPropertyType()
    {
        return m2Property.getType().getQName();
    }


    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.PropertyDefinition#getContainerClass()
     */
    public ClassDefinition getContainerClass()
    {
        return m2Property.getContainerClass().getClassDefinition();
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
