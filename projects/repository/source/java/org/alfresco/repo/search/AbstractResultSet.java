/*
 * Created on 08-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;

public abstract class AbstractResultSet implements ResultSet
{

    private Path[] propertyPaths;
    
    public AbstractResultSet(Path[] propertyPaths)
    {
        super();
        this.propertyPaths = propertyPaths;
    }

    public Path[] getPropertyPaths()
    {
        return propertyPaths;
    }

  
    public float getScore(int n)
    {
        // All have equal weight by default
        return 1.0f;
    }

    public void close()
    {
        // default to do nothing
    }

    public List<NodeRef> getNodeRefs()
    {
        ArrayList<NodeRef> nodeRefs = new ArrayList<NodeRef>(length());
        for(ResultSetRow row: this)
        {
            nodeRefs.add(row.getNodeRef());
        }
        return nodeRefs;
    }

    public List<ChildAssociationRef> getChildAssocRefs()
    {
        ArrayList<ChildAssociationRef> cars = new ArrayList<ChildAssociationRef>(length());
        for(ResultSetRow row: this)
        {
            cars.add(row.getChildAssocRef());
        }
        return cars;
    }

  

}
