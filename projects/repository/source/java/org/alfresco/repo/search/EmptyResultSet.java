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

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;

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

    public List<ChildAssocRef> getChildAssocRefs()
    {
        return Collections.<ChildAssocRef>emptyList();
    }

    public ChildAssocRef getChildAssocRef(int n)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
