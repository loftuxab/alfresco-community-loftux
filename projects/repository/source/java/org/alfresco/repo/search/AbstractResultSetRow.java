/*
 * Created on 06-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.ref.NodeRef;

public abstract class AbstractResultSetRow implements ResultSetRow
{

    /**
     * The containing result set
     */
    private ResultSet resultSet;
    
    /**
     * The current position in the containing result set
     */
    private int index;

    
    public AbstractResultSetRow(ResultSet resultSet, int index)
    {
        super();
        this.resultSet = resultSet;
        this.index = index;
    }

    public ResultSet getResultSet()
    {
        return resultSet;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public NodeRef getNodeRef()
    {
        return getResultSet().getNodeRef(getIndex());
    }

    public float getScore()
    {
        return getResultSet().getScore(getIndex());
    }

    
}
