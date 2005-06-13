package org.alfresco.repo.content.filestore;

import java.io.File;

import org.alfresco.repo.content.AbstractContentReadWriteTest;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;

/**
 * Tests that the file reader and writer work
 * 
 * @see org.alfresco.repo.content.filestore.FileContentReader
 * @see org.alfresco.repo.content.filestore.FileContentWriter
 * 
 * @author Derek Hulley
 */
public class FileContentReadWriteTest extends AbstractContentReadWriteTest
{
    private File file;
    
    public FileContentReadWriteTest()
    {
        super();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        file = File.createTempFile(getName(), ".txt");
        file.deleteOnExit();
    }

    @Override
    protected ContentStore getStore()
    {
        return null;
    }
    
    @Override
    protected ContentReader getReader()
    {
        return new FileContentReader(file);
    }

    @Override
    protected ContentWriter getWriter()
    {
        return new FileContentWriter(file);
    }
}
