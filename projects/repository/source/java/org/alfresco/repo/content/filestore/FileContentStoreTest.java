package org.alfresco.repo.content.filestore;

import java.io.File;

import org.alfresco.repo.content.AbstractContentReadWriteTest;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.StoreRef;

/**
 * Tests read and write functionality for the store.
 * 
 * @see org.alfresco.repo.content.filestore.FileContentStore
 * 
 * @author Derek Hulley
 */
public class FileContentStoreTest extends AbstractContentReadWriteTest
{
    private ContentReader reader;
    private ContentWriter writer;
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // create a writer to a temp file and then use the URL that the writer
        // has for the URL of the reader
        File tempFile = File.createTempFile(getName(), ".tmp");
        File tempDir = tempFile.getParentFile();
        assertTrue(tempDir.isDirectory());
        ContentStore store = new FileContentStore(tempDir.getAbsolutePath());
        
        StoreRef storeRef = new StoreRef(getName(), "test");
        NodeRef nodeRef = new NodeRef(storeRef, "GUID-12345");

        writer = store.getWriter(nodeRef);
        String contentUrl = writer.getContentUrl();
        reader = store.getReader(contentUrl);
    }

    public void testSetUp() throws Exception
    {
        super.testSetUp();
        assertNotNull(reader);
        assertNotNull(writer);
    }

    @Override
    protected ContentReader getReader()
    {
        return reader;
    }

    @Override
    protected ContentWriter getWriter()
    {
        return writer;
    }
}
