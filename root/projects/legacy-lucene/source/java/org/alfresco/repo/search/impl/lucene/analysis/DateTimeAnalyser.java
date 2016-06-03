package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class DateTimeAnalyser extends Analyzer
{

    public DateTimeAnalyser()
    {
        super();
    }

    // Split at the T in the XML date form
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new DateTimeTokenFilter(reader);
    }
}
