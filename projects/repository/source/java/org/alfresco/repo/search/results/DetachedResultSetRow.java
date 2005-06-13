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

import org.alfresco.repo.search.AbstractResultSetRow;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.QName;

public class DetachedResultSetRow extends AbstractResultSetRow
{
    private ChildAssociationRef car;
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

    public ChildAssociationRef getChildAssocRef()
    {
        return car;
    }
    
    

}
