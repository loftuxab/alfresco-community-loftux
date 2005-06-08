/*
 * Created on 02-Jun-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.search.CategoryService;
import org.alfresco.repo.search.IndexerException;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.ResultSetRow;

public class LuceneCategoryServiceImpl implements CategoryService
{
    private NodeService nodeService;
    
    private NamespacePrefixResolver namespacePrefixResolver;
    
    private DictionaryService dictionaryService;

    private LuceneIndexerAndSearcher indexerAndSearcher;
    
    public LuceneCategoryServiceImpl()
    {
        super();
    }

    // Inversion of control support

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    

    public void setIndexerAndSearcher(LuceneIndexerAndSearcher indexerAndSearcher)
    {
        this.indexerAndSearcher = indexerAndSearcher;
    }
    

    public Collection<ChildAssocRef> getChildren(NodeRef categoryRef, Mode mode, Depth depth)
    {
        if(categoryRef == null)
        {
            return Collections.<ChildAssocRef>emptyList();
        }
        ResultSet resultSet = null;
        StringBuffer luceneQuery = new StringBuffer();

        if (!mode.equals(Mode.ALL))
        {
            luceneQuery.append(mode.equals(Mode.SUB_CATEGORIES) ? "-" : "").append("PATH:\"");
            luceneQuery.append(buildXPath(nodeService.getPath(categoryRef))).append("/");
            if (depth.equals(Depth.ANY))
            {
                luceneQuery.append("/");
            }
            luceneQuery.append("member").append("\" ");
        }
        
        if(!mode.equals(Mode.MEMBERS))
        {
            luceneQuery.append("PATH:\"");
            luceneQuery.append(buildXPath(nodeService.getPath(categoryRef))).append("/");
            if (depth.equals(Depth.ANY))
            {
                luceneQuery.append("/");
            }
            luceneQuery.append("*").append("\" ");
        }
        
        resultSet = indexerAndSearcher.getSearcher(categoryRef.getStoreRef(), false).query(categoryRef.getStoreRef(), "lucene", luceneQuery.toString(), null, null);

        return resultSetToChildAssocCollection(resultSet);
    }
    
    private String buildXPath(Path path)
    {
        StringBuffer pathBuffer = new StringBuffer();
        for (Iterator<Path.Element> elit = path.iterator(); elit.hasNext(); /**/)
        {
            Path.Element element = elit.next();
            if (!(element instanceof Path.ChildAssocElement))
            {
                throw new IndexerException("Confused path: " + path);
            }
            Path.ChildAssocElement cae = (Path.ChildAssocElement) element;
            if(cae.getRef().getParentRef() != null)
            {
                pathBuffer.append("/");
                pathBuffer.append(getPrefix(cae.getRef().getQName().getNamespaceURI()));
                pathBuffer.append(cae.getRef().getQName().getLocalName());
            }
        }
        return pathBuffer.toString();
    }
    
    HashMap<String, String> prefixLookup = new HashMap<String, String>();
    
    private String getPrefix(String uri)
    {
        String prefix = prefixLookup.get(uri);
        if(prefix == null)
        {
           Collection<String> prefixes = namespacePrefixResolver.getPrefixes(uri);
           for(String first: prefixes)
           {
               prefix = first;
               break;
           }
           
           prefixLookup.put(uri, prefix);
        }
        if(prefix == null)
        {
            return "";
        }
        else
        {
            return prefix +":";
        }
        
    }
    

    private Collection<ChildAssocRef> resultSetToChildAssocCollection(ResultSet resultSet)
    {
        List<ChildAssocRef> collection = new ArrayList<ChildAssocRef>();
        if (resultSet != null)
        {
            for(ResultSetRow row: resultSet)
            {
                ChildAssocRef car = nodeService.getPrimaryParent(row.getNodeRef());
                collection.add(car);
            }
        }
        return collection;
    }

    public Collection<ChildAssocRef> getCategories(StoreRef storeRef, QName attributeQName, Depth depth)
    {
        QName qname = dictionaryService.getProperty(attributeQName).getContainerClass().getName();
        return getChildren(getCategoryRootNode(storeRef, qname), Mode.SUB_CATEGORIES, depth);
    }
    
    private NodeRef getCategoryRootNode(StoreRef storeRef, QName qname)
    {
        ResultSet resultSet = indexerAndSearcher.getSearcher(storeRef, false).query(storeRef, "lucene", "PATH:\"/"+getPrefix(qname.getNamespaceURI())+qname.getLocalName()+"\"", null, null);
        if(resultSet.length() != 1)
        {
            return null;
        }
        else
        {
            return resultSet.getNodeRef(0);
        }
    }
    

    public Collection<ChildAssocRef> getRootCategories(StoreRef storeRef)
    {
        
        ResultSet resultSet = indexerAndSearcher.getSearcher(storeRef, false).query(storeRef, "lucene", "PATH:\"//alf:categoryRoot/*\"", null, null);
        return resultSetToChildAssocCollection(resultSet);
    }

    public Collection<QName> getCategoryAspects()
    {
        List<QName> list = new ArrayList<QName>();
        for(QName aspect :dictionaryService.getAllAspects())
        {
            if(dictionaryService.isSubClass(aspect, DictionaryBootstrap.ASPECT_QNAME_CLASSIFIABLE))
            {
                list.add(aspect);
            }
        }
        return list;
    }

    public NodeRef newCategory(QName typeName, String attributeName)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
