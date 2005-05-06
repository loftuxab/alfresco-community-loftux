package org.alfresco.repo.content;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.springframework.dao.DataIntegrityViolationException;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;

/**
 * @see org.alfresco.repo.content.RoutingContentServiceImpl
 * 
 * @author Derek Hulley
 */
public class RoutingContentServiceImplTest extends BaseSpringTest
{
    private static final String SOME_CONTENT = "ABC";
    
    private ContentService contentService;
    private NodeService nodeService;
    private NodeRef rootNodeRef;
    private NodeRef contentNodeRef;
    
    public RoutingContentServiceImplTest()
    {
    }
    
    @Override
    public void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        nodeService = (NodeService) applicationContext.getBean("dbNodeService");
        contentService = (ContentService) applicationContext.getBean("contentService");
        // create a store and get the root node
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, getName());
        if (!nodeService.exists(storeRef))
        {
            storeRef = nodeService.createStore(storeRef.getProtocol(), storeRef.getIdentifier());
        }
        rootNodeRef = nodeService.getRootNode(storeRef);
        // create a basic node and add the content aspect
        ChildAssocRef assocRef = nodeService.createNode(
                rootNodeRef,
                QName.createQName(NamespaceService.alfresco_TEST_PREFIX, GUID.generate()),
                DictionaryBootstrap.TYPE_BASE);
        contentNodeRef = assocRef.getChildRef();
        Map<QName, Serializable> properties = nodeService.getProperties(contentNodeRef);
        properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "text/plain");
        properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-8");
        nodeService.addAspect(contentNodeRef, DictionaryBootstrap.ASPECT_CONTENT, properties);
    }
    
    private UserTransaction getUserTransaction()
    {
        return (UserTransaction) applicationContext.getBean("userTransaction");
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(contentService);
        assertNotNull(nodeService);
        assertNotNull(rootNodeRef);
        assertNotNull(contentNodeRef);
        assertNotNull(getUserTransaction());
        assertFalse(getUserTransaction() == getUserTransaction());  // ensure txn instances aren't shared 
    }
    
    public void testSimplePutContent() throws Exception
    {
        // check that the content URL property has not been set
        String contentUrl = (String) nodeService.getProperty(contentNodeRef, DictionaryBootstrap.PROP_QNAME_CONTENT_URL); 
        assertNull("Content URL should be null", contentUrl);
        
        // before the content is written, there should not be any reader available
        ContentReader reader = contentService.getReader(contentNodeRef);
        assertNull("No reader should be available for new node", reader);
        
        // get the writer
        ContentWriter writer = contentService.getWriter(contentNodeRef);
        assertNotNull("No writer received", writer);
        // write some content directly
        writer.putContent(SOME_CONTENT);
        
        // make sure that we can't reuse the writer
        try
        {
            writer.putContent("DEF");
            fail("Failed to prevent repeated use of the content writer");
        }
        catch (ContentIOException e)
        {
            // expected
        }
        
        // check that there is a reader available
        reader = contentService.getReader(contentNodeRef);
        assertNotNull("No reader available for node", reader);
        String contentCheck = reader.getContentString();
        assertEquals("Content fetched doesn't match that written", SOME_CONTENT, contentCheck);

        // check that the content URL was set
        contentUrl = (String) nodeService.getProperty(contentNodeRef, DictionaryBootstrap.PROP_QNAME_CONTENT_URL);
        assertNotNull("Content URL not set", contentUrl);
        assertEquals("Mismatched URL between writer and node", writer.getContentUrl(), contentUrl);
    }
    
    /**
     * Checks that multiple writes can occur to the same node outside of any transactions.
     * <p>
     * It is only when the streams are closed that the node is updated.
     */
    public void testConcurrentWritesNoTxn() throws Exception
    {
        // ensure that the transaction is ended - ofcourse, we need to force a commit
        setComplete();
        endTransaction();
        
        ContentWriter writer1 = contentService.getWriter(contentNodeRef);
        ContentWriter writer2 = contentService.getWriter(contentNodeRef);
        ContentWriter writer3 = contentService.getWriter(contentNodeRef);
        
        writer1.putContent("writer1 wrote this");
        writer2.putContent("writer2 wrote this");
        writer3.putContent("writer3 wrote this");

        // get the content
        ContentReader reader = contentService.getReader(contentNodeRef);
        String contentCheck = reader.getContentString();
        assertEquals("Content check failed", "writer3 wrote this", contentCheck);
    }
    
    public void testConcurrentWritesWithSingleTxn() throws Exception
    {
        // want to operate in a user transaction
        setComplete();
        endTransaction();
        
        UserTransaction txn = getUserTransaction();
        txn.begin();
        txn.setRollbackOnly();

        ContentWriter writer1 = contentService.getWriter(contentNodeRef);
        ContentWriter writer2 = contentService.getWriter(contentNodeRef);
        ContentWriter writer3 = contentService.getWriter(contentNodeRef);
        
        writer1.putContent("writer1 wrote this");
        writer2.putContent("writer2 wrote this");
        writer3.putContent("writer3 wrote this");

        // get the content
        ContentReader reader = contentService.getReader(contentNodeRef);
        String contentCheck = reader.getContentString();
        assertEquals("Content check failed", "writer3 wrote this", contentCheck);
        
        try
        {
            txn.commit();
            fail("Transaction has been marked for rollback");
        }
        catch (RollbackException e)
        {
            // expected
        }
        
        // rollback and check that the content has 'disappeared'
        txn.rollback();
        reader = contentService.getReader(contentNodeRef);
        assertNull("Transaction was rolled back - no content should be visible", reader);
    }
    
    public synchronized void testConcurrentWritesWithMultipleTxns() throws Exception
    {
        // commit node so that threads can see node
        setComplete();
        endTransaction();
        
        UserTransaction txn = getUserTransaction();
        txn.begin();
        
        // ensure that there is no content to read on the node
        ContentReader reader = contentService.getReader(contentNodeRef);
        assertNull("Reader should not be available", reader);
        
        ContentWriter threadWriter = contentService.getWriter(contentNodeRef);
        String threadContent = "Thread content";
        WriteThread thread = new WriteThread(threadWriter, threadContent);
        // kick off thread
        thread.start();
        // wait for thread to get to its wait points
        while (!thread.isWaiting())
        {
            wait(10);
        }
        // write to the content
        ContentWriter writer = contentService.getWriter(contentNodeRef);
        writer.putContent(SOME_CONTENT);
        
        // fire thread up again
        synchronized(threadWriter)
        {
            threadWriter.notifyAll();
        }
        // thread is released - but we have to wait for it to complete
        while (!thread.isDone())
        {
            wait(10);
        }
        // the thread has finished and has committed its changes - check the visibility
        reader = contentService.getReader(contentNodeRef);
        assertNotNull("Reader should now be available", reader);
        String checkContent = reader.getContentString();
        assertEquals("Content check failed", SOME_CONTENT, checkContent);
        
        // now commit this transaction
        try
        {
            txn.commit();
            fail("Multiple commits after inserts of same data should have been detected");
        }
        catch (DataIntegrityViolationException e)
        {
            // expected
        }
        // failed commit - rollback must be automatic
        assertEquals("Expected transaction to have been rolled back", Status.STATUS_ROLLEDBACK, txn.getStatus());
        
        // check content has taken on thread's content
        reader = contentService.getReader(contentNodeRef);
        assertNotNull("Reader should now be available", reader);
        checkContent = reader.getContentString();
        assertEquals("Content check failed", threadContent, checkContent);
    }
    
    /**
     * Writes some content to the writer's output stream and then aquires
     * a lock on the writer, waits until notified and then closes the
     * output stream before terminating.
     * <p>
     * When firing thread up, be sure to call <code>notify</code> on the
     * writer in order to let the thread run to completion.
     */
    private class WriteThread extends Thread
    {
        private ContentWriter writer;
        private String content;
        private boolean isWaiting;
        private boolean isDone;
        
        public WriteThread(ContentWriter writer, String content)
        {
            this.writer = writer;
            this.content = content;
            isWaiting = false;
            isDone = false;
        }
        
        public boolean isWaiting()
        {
            return isWaiting;
        }
        
        public boolean isDone()
        {
            return isDone;
        }

        public void run()
        {
            isWaiting = false;
            isDone = false;
            UserTransaction txn = getUserTransaction();
            OutputStream os = writer.getContentOutputStream();
            try
            {
                txn.begin();    // not testing transactions - this is not a safe pattern
                // put the content
                os.write(content.getBytes());
                synchronized (writer)
                {
                    isWaiting = true;
                    writer.wait();   // wait until notified
                }
                os.close();
                txn.commit();
            }
            catch (Throwable e)
            {
                throw new RuntimeException("Failed writing to output stream for writer: " + writer, e);
            }
            finally
            {
                if (os != null)
                {
                    try { os.close(); } catch (IOException e) {}
                }
            }
            isDone = true;
        }
    }
}
