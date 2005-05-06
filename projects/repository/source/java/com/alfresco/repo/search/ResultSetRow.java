/*
 * Created on Mar 24, 2005
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;

/**
 * A row in a result set
 * 
 * TODO: Support for other non attribute features such as parents and path
 * 
 * @author andyh
 * 
 */
public interface ResultSetRow
{
    /**
     * Get the values of all available node properties
     * 
     * @return
     */
    public Value[] getValues();

    /**
     * Get a node property by path
     * 
     * @param path
     * @return
     */
    public Value getValue(Path path);

    /**
     * Get a node value by name
     * 
     * @param qname
     * @return
     */
    public Value getValue(QName qname);

    /**
     * The refernce to the node that equates to this row in the result set
     * 
     * @return
     */
    public NodeRef getNodeRef();

    /**
     * Get the score for this row in the result set
     * 
     * @return
     */
    public float getScore(); // Score is score + rank + potentially other
                                // stuff

    /**
     * Get the containing result set
     * 
     * @return
     */
    public ResultSet getResultSet();
    
    /**
     * Return the QName of the node in the context in which it was found.
     * @return
     */
    
    public QName getQName();

}
