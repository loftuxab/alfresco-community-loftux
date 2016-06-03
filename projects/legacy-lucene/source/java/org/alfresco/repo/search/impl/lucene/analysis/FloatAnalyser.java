package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Simple analyser for floats.
 * 
 * @author Andy Hind
 */
public class FloatAnalyser extends Analyzer
{

    public FloatAnalyser()
    {
        super();
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new FloatTokenFilter(reader);
    }
}
