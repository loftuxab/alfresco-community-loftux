/*
 * Created on 02-Jun-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.impl.lucene.LuceneSearcher;

public class LuceneCategoryServiceImpl implements CategoryService
{
    private NodeService nodeService;
    
    private NamespacePrefixResolver namespacePrefixResolver;

    private LuceneSearcher searcher;
    
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

    
    
    public void setSearcher(LuceneSearcher searcher)
    {
        this.searcher = searcher;
    }
    

    public Collection<ChildAssocRef> getChildren(NodeRef categoryRef, Mode mode, Depth depth)
    {
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
        
        resultSet = searcher.query(categoryRef.getStoreRef(), "lucene", luceneQuery.toString(), null, null);

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

    public Collection<ChildAssocRef> getCategories(QName attributeQName, Depth depth)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<ChildAssocRef> getRootCategories()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<QName> getCategoryAspects()
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void newCategory(QName typeName, String attributeName)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}
