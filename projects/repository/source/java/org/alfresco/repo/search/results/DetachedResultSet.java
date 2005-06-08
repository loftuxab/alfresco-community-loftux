/*
 * Created on 08-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.search.AbstractResultSet;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;

public class DetachedResultSet extends AbstractResultSet
{
    List<ResultSetRow> rows = new ArrayList<ResultSetRow>();
    
    public DetachedResultSet(ResultSet resultSet, Path[] propertyPaths)
    {
        super(propertyPaths);
        for(ResultSetRow row : resultSet)
        {
            rows.add(new DetachedResultSetRow(this, row));
        }
    }

    public int length()
    {
        return rows.size();
    }

    public NodeRef getNodeRef(int n)
    {
        return rows.get(n).getNodeRef();
    }

    public ResultSetRow getRow(int i)
    {
        return rows.get(i);
    }

    public Iterator<ResultSetRow> iterator()
    {
       return rows.iterator();
    }

    public ChildAssocRef getChildAssocRef(int n)
    {
        return rows.get(n).getChildAssocRef();
    }

}
