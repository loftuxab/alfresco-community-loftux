/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.search.impl.lucene;


import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.search.impl.lucene.fts.FTSIndexerAware;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeService;

public interface LuceneIndexer extends Indexer, Lockable
{

    public void commit();
    public void rollback();
    public int prepare();
    public boolean isModified();
    public void setNodeService(NodeService nodeService);
    public void setDictionaryService(DictionaryService dictionaryService);
    public void setLuceneFullTextSearchIndexer(FullTextSearchIndexer luceneFullTextSearchIndexer);
    
    public void updateFullTextSearch(int size);
    public void registerCallBack(FTSIndexerAware indexer);
}
