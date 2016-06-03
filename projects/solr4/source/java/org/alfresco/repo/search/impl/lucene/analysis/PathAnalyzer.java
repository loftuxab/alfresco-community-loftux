package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;

/**
 * @author Andy
 *
 */
public class PathAnalyzer extends Analyzer
{

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#createComponents(java.lang.String, java.io.Reader)
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader)
    {
        return new TokenStreamComponents(new PathTokenFilter(reader, PathTokenFilter.PATH_SEPARATOR, PathTokenFilter.SEPARATOR_TOKEN_TEXT, PathTokenFilter.NO_NS_TOKEN_TEXT, PathTokenFilter.NAMESPACE_START_DELIMITER, PathTokenFilter.NAMESPACE_END_DELIMITER, true));
    }

}
