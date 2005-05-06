/*
 * Created on Mar 30, 2005
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.search.ResultSetRowIterator;

/**
 * Iterate over the rows in a LuceneResultSet
 * 
 * @author andyh
 * 
 */
public class LuceneResultSetRowIterator implements ResultSetRowIterator
{
    /**
     * The result set
     */
    private LuceneResultSet resultSet;

    /**
     * The current position
     */
    private int position = -1;

    /**
     * The maximum position
     */
    private int max;

    /**
     * Create an iterator over the result set. Follows stadard ListIterator
     * conventions
     * 
     * @param resultSet
     */
    public LuceneResultSetRowIterator(LuceneResultSet resultSet)
    {
        super();
        this.resultSet = resultSet;
        this.max = resultSet.length();
    }

    /*
     * ListIterator implementation
     */
    public boolean hasNext()
    {
        return position < (max - 1);
    }

    public boolean allowsReverse()
    {
        return true;
    }

    public boolean hasPrevious()
    {
        return position > 0;
    }

    public ResultSetRow next()
    {
        return new LuceneResultSetRow(resultSet, ++position);
    }

    public ResultSetRow previous()
    {
        return new LuceneResultSetRow(resultSet, --position);
    }

    public int nextIndex()
    {
        return position + 1;
    }

    public int previousIndex()
    {
        return position - 1;
    }

    /*
     * Mutation is not supported
     */

    public void remove()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void set(ResultSetRow o)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void add(ResultSetRow o)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
