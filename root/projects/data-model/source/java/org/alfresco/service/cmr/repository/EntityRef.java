package org.alfresco.service.cmr.repository;

import java.io.Serializable;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * A marker interface for entity reference classes.
 * <p>
 * This is used primarily as a means of ensuring type safety in collections
 * of mixed type references.
 * 
 * @see org.alfresco.service.cmr.repository.NodeService#removeChild(NodeRef, NodeRef)
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public interface EntityRef extends Serializable
{
}
