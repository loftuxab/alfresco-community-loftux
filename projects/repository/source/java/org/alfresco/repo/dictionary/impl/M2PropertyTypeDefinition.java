/*
 * Created on 26-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.dictionary.impl;

import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Property Type Definition
 * 
 * @author David Caruana
 *
 */
/*package*/ class M2PropertyTypeDefinition implements PropertyTypeDefinition
{
    private QName name;
    private M2PropertyType propertyType;
    
    
    /*package*/ M2PropertyTypeDefinition(M2PropertyType propertyType, NamespacePrefixResolver resolver)
    {
        this.name = QName.createQName(propertyType.getName(), resolver);
        this.propertyType = propertyType;
    }


    /*package*/ void resolveDependencies(ModelQuery query)
    {
        // Ensure java class has been specified
        String javaClass = propertyType.getJavaClassName();
        if (javaClass == null)
        {
            throw new DictionaryException("Java class of property type " + name.toPrefixString() + " must be specified");
        }
        
        // Ensure java class is valid and referencable
        try
        {
            Class.forName(javaClass);
        }
        catch (ClassNotFoundException e)
        {
            throw new DictionaryException("Java class " + javaClass + " of property type " + name.toPrefixString() + " is invalid", e);
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getName()
     */
    public QName getName()
    {
        return name;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getTitle()
     */
    public String getTitle()
    {
        return propertyType.getTitle();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getDescription()
     */
    public String getDescription()
    {
        return propertyType.getDescription();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getAnalyserClassName()
     */
    public String getAnalyserClassName()
    {
       return propertyType.getAnalyserClassName();
    }


    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.PropertyTypeDefinition#getJavaClassName()
     */
    public String getJavaClassName()
    {
        return propertyType.getJavaClassName();
    }
    
}
