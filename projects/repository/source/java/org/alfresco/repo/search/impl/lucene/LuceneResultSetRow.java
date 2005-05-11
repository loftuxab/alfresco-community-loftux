/*
 * Created on Mar 30, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.AbstractResultSetRow;
import org.apache.lucene.document.Document;

/**
 * A row ina result set. Created on the fly.
 * 
 * @author andyh
 * 
 */
public class LuceneResultSetRow extends AbstractResultSetRow
{
    

    /**
     * The current document - cached so we do not get it for each value
     */
    private Document document;
    
    private Map<Path, Serializable> properties;

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
            document = ((LuceneResultSet)getResultSet()).getDocument(getIndex());
        }
        return document;
    }

    /*
     * ResultSetRow implementation
     */

    public Map<Path, Serializable> getValues()
    {
        if(properties == null)
        {
            properties = new LinkedHashMap<Path, Serializable>();
            LuceneResultSet lrs = (LuceneResultSet)getResultSet();
            Map<QName, Serializable> byQname = lrs.getNodeService().getProperties(lrs.getNodeRef(getIndex()));
            for(QName qname: byQname.keySet())
            {
                Serializable value = byQname.get(qname);
                Path path = new Path();
                path.append(new Path.SelfElement());
                path.append(new Path.AttributeElement(qname));
                properties.put(path, value);
            }
        }
        return Collections.unmodifiableMap(properties);
    }

    public Serializable getValue(Path path)
    {
        // TODO: implement path base look up against the document or via the
        // node service
        throw new UnsupportedOperationException();
    }

  

    public Serializable getValue(QName qname)
    {
        Path path = new Path();
        path.append(new Path.SelfElement());
        path.append(new Path.AttributeElement(qname));
       return getValues().get(path);
    }
    
 

   public QName getQName()
   {
      String qname = getDocument().getField("QNAME").stringValue();
      return QName.createQName(qname);
   }
    
    

}
