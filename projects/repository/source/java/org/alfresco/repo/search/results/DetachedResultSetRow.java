/*
 * Created on 08-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.results;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.AbstractResultSetRow;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;

public class DetachedResultSetRow extends AbstractResultSetRow
{
    private ChildAssocRef car;
    private Map<Path, Serializable> properties;
    
    public DetachedResultSetRow(ResultSet resultSet, ResultSetRow row)
    {
        super(resultSet, row.getIndex());
        car = row.getChildAssocRef();
        properties = row.getValues();
    }

    public Serializable getValue(Path path)
    {
        return properties.get(path);
    }

    public QName getQName()
    {
        return car.getQName();
    }

    public NodeRef getNodeRef()
    {
        return car.getChildRef();
    }

    public Map<Path, Serializable> getValues()
    {
        return properties;
    }

    public ChildAssocRef getChildAssocRef()
    {
        return car;
    }
    
    

}
