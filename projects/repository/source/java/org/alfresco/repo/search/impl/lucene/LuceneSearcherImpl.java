/*
 * Created on Mar 24, 2005
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.CannedQueryDef;
import org.alfresco.repo.search.EmptyResultSet;
import org.alfresco.repo.search.QueryParameter;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.alfresco.repo.search.QueryRegisterComponent;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.SearcherException;
import org.alfresco.repo.value.ValueConverter;
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

    private QueryRegisterComponent queryRegister;

    /*
     * Searcher implementation
     */

    public ResultSet query(StoreRef store, String language, String queryString, Path[] queryOptions, QueryParameterDefinition[] queryParameterDefinitions) throws SearcherException
    {
        if (indexExists())
        {
            String parameterisedQueryString;
            if (queryParameterDefinitions != null)
            {
                Map<QName, QueryParameterDefinition> map = new HashMap<QName, QueryParameterDefinition>();
                if (queryParameterDefinitions != null)
                {
                    for (QueryParameterDefinition qpd : queryParameterDefinitions)
                    {
                        map.put(qpd.getQName(), qpd);
                    }
                }
                parameterisedQueryString = parameterise(queryString, map, null, nameSpaceService);
            }
            else
            {
                parameterisedQueryString = queryString;
            }
            
            if (language.equalsIgnoreCase(LUCENE))
            {
                try
                {

                    Query query = LuceneQueryParser.parse(parameterisedQueryString, DEFAULT_FIELD, new LuceneAnalyser(dictionaryService), nameSpaceService, dictionaryService);
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
                    // TODO: Handler should have the query parameters to use in building its lucene query
                    // At the moment xpath style parameters in the PATH expression are not supported.
                    reader.setXPathHandler(handler);
                    reader.parse(parameterisedQueryString);
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

    public void setQueryRegister(QueryRegisterComponent queryRegister)
    {
        this.queryRegister = queryRegister;
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
        CannedQueryDef definition = queryRegister.getQueryDefinition(queryId);

        // Do parameter replacement
        // As lucene phrases are tokensied it is correct to just do straight
        // string replacement.
        // The string will be formatted by the tokeniser.
        //
        // For non phrase queries this is incorrect but string replacement is
        // probably the best we can do.
        // As numbers and text are indexed specially, direct term queries only
        // make sense against textual data

        checkParameters(definition, queryParameters);

        String queryString = parameterise(definition.getQuery(), definition.getQueryParameterMap(), queryParameters, definition.getNamespacePrefixResolver());

        return query(store, definition.getLanguage(), queryString, null, null);
    }

    /**
     * The definitions must provide a default value, or of not there must be a
     * parameter to provide the value
     * 
     * @param definition
     * @param queryParameters
     * @throws QueryParameterisationException
     */
    private void checkParameters(CannedQueryDef definition, QueryParameter[] queryParameters) throws QueryParameterisationException
    {
        List<QName> missing = new ArrayList<QName>();

        Set<QName> parameterQNameSet = new HashSet<QName>();
        if (queryParameters != null)
        {
            for (QueryParameter parameter : queryParameters)
            {
                parameterQNameSet.add(parameter.getQName());
            }
        }

        for (QueryParameterDefinition parameterDefinition : definition.getQueryParameterDefs())
        {
            if (!parameterDefinition.hasDefaultValue())
            {
                if (!parameterQNameSet.contains(parameterDefinition.getQName()))
                {
                    missing.add(parameterDefinition.getQName());
                }
            }
        }

        if (missing.size() > 0)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("The query is missing values for the following parameters: ");
            for (QName qName : missing)
            {
                buffer.append(qName);
                buffer.append(", ");
            }
            buffer.delete(buffer.length() - 1, buffer.length() - 1);
            buffer.delete(buffer.length() - 1, buffer.length() - 1);
            throw new QueryParameterisationException(buffer.toString());
        }
    }

    /*
     * Parameterise the query string - not sure if it is required to escape
     * lucence spacials chars The parameters could be used to build the query -
     * the contents of parameters should alread have been escaped if required.
     * ... mush better to provide the parameters and work out what to do TODO:
     * conditional query escapement - may be we should have a parameter type
     * that is not escaped
     */
    private String parameterise(String unparameterised, Map<QName, QueryParameterDefinition> map, QueryParameter[] queryParameters, NamespacePrefixResolver nspr)
            throws QueryParameterisationException
    {

        Map<QName, List<Serializable>> valueMap = new HashMap<QName, List<Serializable>>();

        if (queryParameters != null)
        {
            for (QueryParameter parameter : queryParameters)
            {
                List<Serializable> list = valueMap.get(parameter.getQName());
                if (list == null)
                {
                    list = new ArrayList<Serializable>();
                    valueMap.put(parameter.getQName(), list);
                }
                list.add(parameter.getValue());
            }
        }

        Map<QName, ListIterator<Serializable>> iteratorMap = new HashMap<QName, ListIterator<Serializable>>();

        List<QName> missing = new ArrayList<QName>();
        StringBuffer buffer = new StringBuffer(unparameterised);
        int index = 0;
        while ((index = buffer.indexOf("${", index)) != -1)
        {
            int endIndex = buffer.indexOf("}", index);
            String qNameString = buffer.substring(index + 2, endIndex);
            QName key = QName.createQName(qNameString, nspr);
            QueryParameterDefinition parameterDefinition = map.get(key);
            if (parameterDefinition == null)
            {
                missing.add(key);
                buffer.replace(index, endIndex + 1, "");
            }
            else
            {
                ListIterator<Serializable> it = iteratorMap.get(key);
                if ((it == null) || (!it.hasNext()))
                {
                    List<Serializable> list = valueMap.get(key);
                    if ((list != null) && (list.size() > 0))
                    {
                        it = list.listIterator();
                    }
                    if (it != null)
                    {
                        iteratorMap.put(key, it);
                    }
                }
                String value;
                if (it == null)
                {
                    value = parameterDefinition.getDefault();
                }
                else
                {
                    value = ValueConverter.convert(String.class, it.next());
                }
                buffer.replace(index, endIndex + 1, value);
            }
        }
        if (missing.size() > 0)
        {
            StringBuffer error = new StringBuffer();
            error.append("The query uses the following parameters which are not defined: ");
            for (QName qName : missing)
            {
                error.append(qName);
                error.append(", ");
            }
            error.delete(error.length() - 1, error.length() - 1);
            error.delete(error.length() - 1, error.length() - 1);
            throw new QueryParameterisationException(error.toString());
        }
        return buffer.toString();
    }

    private void reset(ListIterator it)
    {
        while (it.hasPrevious())
        {
            it.previous();
        }
    }
}
