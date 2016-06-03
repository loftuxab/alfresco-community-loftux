package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.EmptyTokenStream;

/**
 * 
 * 
 * @author Derek Hulley
 * @since 4.0
 */
public class EmptyAnalyser extends Analyzer
{
    /** Builds an analyzer. */
    public EmptyAnalyser()
    {
    }

    /**
     * Constructs a {@link TokenStream} that returns nothing
     */
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new EmptyTokenStream();
    }
}
