/*
 * Created on 13-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.search.Searcher;

public interface LuceneSearcher extends Searcher, Lockable
{
   public boolean indexExists();
   public void setNodeService(NodeService nodeService);
   public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver);
}
