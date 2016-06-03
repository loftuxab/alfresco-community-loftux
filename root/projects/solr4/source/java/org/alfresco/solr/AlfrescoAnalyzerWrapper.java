package org.alfresco.solr;

import org.alfresco.repo.search.MLAnalysisMode;
import org.alfresco.repo.search.impl.lucene.analysis.MLAnalayser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.solr.schema.IndexSchema;

/**
 * Wraps SOLR access to for localising tokens
 * As analysers are cached, and anylysers themselves cache token streams we have to be able to switch locales 
 * inside the MLAnalyser.  
 * 
 * @author Andy
 *
 */
public class AlfrescoAnalyzerWrapper extends AnalyzerWrapper
{
	public static enum Mode
	{
		INDEX, QUERY;
	}
	
    IndexSchema schema;
    
    Mode mode;
    
    /**
     * @param schema
     * @param index 
     */
    public AlfrescoAnalyzerWrapper(IndexSchema schema, Mode mode)
    {
        super(Analyzer.PER_FIELD_REUSE_STRATEGY);
        this.schema = schema;
        this.mode = mode;
    }
    
    

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.AnalyzerWrapper#getPositionIncrementGap(java.lang.String)
     */
    @Override
    public int getPositionIncrementGap(String fieldName)
    {
        return 100;
    }



    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.AnalyzerWrapper#getWrappedAnalyzer(java.lang.String)
     */
    @Override
    protected Analyzer getWrappedAnalyzer(String fieldName)
    {
        if(fieldName.contains("l_@{"))
        {
            return new MLAnalayser(MLAnalysisMode.EXACT_LANGUAGE, schema, mode);
        }
        else if(fieldName.contains("lt@{"))
        {
             return new MLAnalayser(MLAnalysisMode.EXACT_LANGUAGE, schema, mode);
        }
        else
        {
            return schema.getFieldTypeByName("text___").getAnalyzer();
        }
    }

}
