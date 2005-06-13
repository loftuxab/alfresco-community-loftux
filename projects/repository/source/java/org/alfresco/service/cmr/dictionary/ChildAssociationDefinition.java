package org.alfresco.service.cmr.dictionary;

/**
 * Read-only definition of a Child Association.
 * 
 * @author David Caruana
 *
 */
public interface ChildAssociationDefinition extends AssociationDefinition
{

    /**
     * @return the required name of children (or null if none)
     */
    public String getRequiredChildName();
    
    /**
     * @return whether duplicate child names allowed within this association? 
     */
    public boolean getDuplicateChildNamesAllowed();

}
