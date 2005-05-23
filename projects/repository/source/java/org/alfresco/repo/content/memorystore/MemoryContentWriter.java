package org.alfresco.repo.content.memorystore;

import java.io.OutputStream;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;

/**
 * A writer onto a <tt>byte[]</tt> instance in memory.
 * <p>
 * There is no permament persistence of the content and this class should
 * therefore only be used for the most transient of content.
 * 
 * @author Derek Hulley
 */
public class MemoryContentWriter extends AbstractContentWriter
{
    private byte[] content;
    
    /**
     * @param contentUrl the URL by which the content can be accessed
     */
    public MemoryContentWriter(String contentUrl)
    {
        super(contentUrl);
    }
    
    /**
     * Get the physical content.  This operation can only be used once the content
     * writing stream has been closed.
     * 
     * @return Returns the memory-resident content
     */
    public byte[] getContent()
    {
        if (!isClosed())
        {
            throw new AlfrescoRuntimeException("The content stream has not been closed");
        }
        return content;
    }
    
    /**
     * @return Returns a <tt>ByteArrayOutputStream</tt> directly onto the
     *      underlying {@link #content}.
     */
    protected OutputStream getDirectOutputStream() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Abstract base class takes care of all considerations other than construction
     */
    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }
}
