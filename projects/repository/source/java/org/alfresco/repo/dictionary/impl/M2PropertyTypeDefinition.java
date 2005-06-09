/*
 * Created on 26-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;


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
    
}
