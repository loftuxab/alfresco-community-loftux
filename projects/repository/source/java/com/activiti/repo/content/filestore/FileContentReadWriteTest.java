package com.activiti.repo.content.filestore;

import java.io.File;

import com.activiti.repo.content.AbstractContentReadWriteTest;
import com.activiti.repo.content.ContentReader;
import com.activiti.repo.content.ContentWriter;

/**
 * Tests that the file reader and writer work
 * 
 * @see com.activiti.repo.content.filestore.FileContentReader
 * @see com.activiti.repo.content.filestore.FileContentWriter
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
