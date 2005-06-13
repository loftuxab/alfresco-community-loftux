package org.alfresco.repo.content.filestore;

import java.io.File;

import org.alfresco.repo.content.AbstractContentReadWriteTest;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.util.TempFileProvider;

/**
 * Tests read and write functionality for the store.
 * 
 * @see org.alfresco.repo.content.filestore.FileContentStore
 * 
 * @author Derek Hulley
 */
public class FileContentStoreTest extends AbstractContentReadWriteTest
{
    private ContentStore store;
    private ContentReader reader;
    private ContentWriter writer;
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // create a store that uses a subdirectory of the temp directory
        File tempDir = TempFileProvider.getTempDir();
        store = new FileContentStore(
                tempDir.getAbsolutePath() +
                File.separatorChar +
                getName());
        
        writer = store.getWriter();
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
    protected ContentStore getStore()
    {
        return store;
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
