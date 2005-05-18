/*
 * Created on 05-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.util.ArrayList;
import java.util.HashSet;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.impl.lucene.query.PathQuery;
import org.alfresco.repo.search.impl.lucene.query.RelativeStructuredFieldPosition;
import org.alfresco.repo.search.impl.lucene.query.StructuredFieldPosition;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.saxpath.SAXPathException;

import com.werken.saxpath.XPathReader;

public class LuceneQueryParser extends QueryParser
{
    private NamespaceService nameSpaceService;
    
    private DictionaryService dictionaryService;
    
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
    static public Query parse(String query, String field, Analyzer analyzer, NamespaceService nameSpaceService, DictionaryService dictionaryService) throws ParseException
    {
        LuceneQueryParser parser = new LuceneQueryParser(field, analyzer);
        parser.setNameSpaceService(nameSpaceService);
        parser.setDictionaryService(dictionaryService);
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
                handler.setDictionaryService(dictionaryService);
                reader.setXPathHandler(handler);
                reader.parse(queryText);
                return handler.getQuery();
            }
            else if (field.equals("QNAME"))
            {
                XPathReader reader = new XPathReader();
                LuceneXPathHandler handler = new LuceneXPathHandler();
                handler.setNameSpaceService(nameSpaceService);
                handler.setDictionaryService(dictionaryService);
                reader.setXPathHandler(handler);
                reader.parse("//"+queryText);
                return handler.getQuery();
            }
            else if (field.equals("TYPE"))
            {
                ClassDefinition target = dictionaryService.getType(new ClassRef(QName.createQName(queryText)));
                QName targetQName = target.getQName();
                HashSet<QName> subclasses = new HashSet<QName>();
                for(ClassRef classRef :  dictionaryService.getTypes())
                {
                    ClassDefinition current = dictionaryService.getType(classRef);
                    QName currentQname = current.getQName();
                    while( (current != null) && !current.getQName().equals(targetQName))
                    {
                        current = current.getSuperClass();
                        currentQname = (current == null) ? null : current.getQName();
                    }
                    if(current != null)
                    {
                        subclasses.add(classRef.getQName());
                    }
                }
                BooleanQuery booleanQuery = new BooleanQuery();
                for(QName qname: subclasses)
                { 
                   TermQuery termQuery =  new TermQuery(new Term(field, qname.toString()));
                   booleanQuery.add(termQuery, false, false);
                }
                return booleanQuery;
            }
            else if (field.equals("ASPECT"))
            {
                ClassDefinition target = dictionaryService.getAspect(new ClassRef(QName.createQName(queryText)));
                QName targetQName = target.getQName();
                HashSet<QName> subclasses = new HashSet<QName>();
                for(ClassRef classRef : dictionaryService.getAspects())
                {
                    ClassDefinition current = dictionaryService.getAspect(classRef);
                    QName currentQname = current.getQName();
                    while( (current != null) && !current.getQName().equals(targetQName))
                    {
                        current = current.getSuperClass();
                        currentQname = (current == null) ? null : current.getQName();
                    }
                    if(current != null)
                    {
                        subclasses.add(classRef.getQName());
                    }
                }
                
                BooleanQuery booleanQuery = new BooleanQuery();
                for(QName qname: subclasses)
                { 
                    TermQuery termQuery =  new TermQuery(new Term(field, qname.toString()));
                    booleanQuery.add(termQuery, false, false);
                }
                return booleanQuery;
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

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    

}
