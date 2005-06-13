/*
 * Created on 14-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;

public class EmptyResultSet implements ResultSet
{

    public EmptyResultSet()
    {
        super();
    }

    public Path[] getPropertyPaths()
    {
       return new Path[]{};
    }

    public int length()
    {
        return 0;
    }

    public NodeRef getNodeRef(int n)
    {
        throw new UnsupportedOperationException();
    }

    public float getScore(int n)
    {
        throw new UnsupportedOperationException();
    }

    public Iterator<ResultSetRow> iterator()
    {
        ArrayList<ResultSetRow> dummy = new ArrayList<ResultSetRow>(0);
        return dummy.iterator();
    }

    public void close()
    {

    }

    public ResultSetRow getRow(int i)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public List<NodeRef> getNodeRefs()
    {
        return Collections.<NodeRef>emptyList();
    }

    public List<ChildAssociationRef> getChildAssocRefs()
    {
        return Collections.<ChildAssociationRef>emptyList();
    }

    public ChildAssociationRef getChildAssocRef(int n)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
