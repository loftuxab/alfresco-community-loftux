/*
 * Created on Mar 24, 2005
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.IOException;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.EmptyResultSet;
import org.alfresco.repo.search.QueryParameter;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.SearcherException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.saxpath.SAXPathException;

import com.werken.saxpath.XPathReader;

/**
 * The Lucene implementation of Searcher At the moment we support only lucene
 * based queries.
 * 
 * TODO: Support for other query languages
 * 
 * @author andyh
 * 
 */
public class LuceneSearcherImpl extends LuceneBase implements LuceneSearcher
{
    /**
     * Lucence languages key = temporary implementation
     */
    private static final String LUCENE = "lucene";

    private static final String XPATH = "xpath";

    /**
     * Default field name
     */
    private static final String DEFAULT_FIELD = "FTS";

    private NamespaceService nameSpaceService;

    private NodeService nodeService;
    
    private DictionaryService dictionaryService;

    /*
     * Searcher implementation
     */

    public ResultSet query(StoreRef store, String language, String queryString, Path[] queryOptions, QueryParameterDefinition[] queryParameterDefinitions) throws SearcherException
    {
        if (indexExists())
        {
            if (language.equalsIgnoreCase(LUCENE))
            {
                try
                {
                    Query query = LuceneQueryParser.parse(queryString, DEFAULT_FIELD, new LuceneAnalyser(dictionaryService), nameSpaceService, dictionaryService);
                    Searcher searcher = getSearcher();

                    Hits hits = searcher.search(query);
                    return new LuceneResultSet(store, hits, searcher, nodeService);

                }
                catch (ParseException e)
                {
                    throw new SearcherException("Failed to parse query: " + queryString, e);
                }
                catch (IOException e)
                {
                    throw new SearcherException("IO exception during search", e);
                }
            }
            else if (language.equalsIgnoreCase(XPATH))
            {
                try
                {
                    XPathReader reader = new XPathReader();
                    LuceneXPathHandler handler = new LuceneXPathHandler();
                    handler.setNameSpaceService(nameSpaceService);
                    handler.setDictionaryService(dictionaryService);
                    reader.setXPathHandler(handler);
                    reader.parse(queryString);
                    Query query = handler.getQuery();
                    Searcher searcher = getSearcher();
                    Hits hits = searcher.search(query);
                    return new LuceneResultSet(store, hits, searcher, nodeService);

                }
                catch (SAXPathException e)
                {
                    throw new SearcherException("Failed to parse query: " + queryString, e);
                }
                catch (IOException e)
                {
                    throw new SearcherException("IO exception during search", e);
                }
            }
            else
            {
                throw new SearcherException("Unknown query language: " + language);
            }
        }
        else
        {
            // no index return an empty result set
            return new EmptyResultSet();
        }
    }

    /**
     * Get an intialised searcher for the store and transaction Normally we do
     * not search againsta a store and delta. Currently only gets the searcher
     * against the main index.
     * 
     * @param storeRef
     * @param deltaId
     * @return
     */
    public static LuceneSearcherImpl getSearcher(StoreRef storeRef, String deltaId)
    {
        LuceneSearcherImpl searcher = new LuceneSearcherImpl();
        try
        {
            searcher.initialise(storeRef, deltaId, false);
        }
        catch (IOException e)
        {
            throw new SearcherException(e);
        }
        return searcher;
    }

    /**
     * Get an intialised searcher for the store. No transactional ammendsmends
     * are searched.
     * 
     * 
     * @param storeRef
     * @return
     */
    public static LuceneSearcherImpl getSearcher(StoreRef storeRef)
    {
        return getSearcher(storeRef, null);
    }

    public void setNamespaceService(NamespaceService nameSpaceService)
    {
        this.nameSpaceService = nameSpaceService;
    }

    public boolean indexExists()
    {
        return mainIndexExists();
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public ResultSet query(StoreRef store, String language, String query)
    {
        return query(store, language, query, null, null);
    }

    public ResultSet query(StoreRef store, String language, String query, QueryParameterDefinition[] queryParameterDefintions)
    {
        return query(store, language, query, null, queryParameterDefintions);
    }

    public ResultSet query(StoreRef store, String language, String query, Path[] attributePaths)
    {
        return query(store, language, query, attributePaths, null);
    }

    public ResultSet query(StoreRef store, QName queryId, QueryParameter[] queryParameters)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
    
    
    
}
