package org.alfresco.repo.content.memorystore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;

/**
 * A reader onto <tt>byte[]</tt> instances in memory.
 * <p>
 * There is no permament persistence of the content and this class should
 * therefore only be used for the most transient of content.
 * 
 * @author Derek Hulley
 */
public class MemoryContentReader extends AbstractContentReader
{
    private byte[] content;
    
    /**
     * @param contentUrl the URL by which the content can be accessed
     * @param content the physical bytes making up the the content
     */
    public MemoryContentReader(String contentUrl, byte[] content)
    {
        super(contentUrl);
        this.content = content;
    }

    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        return new MemoryContentReader(this.getContentUrl(), this.content);
    }
    
    /**
     * @return Returns a <tt>ByteArrayInputStream</tt> directly onto the
     *      underlying {@link #content}.
     */
    protected InputStream getDirectInputStream() throws ContentIOException
    {
        return new ByteArrayInputStream(content);
    }
}
