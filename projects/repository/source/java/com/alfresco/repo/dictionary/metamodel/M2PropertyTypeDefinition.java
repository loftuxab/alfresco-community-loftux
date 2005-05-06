/*
 * Created on 26-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.dictionary.metamodel;

import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.ref.QName;

public class M2PropertyTypeDefinition implements PropertyTypeDefinition
{

    private M2PropertyType m2PropertyType;
    
    /**
     * Construct read-only Property Definition
     * 
     * @param m2Property  property definition
     * @return  read-only property definition
     */
    public static M2PropertyTypeDefinition create(M2PropertyType m2PropertyType)
    {
        return new M2PropertyTypeDefinition(m2PropertyType);
    }
    
    /*package*/ M2PropertyTypeDefinition(M2PropertyType m2PropertyType)
    {
        this.m2PropertyType = m2PropertyType;
        
        // Force load-on-demand of related entities
       
        // There are non here
    }

    public QName getName()
    {
        return m2PropertyType.getQName();
    }

    public String getAnalyserClassName()
    {
       return m2PropertyType.getAnalyserClassName();
    }

    
}
