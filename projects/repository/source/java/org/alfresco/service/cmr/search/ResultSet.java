/*
 * Created on Mar 24, 2005
 */
package org.alfresco.service.cmr.search;

import java.util.List;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;

/**
 * An iterable result set from a searcher query. TODO: Expose meta data and XML
 * 
 * @author andyh
 * 
 */
public interface ResultSet extends Iterable<ResultSetRow> // Specfic iterator
                                                            // over
                                                            // ResultSetRows
{
    /**
     * Get the relative paths to all the elements contained in this result set
     */
    Path[] getPropertyPaths();

    /**
     * Get the size of the result set
     */
    int length();

    /**
     * Get the id of the node at the given index
     */
    NodeRef getNodeRef(int n);

    /**
     * Get the score for the node at the given position
     */
    float getScore(int n);

    /**
     * Generate the XML form of this result set
     */
    // Dom getXML(int page, int pageSize, boolean includeMetaData);
    /**
     * Generate as XML for Reading
     */
    // Stream getStream(int page, int pageSize, boolean includeMetaData);
    /**
     * toString() as above but for the whole set
     */
    // String toString();
    // ResultSetMetaData getMetaData();
    
    void close();
    
    ResultSetRow getRow(int i);
    
    List<NodeRef> getNodeRefs();
    
    List<ChildAssociationRef> getChildAssocRefs();
    
    ChildAssociationRef getChildAssocRef(int n);
}
