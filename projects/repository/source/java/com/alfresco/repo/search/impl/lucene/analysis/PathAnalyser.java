/*
 * Created on Mar 16, 2005
 */
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Analyse repository paths
 * 
 * @author andyh
 */
public class PathAnalyser extends Analyzer
{
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new PathTokenFilter(reader, PathTokenFilter.PATH_SEPARATOR,
                PathTokenFilter.SEPARATOR_TOKEN_TEXT, PathTokenFilter.NO_NS_TOKEN_TEXT,
                PathTokenFilter.NAMESPACE_START_DELIMITER, PathTokenFilter.NAMESPACE_END_DELIMITER, true);
    }
}