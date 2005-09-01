/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.content.replication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.TempFileProvider;
import org.springframework.context.ApplicationContext;

/**
 * Tests read and write functionality for the replicatingStore.
 * 
 * @see org.alfresco.repo.content.replication.ReplicatingContentStore
 * 
 * @author Derek Hulley
 */
public class ReplicatingContentStoreTest extends TestCase
{
    private static final ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
    
    private TransactionService transactionService;
    private ReplicatingContentStore replicatingStore;
    private List<ContentStore> stores;
    private String contentUrl;
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        transactionService = (TransactionService) ctx.getBean("transactionComponent");
        
        File tempDir = TempFileProvider.getTempDir();
        // create some file stores
        stores = new ArrayList<ContentStore>(3);
        for (int i = 0; i < 3; i++)
        {
            String storeDir = tempDir.getAbsolutePath() + File.separatorChar + GUID.generate();
            FileContentStore store = new FileContentStore(storeDir);
            stores.add(store);
        }
        replicatingStore = new ReplicatingContentStore();
        replicatingStore.setTransactionService(transactionService);
        replicatingStore.setStores(stores);
    }

    public void testSetUp() throws Exception
    {
        assertNotNull(transactionService);
    }
    
    public void testReplication() throws Exception
    {
        String contentUrl =
            FileContentStore.STORE_PROTOCOL +
            getName() + '/' +
            GUID.generate() +
            ".bin";
        String content = "ABCDEFG";
        // write some content to the URL
        ContentWriter writer = replicatingStore.getWriter(null, contentUrl);
        writer.putContent(content);
        
        // check the content
        ContentReader reader = replicatingStore.getReader(contentUrl);
        assertTrue("Content not available from replicating store", reader.exists());
        String contentCheck = reader.getContentString();
        assertEquals("Content check failed", content, contentCheck);
        
        // check that the content has been replicated to all the stores
        for (ContentStore store : stores)
        {
            reader = store.getReader(contentUrl);
            assertTrue("Content not replicated", reader.exists());
            // check contents
            contentCheck = reader.getContentString();
            assertEquals("Replicated content incorrect", content, contentCheck);
        }
        
        // list the urls and check that they match
        List<String> globalUrls = replicatingStore.listUrls();
        assertTrue("URL of new content not present in replicating store", globalUrls.contains(contentUrl));
        // check that the URL is present for each of the stores
        checkForUrl(contentUrl, true);
        
        // delete the content
        replicatingStore.delete(contentUrl);
        // check that the deletion was removed
        checkForUrl(contentUrl, false);
    }
    
    /**
     * Checks that the url is present in each of the stores
     * 
     * @param contentUrl
     * @param mustExist true if the content must exist, false if it must <b>not</b> exist
     */
    private void checkForUrl(String contentUrl, boolean mustExist)
    {
        // check that the URL is present for each of the stores
        for (ContentStore store : stores)
        {
            List<String> urls = store.listUrls();
            assertTrue("URL of new content not present in store", urls.contains(contentUrl) == mustExist);
        }
    }
}
