package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Simple analyser for longs.
 * 
 * @author Andy Hind
 */
public class LongAnalyser extends Analyzer
{

    public LongAnalyser()
    {
        super();
    }


    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new LongTokenFilter(reader);
    }
}
