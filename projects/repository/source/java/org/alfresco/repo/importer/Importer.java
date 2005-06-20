package org.alfresco.repo.importer;

import java.io.InputStream;
import java.util.Properties;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * This interface represents the contract between the importer service and an 
 * importer (which is responsible for parsing and creating nodes).
 *  
 * @author David Caruana
 */
public interface Importer
{
    /**
     * Import node from the specified input stream into the specified store location.
     * 
     * @param inputStream  the input stream containing the xml to parse
     * @param parentRef  the parent node to place nodes into
     * @param childAssocType  the child association type to place children under
     * @param configuration  property values used for binding property place holders in import stream
     * @param progress  progress monitor (may be null)
     */
    public void importNodes(InputStream inputStream, NodeRef parentRef, QName childAssocType, Properties configuration, Progress progress);

}
