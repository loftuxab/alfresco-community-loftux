package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ISOLatin1AccentFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;


public class AlfrescoStandardAnalyser extends Analyzer
{
    @SuppressWarnings("rawtypes")
    private Set stopSet;

    /**
     * An array containing some common English words that are usually not useful for searching.
     */
    public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;

    /** Builds an analyzer. */
    public AlfrescoStandardAnalyser()
    {
        this(STOP_WORDS);
    }

    /** Builds an analyzer with the given stop words. */
    public AlfrescoStandardAnalyser(String[] stopWords)
    {
        stopSet = StopFilter.makeStopSet(stopWords);
    }
    
    /**
     * Constructs a {@link StandardTokenizer} filtered by a {@link StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}.
     */
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new StandardTokenizer(reader);
        result = new AlfrescoStandardFilter(result);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopSet);
        result = new ISOLatin1AccentFilter(result);
        return result;
    }
}
