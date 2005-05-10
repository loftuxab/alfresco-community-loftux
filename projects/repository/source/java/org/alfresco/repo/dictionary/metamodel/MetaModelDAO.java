package org.alfresco.repo.dictionary.metamodel;

import java.util.Collection;

import org.alfresco.repo.ref.QName;


/**
 * Meta Model DAO Interface.
 * 
 * This DAO is responsible for retrieving and creating Meta model definitions.
 * 
 * @author David Caruana
 */
public interface MetaModelDAO
{

    /**
     * Gets a collection of all defined Type names.
     * 
     * @return  type names
     */
    public Collection<QName> getTypes();

    /**
     * Gets a Class
     * 
     * @param className  qualified name of class to retrieve
     * @return  the class (or null if not found)
     */
    public M2Class getClass(QName className);

    /**
     * Gets a Type
     * 
     * @param typeName  qualified name of class to retrieve
     * @return  the type (or null if not found)
     */    
    public M2Type getType(QName typeName);

    /**
     * Gets an Aspect
     * 
     * @param aspectName  qualified name of aspect to retrieve
     * @return  the aspect (or null if not found)
     */
    public M2Aspect getAspect(QName aspectName);

    /**
     * Gets a Property
     * 
     * @param className  qualified name of owning class
     * @param propertyName  name of property
     * @return  the property
     */
    public M2Property getProperty(QName className, String propertyName);
    
    public M2Property getProperty(QName propertyName);

    /**
     * Gets a Property Type
     * 
     * @param propertyType  qualified name of property
     * @return  the property type
     */
    public M2PropertyType getPropertyType(QName propertyType);
        
    /**
     * Create a new Type
     * 
     * @param typeName  name to provide Type
     * @return  the type
     */    
    public M2Type createType(QName typeName);
    
    /**
     * Create a new Aspect
     * 
     * @param aspectName  name to provide Aspect
     * @return  the aspect
     */
    public M2Aspect createAspect(QName aspectName);
    
    /**
     * Create a new Property Type
     * 
     * @param typeName  name to provide property type
     * @return  the property type
     */
    public M2PropertyType createPropertyType(QName typeName);

    public Collection<QName> getAspects();
        
}
