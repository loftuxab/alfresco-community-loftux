package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FilteredTermEnum;
import org.apache.lucene.search.MultiTermQuery;

/**
 * Perform a case insensitive match against a field
 * 
 * @author andyh
 *
 */
public class CaseInsensitiveFieldQuery extends MultiTermQuery
{
    /**
     * 
     */
    private static final long serialVersionUID = -2570803495329346982L;

    /**
     * @param term - the term for the match
     */
    public CaseInsensitiveFieldQuery(Term term)
    {
        super(term);
    }
    
    @Override
    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException
    {
        Term term = new Term(getTerm().field(), getTerm().text());
        return new CaseInsensitiveTermEnum(reader, term);
    }

}
