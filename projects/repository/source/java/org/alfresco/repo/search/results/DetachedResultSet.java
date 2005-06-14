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

import org.alfresco.repo.search.AbstractResultSet;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;

public class DetachedResultSet extends AbstractResultSet
{
    List<ResultSetRow> rows = null;
    
    public DetachedResultSet(ResultSet resultSet, Path[] propertyPaths)
    {
        super(propertyPaths);
        rows = new ArrayList<ResultSetRow>(resultSet.length());
        for (ResultSetRow row : resultSet)
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

    public ChildAssociationRef getChildAssocRef(int n)
    {
        return rows.get(n).getChildAssocRef();
    }

}
