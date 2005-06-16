package org.alfresco.repo.importer;

import java.io.InputStream;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;


/**
 * Importer Service.  Entry point for importing xml data sources into the Repository.
 * 
 * @author David Caruana
 *
 */
public interface ImporterService
{

    /**
     * Import Nodes into the specified parent and child association type
     * 
     * @param inputStream  input stream containing xml to parse
     * @param parentRef  the parent node to place items under
     * @param childAssocType  the child association type to place items under
     * @param progress  progress monitor (maybe null)
     */
    public void importNodes(InputStream inputStream, NodeRef parentRef, QName childAssocType, ImporterProgress progress);

    /**
     * Import Nodes into the specified parent (the child association type is determined
     * by the import).  An exception is thrown if multiple child association types exist
     * for the specified parent.
     * 
     * @param inputStream  input stream containing xml to parse
     * @param parentRef  the parent node to place items under
     * @param progress  progress monitor (maybe null)
     */
    public void importNodes(InputStream inputStream, NodeRef parentRef, ImporterProgress progress);

    /**
     * Import Nodes into the root of the specified store
     * 
     * @param inputStream  input stream containing xml to parse
     * @param storeRef  the store to place items into
     * @param progress  progress monitor (maybe null)
     */
    public void importNodes(InputStream inputStream, StoreRef storeRef, ImporterProgress progress);

    /**
     * Import Nodes into the path of the specified store
     *  
     * @param inputStream  input stream containing xml to parse
     * @param storeRef  the store to place items into
     * @param parentPath  the path within the store to place items into
     * @param childAssocType  the child association type to place items under
     * @param progress  progress monitor (maybe null)
     */
    public void importNodes(InputStream inputStream, StoreRef storeRef, String parentPath, QName childAssocType, ImporterProgress progress);

}
