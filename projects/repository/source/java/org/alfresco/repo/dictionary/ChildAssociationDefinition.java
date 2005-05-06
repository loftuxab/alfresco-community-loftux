package org.alfresco.repo.dictionary;

/**
 * Read-only definition of a Child Association.
 * 
 * @author David Caruana
 *
 */
public interface ChildAssociationDefinition extends AssociationDefinition
{
    /**
     * @return the class definition of the default child type
     */
    public ClassDefinition getDefaultType();

}
