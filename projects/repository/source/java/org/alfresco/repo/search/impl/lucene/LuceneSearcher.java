/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;

public interface LuceneSearcher extends SearchService, Lockable
{
   public boolean indexExists();
   public void setNodeService(NodeService nodeService);
   public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver);
}
