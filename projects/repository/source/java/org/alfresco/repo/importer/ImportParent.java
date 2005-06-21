package org.alfresco.repo.importer;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Description of parent for node to import.
 * 
 * @author David Caruana
 *
 */
public interface ImportParent
{
    /**
     * @return  the parent ref
     */    
    /*package*/ NodeRef getParentRef();
    
    /**
     * @return  the child association type
     */
    /*package*/ QName getAssocType();
    
}
