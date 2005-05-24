/*
 * Created on 19-May-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;
import org.dom4j.Element;
import org.dom4j.Namespace;

public class CannedQueryDefImpl implements CannedQueryDef
{
    private static final org.dom4j.QName ELEMENT_QNAME = new org.dom4j.QName("query-definition", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName QNAME = new org.dom4j.QName("qname", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName LANGUAGE = new org.dom4j.QName("language", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName QUERY = new org.dom4j.QName("query", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private QName qName;

    private String language;

    private List<QueryParameterDefinition> queryParameterDefs;

    String query;

    QueryCollection container;

    public CannedQueryDefImpl(QName qName, String language, String query, List<QueryParameterDefinition> queryParameterDefs, QueryCollection container)
    {
        super();
        this.qName = qName;
        this.language = language;
        this.query = query;
        this.queryParameterDefs = queryParameterDefs;
        this.container = container;
    }

    public QName getQname()
    {
        return qName;
    }

    public String getLanguage()
    {
        return language;
    }

    public List<QueryParameterDefinition> getQueryParameterDefs()
    {
        return queryParameterDefs;
    }

    public String getQuery()
    {
        return query;
    }

    public NamespacePrefixResolver getNamespacePrefixResolver()
    {
        return container.getNamespacePrefixResolver();
    }

    public static CannedQueryDefImpl createCannedQuery(Element element, DictionaryService dictionaryService, QueryCollection container)
    {
        if (element.getQName().getName().equals(ELEMENT_QNAME.getName()))
        {
            QName qName = null;
            Element qNameElement = element.element(QNAME.getName());
            if(qNameElement != null)
            {
               qName = QName.createQName(qNameElement.getText(), container.getNamespacePrefixResolver());
            }   
            
            String language = null;
            Element languageElement = element.element(LANGUAGE.getName());
            if(languageElement != null)
            {
               language = languageElement.getText();
            }  
            
            String query = null;
            Element queryElement = element.element(QUERY.getName());
            if(queryElement != null)
            {
               query = queryElement.getText();
            }  
            
            List<QueryParameterDefinition> queryParameterDefs = new ArrayList<QueryParameterDefinition>();
            
            List list = element.elements(QueryParameterDefImpl.getElementQName().getName());
            for(Iterator it = list.iterator(); it.hasNext(); /**/)
            {
                Element defElement = (Element) it.next();
                NamedQueryParameterDefinition nqpd = QueryParameterDefImpl.createParameterDefinition(defElement, dictionaryService);
                queryParameterDefs.add(nqpd.getQueryParameterDefinition());
            }
            
            list = element.elements(QueryParameterRefImpl.getElementQName().getName());
            for(Iterator it = list.iterator(); it.hasNext(); /**/)
            {
                Element refElement = (Element) it.next();
                NamedQueryParameterDefinition nqpd = QueryParameterRefImpl.createParameterReference(refElement, dictionaryService, container);
                queryParameterDefs.add(nqpd.getQueryParameterDefinition());
            }
            
            return new CannedQueryDefImpl(qName, language, query, queryParameterDefs, container);
            
        }
        else
        {
            return null;
        }
    }
    
    public static org.dom4j.QName getElementQName()
    {
        return ELEMENT_QNAME;
    }

}
