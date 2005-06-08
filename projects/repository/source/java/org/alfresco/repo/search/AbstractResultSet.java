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

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;

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

    public List<ChildAssocRef> getChildAssocRefs()
    {
        ArrayList<ChildAssocRef> cars = new ArrayList<ChildAssocRef>(length());
        for(ResultSetRow row: this)
        {
            cars.add(row.getChildAssocRef());
        }
        return cars;
    }

  

}
