package com.activiti.repo.dictionary;


/**
 * Read-only definition of a Child Association.
 * 
 * @author David Caruana
 *
 */
public interface ChildAssociationDefinition extends AssociationDefinition
{
    /**
     * Gets the default child type 
     * 
     * @return  the class reference of the child type
     */
    public ClassRef getDefaultType();

}
