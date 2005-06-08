/*
 * Created on Mar 30, 2005
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.IOException;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.AbstractResultSet;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.ResultSetRowIterator;
import org.alfresco.repo.search.SearcherException;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Searcher;

/**
 * Implementation of a ResultSet on top of Lucene Hits class.
 * 
 * @author andyh
 * 
 */
public class LuceneResultSet extends AbstractResultSet
{
    /**
     * The underlying hits
     */
    Hits hits;

    /**
     * The store against this result set applies. We do not have to store this
     * in the index - but we may
     */
    StoreRef storeRef;

    private Searcher searcher;
    
    private NodeService nodeService;

    /**
     * Wrap a lucene seach result with node support
     * 
     * @param storeRef
     * @param hits
     */
    public LuceneResultSet(StoreRef storeRef, Hits hits, Searcher searcher, NodeService nodeService, Path[]propertyPaths)
    {
        super(propertyPaths);
        this.hits = hits;
        this.storeRef = storeRef;
        this.searcher = searcher;
        this.nodeService = nodeService;
    }

    /*
     * ResultSet implementation
     */

    public ResultSetRowIterator iterator()
    {
        return new LuceneResultSetRowIterator(this);
    }

    public int length()
    {
        return hits.length();
    }

    public NodeRef getNodeRef(int n)
    {
        try
        {
            // We have to get the document to resolve this
            // It is possible the store ref is also stored in the index
            Document doc = hits.doc(n);
            String id = doc.get("ID");
            return new NodeRef(storeRef, id);
        }
        catch (IOException e)
        {
            throw new SearcherException("IO Error reading reading node ref from the result set", e);
        }
    }

    public float getScore(int n) throws SearcherException
    {
        try
        {
            return hits.score(n);
        }
        catch (IOException e)
        {
            throw new SearcherException("IO Error reading score from the result set", e);
        }
    }

    public Document getDocument(int n)
    {
        try
        {
            Document doc = hits.doc(n);
            return doc;
        }
        catch (IOException e)
        {
            throw new SearcherException("IO Error reading reading document from the result set", e);
        }
    }

    public void close()
    {
        try
        {
            searcher.close();
        }
        catch (IOException e)
        {
            throw new SearcherException(e);
        }
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }

    public ResultSetRow getRow(int i)
    {
        if(i < length())
        {
           return new LuceneResultSetRow(this, i);
        }
        else
        {
            throw new SearcherException("Invalid row");
        }
    }

    public ChildAssocRef getChildAssocRef(int n)
    {
       return getRow(n).getChildAssocRef();
    }
}
