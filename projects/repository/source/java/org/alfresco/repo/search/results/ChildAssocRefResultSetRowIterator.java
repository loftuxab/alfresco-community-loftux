/*
 * Created on 08-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.results;

import org.alfresco.repo.search.AbstractResultSetRowIterator;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;

public class ChildAssocRefResultSetRowIterator extends AbstractResultSetRowIterator
{

    public ChildAssocRefResultSetRowIterator(ResultSet resultSet)
    {
        super(resultSet);
    }

    @Override
    public ResultSetRow next()
    {
       return new ChildAssocRefResultSetRow((ChildAssocRefResultSet)getResultSet(), moveToNextPosition());
    }

    @Override
    public ResultSetRow previous()
    {
        return new ChildAssocRefResultSetRow((ChildAssocRefResultSet)getResultSet(), moveToPreviousPosition());
    }

}
