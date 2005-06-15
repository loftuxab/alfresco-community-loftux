/*
 * Created on Mar 30, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.AbstractResultSetRow;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.QName;
import org.apache.lucene.document.Document;

/**
 * A row ina result set. Created on the fly.
 * 
 * @author andyh
 * 
 */
public class LuceneResultSetRow extends AbstractResultSetRow
{
    public static final QName ASSOC_TYPE_QNAME = ContentModel.ASSOC_CONTAINS;
    
    /**
     * The current document - cached so we do not get it for each value
     */
    private Document document;

    /**
     * Wrap a position in a lucene Hits class with node support
     * 
     * @param resultSet
     * @param position
     */
    public LuceneResultSetRow(LuceneResultSet resultSet, int index)
    {
        super(resultSet, index);
    }

    /**
     * Support to cache the document for this row
     * 
     * @return
     */
    public Document getDocument()
    {
        if (document == null)
        {
            document = ((LuceneResultSet) getResultSet()).getDocument(getIndex());
        }
        return document;
    }

    /*
     * ResultSetRow implementation
     */

    protected Map<QName, Serializable> getDirectProperties()
    {
        LuceneResultSet lrs = (LuceneResultSet) getResultSet();
        return lrs.getNodeService().getProperties(lrs.getNodeRef(getIndex()));
    }

    public Serializable getValue(Path path)
    {
        // TODO: implement path base look up against the document or via the
        // node service
        throw new UnsupportedOperationException();
    }

    public QName getQName()
    {
        String qname = getDocument().getField("QNAME").stringValue();
        return QName.createQName(qname);
    }

    public ChildAssociationRef getChildAssocRef()
    {
        String primaryParent = getDocument().getField("PRIMARYPARENT").stringValue();
        NodeRef childNodeRef = getNodeRef();
        NodeRef paretnNodeRef = new NodeRef(childNodeRef.getStoreRef(), primaryParent);
        return new ChildAssociationRef(ASSOC_TYPE_QNAME, paretnNodeRef, getQName(), childNodeRef);
    }

}
