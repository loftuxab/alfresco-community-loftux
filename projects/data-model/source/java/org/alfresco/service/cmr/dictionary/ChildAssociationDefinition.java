package org.alfresco.service.cmr.dictionary;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Read-only definition of a Child Association.
 * 
 * @author David Caruana
 *
 */
@AlfrescoPublicApi
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

    /**
     * @return  whether timestamps should be propagated upwards along primary associations
     */
    public boolean getPropagateTimestamps();
}
