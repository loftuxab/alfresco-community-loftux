package org.alfresco.repo.search.impl.lucene;

import java.io.Reader;

import org.alfresco.repo.search.adaptor.lucene.AnalysisMode;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author Andy
 *
 */
public abstract class AbstractAnalyzer extends Analyzer
{

    public abstract TokenStream tokenStream(String fieldName, Reader reader, AnalysisMode analysisMode);
}
