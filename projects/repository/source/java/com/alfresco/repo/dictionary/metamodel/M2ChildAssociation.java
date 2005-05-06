package org.alfresco.repo.dictionary.metamodel;


/**
 * Child Association Definition
 * 
 * @author David Caruana
 *
 */
public interface M2ChildAssociation extends M2Association
{
    
    public M2Type getDefaultType();
    
    public void setDefaultType(M2Type defaultType);
    
}
