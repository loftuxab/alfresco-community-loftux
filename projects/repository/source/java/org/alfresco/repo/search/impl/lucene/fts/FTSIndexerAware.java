/*
 * Created on 22-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.fts;

import org.alfresco.service.cmr.repository.StoreRef;

public interface FTSIndexerAware
{

    public void indexCompleted(StoreRef storeRef, int remaining, Exception e);
}
