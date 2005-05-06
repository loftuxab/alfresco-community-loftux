/*
 * Created on 05-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.Query;
import org.saxpath.SAXPathException;

import org.alfresco.repo.dictionary.NamespaceService;
import com.werken.saxpath.XPathReader;

public class LuceneQueryParser extends QueryParser
{
    private NamespaceService nameSpaceService;
    
    /**
     * Parses a query string, returning a {@link org.apache.lucene.search.Query}.
     * 
     * @param query
     *            the query string to be parsed.
     * @param field
     *            the default field for query terms.
     * @param analyzer
     *            used to find terms in the query text.
     * @throws ParseException
     *             if the parsing fails
     */
    static public Query parse(String query, String field, Analyzer analyzer, NamespaceService nameSpaceService) throws ParseException
    {
        LuceneQueryParser parser = new LuceneQueryParser(field, analyzer);
        parser.setNameSpaceService(nameSpaceService);
        return parser.parse(query);
    }

    public void setNameSpaceService(NamespaceService nameSpaceService)
    {
        this.nameSpaceService = nameSpaceService;
    }
    

    public LuceneQueryParser(String arg0, Analyzer arg1)
    {
        super(arg0, arg1);
    }

    public LuceneQueryParser(CharStream arg0)
    {
        super(arg0);
    }

    public LuceneQueryParser(QueryParserTokenManager arg0)
    {
        super(arg0);
    }

    protected Query getFieldQuery(String field, String queryText) throws ParseException
    {
        try
        {
            if (field.equals("PATH"))
            {
                XPathReader reader = new XPathReader();
                LuceneXPathHandler handler = new LuceneXPathHandler();
                handler.setNameSpaceService(nameSpaceService);
                reader.setXPathHandler(handler);
                reader.parse(queryText);
                return handler.getQuery();
            }
            else
            {
                return super.getFieldQuery(field, queryText);
            }
        }
        catch (SAXPathException e)
        {
            throw new ParseException("Failed to parse XPath...\n" + e.getMessage());
        }

    }

}
