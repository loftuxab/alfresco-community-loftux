/*
 * Created on 06-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class DateAnalyser extends Analyzer
{

    public DateAnalyser()
    {
        super();
    }

    // Split at the T in the XML date form
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new DateTokenFilter(reader);
    }
}
