package org.alfresco.solr.query;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;


/**
 * @author andyh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CachingTermPositions extends DocsAndPositionsEnum
{
    int[] results;

    int position = -1;

    int last = -1;

    DocsAndPositionsEnum delegate;

    public CachingTermPositions(DocsAndPositionsEnum delegate)
    {
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.index.TermPositions#nextPosition()
     */
    public int nextPosition() throws IOException
    {
        if (results == null)
        {
            results = new int[freq()];
        }
        position++;
        if (last < position)
        {
            results[position] = delegate.nextPosition();
            last = position;
        }
        return results[position];

    }

    public void reset()
    {
        position = -1;
    }

    private void clear()
    {
        position = -1;
        last = -1;
        results = null;
    }

    /**
     * @return int
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return delegate.hashCode();
    }

    /**
     * @return int
     * @throws IOException
     * @see org.apache.lucene.index.DocsEnum#freq()
     */
    public int freq() throws IOException
    {
        return delegate.freq();
    }

    /**
     * @return int
     * @throws IOException
     * @see org.apache.lucene.index.DocsAndPositionsEnum#startOffset()
     */
    public int startOffset() throws IOException
    {
        return delegate.startOffset();
    }

    /**
     * @return int
     * @throws IOException
     * @see org.apache.lucene.index.DocsAndPositionsEnum#endOffset()
     */
    public int endOffset() throws IOException
    {
        return delegate.endOffset();
    }

    /**
     * @return int
     * @see org.apache.lucene.search.DocIdSetIterator#docID()
     */
    public int docID()
    {
        return delegate.docID();
    }

    /**
     * @return BytesRef
     * @throws IOException
     * @see org.apache.lucene.index.DocsAndPositionsEnum#getPayload()
     */
    public BytesRef getPayload() throws IOException
    {
        return delegate.getPayload();
    }

    /**
     * @return AttributeSource
     * @see org.apache.lucene.index.DocsEnum#attributes()
     */
    public AttributeSource attributes()
    {
        return delegate.attributes();
    }

    /**
     * @return int
     * @throws IOException
     * @see org.apache.lucene.search.DocIdSetIterator#nextDoc()
     */
    public int nextDoc() throws IOException
    {
        int nextDoc = delegate.nextDoc();
        clear();
        return nextDoc;
    }

    /**
     * @param target int
     * @return int
     * @throws IOException
     * @see org.apache.lucene.search.DocIdSetIterator#advance(int)
     */
    public int advance(int target) throws IOException
    {
        int nextDoc = delegate.advance(target);
        clear();
        return nextDoc;
    }

    /**
     * @param obj Object
     * @return boolean
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return delegate.equals(obj);
    }

    /**
     * @return long
     * @see org.apache.lucene.search.DocIdSetIterator#cost()
     */
    public long cost()
    {
        return delegate.cost();
    }

    /**
     * @return String
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return delegate.toString();
    }
    
}