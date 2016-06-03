package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FilteredTermEnum;

/**
 * A term enum to find case insensitive matches - used for Upper and Lower
 * 
 * @author andyh
 */
public class CaseInsensitiveTermEnum extends FilteredTermEnum
{
    private String field = "";

    private boolean endEnum = false;

    private String text;

    /**
     * @param reader =
     *            the index reader
     * @param term -
     *            the term to match
     * @throws IOException
     */
    public CaseInsensitiveTermEnum(IndexReader reader, Term term) throws IOException
    {
        super();
        field = term.field();
        text = term.text();
        // position at the start - we could do slightly better
        setEnum(reader.terms(new Term(term.field(), "")));
    }

    @Override
    public float difference()
    {
        return 1.0f;
    }

    @Override
    protected boolean endEnum()
    {
        return endEnum;
    }

    @Override
    protected boolean termCompare(Term term)
    {
        if (field.equals(term.field()))
        {
            String searchText = term.text();
            return searchText.equalsIgnoreCase(text);
        }
        endEnum = true;
        return false;
    }

}
