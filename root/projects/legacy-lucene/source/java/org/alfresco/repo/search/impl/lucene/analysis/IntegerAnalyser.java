package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Simple analyser for integers.
 * 
 * @author Andy Hind
 */
public class IntegerAnalyser extends Analyzer
{

    public IntegerAnalyser()
    {
        super();
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new IntegerTokenFilter(reader);
    }
}
