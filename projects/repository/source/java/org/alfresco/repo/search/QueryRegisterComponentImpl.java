/*
 * Created on 19-May-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class QueryRegisterComponentImpl implements QueryRegisterComponent
{
    private DictionaryService dictionaryService;

    private NamespacePrefixResolver namespaceService;

    private Map<String, QueryCollection> collections = new HashMap<String, QueryCollection>();

    public QueryRegisterComponentImpl()
    {
        super();
    }

    public CannedQueryDef getQueryDefinition(QName qName)
    {
        for(String key: collections.keySet())
        {
            QueryCollection collection = collections.get(key);
            CannedQueryDef  def = collection.getQueryDefinition(qName);
            if(def != null)
            {
                return def;
            }
        }
        return null;
    }

    public String getCollectionNameforQueryDefinition(QName qName)
    {
        for(String key: collections.keySet())
        {
            QueryCollection collection = collections.get(key);
            if(collection.containsQueryDefinition(qName))
            {
                return key;
            }
        }
        return null;
    }

    public QueryParameterDefinition getParameterDefinition(QName qName)
    {
        for(String key: collections.keySet())
        {
            QueryCollection collection = collections.get(key);
            QueryParameterDefinition  def = collection.getParameterDefinition(qName);
            if(def != null)
            {
                return def;
            }
        }
        return null;
    }

    public String getCollectionNameforParameterDefinition(QName qName)
    {
        for(String key: collections.keySet())
        {
            QueryCollection collection = collections.get(key);
            if(collection.containsParameterDefinition(qName))
            {
                return key;
            }
        }
        return null;
    }

    public QueryCollection getQueryCollection(String location)
    {
        return collections.get(location);
    }

    public void loadQueryCollection(String location)
    {
        try
        {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(location);
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            is.close();
            QueryCollection collection = QueryCollectionImpl.createQueryCollection(document.getRootElement(), dictionaryService, namespaceService);
            collections.put(location, collection);
        }
        catch (DocumentException de)
        {
            throw new AlfrescoRuntimeException("Error reading XML", de);
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException("IO Error reading XML", e);
        }
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespacePrefixResolver namespaceService)
    {
        this.namespaceService = namespaceService;
    }
}
