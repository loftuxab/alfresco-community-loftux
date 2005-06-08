/*
 * Created on 07-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.search.AbstractResultSet;
import org.alfresco.repo.search.ResultSetRow;

public class ChildAssocRefResultSet extends AbstractResultSet
{
    private List<ChildAssocRef> cars;
    NodeService nodeService;
    
    public ChildAssocRefResultSet(NodeService nodeService, List<ChildAssocRef> cars, Path[] propertyPaths)
    {
        super(propertyPaths);
        this.nodeService = nodeService;
        this.cars = cars;
    }
    
    public ChildAssocRefResultSet(NodeService nodeService, List<NodeRef> nodeRefs, Path[] propertyPaths, boolean resolveAllParents)
    {
        super(propertyPaths);
        this.nodeService = nodeService;
        List<ChildAssocRef> cars = new ArrayList<ChildAssocRef>(nodeRefs.size());
        for(NodeRef nodeRef : nodeRefs)
        {
            if(resolveAllParents)
            {
                cars.addAll(nodeService.getParentAssocs(nodeRef));
            }
            else
            {
                cars.add(nodeService.getPrimaryParent(nodeRef));
            }
        }
        this.cars = cars;
    }

    public int length()
    {
        return cars.size();
    }

    public NodeRef getNodeRef(int n)
    {
        return cars.get(n).getChildRef();
    }
    
    public ChildAssocRef getChildAssocRef(int n)
    {
        return cars.get(n);
    }

    public ResultSetRow getRow(int i)
    {
        return new ChildAssocRefResultSetRow(this, i);
    }

    public Iterator<ResultSetRow> iterator()
    {
        return new ChildAssocRefResultSetRowIterator(this);
    }
    
    public NodeService getNodeService()
    {
        return nodeService;
    }

}
