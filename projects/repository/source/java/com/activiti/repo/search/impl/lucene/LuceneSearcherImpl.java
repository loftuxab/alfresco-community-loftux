/*
 * Created on Mar 24, 2005
 */
package com.activiti.repo.search.impl.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.saxpath.SAXPathException;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.EmptyResultSet;
import com.activiti.repo.search.QueryParameter;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.SearcherException;
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

    /*
     * Searcher implementation
     */

    public ResultSet query(StoreRef store, String language, String queryString, Path[] queryOptions, QueryParameter[] queryParameters) throws SearcherException
    {
        if (indexExists())
        {
            if (language.equalsIgnoreCase(LUCENE))
            {
                try
                {
                    Query query = LuceneQueryParser.parse(queryString, DEFAULT_FIELD, new StandardAnalyzer(), nameSpaceService);
                    Searcher searcher = getSearcher();

                    Hits hits = searcher.search(query);
                    return new LuceneResultSet(store, hits, searcher);

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
                    reader.setXPathHandler(handler);
                    reader.parse(queryString);
                    Query query = handler.getQuery();
                    Searcher searcher = getSearcher();
                    Hits hits = searcher.search(query);
                    return new LuceneResultSet(store, hits, searcher);

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

    public void setNameSpaceService(NamespaceService nameSpaceService)
    {
        this.nameSpaceService = nameSpaceService;
    }

    public boolean indexExists()
    {
        return mainIndexExists();
    }
}
