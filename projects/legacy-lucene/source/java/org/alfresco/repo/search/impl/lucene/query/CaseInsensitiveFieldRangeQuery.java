package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FilteredTermEnum;
import org.apache.lucene.search.MultiTermQuery;

/**
 * Find terms that match a range ignoring case
 * 
 * @author andyh
 */
public class CaseInsensitiveFieldRangeQuery extends MultiTermQuery
{
    /**
     * 
     */
    private static final long serialVersionUID = -5859977841901861122L;

    String expandedFieldName;

    String lowerTermText;

    String upperTermText;

    boolean includeLower;

    boolean includeUpper;

    /**
     * @param expandedFieldName -
     *            field
     * @param lowerTermText -
     *            upper range value
     * @param upperTermText -
     *            lower range value
     * @param includeLower -
     *            include the lower value
     * @param includeUpper -
     *            include the upper value
     */
    public CaseInsensitiveFieldRangeQuery(String expandedFieldName, String lowerTermText, String upperTermText, boolean includeLower, boolean includeUpper)
    {
        super(new Term(expandedFieldName, ""));
        this.expandedFieldName = expandedFieldName;
        this.lowerTermText = lowerTermText;
        this.upperTermText = upperTermText;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }

    @Override
    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException
    {
        return new CaseInsensitiveTermRangeEnum(reader, expandedFieldName, lowerTermText, upperTermText, includeLower, includeUpper);
    }

}
