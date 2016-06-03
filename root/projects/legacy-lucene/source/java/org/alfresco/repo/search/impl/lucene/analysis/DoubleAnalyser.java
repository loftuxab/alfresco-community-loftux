package org.alfresco.repo.search.impl.lucene.analysis;

/**
 * Simple analyser to wrap the tokenisation of doubles.
 * 
 * @author Andy Hind
 */
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class DoubleAnalyser extends Analyzer
{

    public DoubleAnalyser()
    {
        super();
    }


    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new DoubleTokenFilter(reader);
    }
}
