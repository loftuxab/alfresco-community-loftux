/*
 * Created on 05-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.util.HashSet;

import org.alfresco.repo.search.SearcherException;
import org.alfresco.repo.search.impl.lucene.query.PathQuery;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.saxpath.SAXPathException;

import com.werken.saxpath.XPathReader;

public class LuceneQueryParser extends QueryParser
{
    private NamespacePrefixResolver namespacePrefixResolver;
    
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
    static public Query parse(String query, String field, Analyzer analyzer, NamespacePrefixResolver namespacePrefixResolver, DictionaryService dictionaryService) throws ParseException
    {
        LuceneQueryParser parser = new LuceneQueryParser(field, analyzer);
        parser.setNamespacePrefixResolver(namespacePrefixResolver);
        parser.setDictionaryService(dictionaryService);
        return parser.parse(query);
    }

    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
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
                handler.setNamespacePrefixResolver(namespacePrefixResolver);
                handler.setDictionaryService(dictionaryService);
                reader.setXPathHandler(handler);
                reader.parse(queryText);
                PathQuery pathQuery = handler.getQuery();
                pathQuery.setRepeats(false);
                return pathQuery;
            }
            else if (field.equals("PATH_WITH_REPEATS"))
            {
                XPathReader reader = new XPathReader();
                LuceneXPathHandler handler = new LuceneXPathHandler();
                handler.setNamespacePrefixResolver(namespacePrefixResolver);
                handler.setDictionaryService(dictionaryService);
                reader.setXPathHandler(handler);
                reader.parse(queryText);
                PathQuery pathQuery = handler.getQuery();
                pathQuery.setRepeats(true);
                return pathQuery;
            }
            else if (field.equals("QNAME"))
            {
                XPathReader reader = new XPathReader();
                LuceneXPathHandler handler = new LuceneXPathHandler();
                handler.setNamespacePrefixResolver(namespacePrefixResolver);
                handler.setDictionaryService(dictionaryService);
                reader.setXPathHandler(handler);
                reader.parse("//"+queryText);
                return handler.getQuery();
            }
            else if (field.equals("TYPE"))
            {
                TypeDefinition target = dictionaryService.getType(QName.createQName(queryText));
                if(target == null)
                {
                    throw new SearcherException("Invalid type: "+queryText);
                }
                QName targetQName = target.getName();
                HashSet<QName> subclasses = new HashSet<QName>();
                for(QName classRef : dictionaryService.getAllTypes())
                {
                    TypeDefinition current = dictionaryService.getType(classRef);
                    while( (current != null) && !current.getName().equals(targetQName))
                    {
                        current = (current.getParentName() == null) ? null : dictionaryService.getType(current.getParentName());
                    }
                    if(current != null)
                    {
                        subclasses.add(classRef);
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
                AspectDefinition target = dictionaryService.getAspect(QName.createQName(queryText));
                QName targetQName = target.getName();
                HashSet<QName> subclasses = new HashSet<QName>();
                for(QName classRef : dictionaryService.getAllAspects())
                {
                    AspectDefinition current = dictionaryService.getAspect(classRef);
                    while( (current != null) && !current.getName().equals(targetQName))
                    {
                        current = (current.getParentName() == null) ? null : dictionaryService.getAspect(current.getParentName());
                    }
                    if(current != null)
                    {
                        subclasses.add(classRef);
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
            else if (field.startsWith("@"))
            {
                // Check for any prefixes and expand to the full uri
                if(field.charAt(1) != '{')
                {
                    int colonPosition = field.indexOf(':');
                    if(colonPosition == -1)
                    {
                        // use the default namespace
                        return super.getFieldQuery("@{"+namespacePrefixResolver.getNamespaceURI("")+"}"+field.substring(1), queryText);
                    }
                    else
                    {
                        // find the prefix
                        return super.getFieldQuery("@{"+namespacePrefixResolver.getNamespaceURI(field.substring(1, colonPosition))+"}"+field.substring(colonPosition+1), queryText);
                    }
                }
                else
                {
                    // Already in expanded form
                    return super.getFieldQuery(field, queryText);
                }
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
