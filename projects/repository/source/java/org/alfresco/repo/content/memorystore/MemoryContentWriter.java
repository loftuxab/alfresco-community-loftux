package org.alfresco.repo.content.memorystore;

import java.nio.channels.WritableByteChannel;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;

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
     * Abstract base class takes care of all considerations other than construction
     */
    @Override
    protected ContentReader createReader() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }

    protected WritableByteChannel getDirectWritableChannel() throws ContentIOException
    {
        throw new UnsupportedOperationException();
    }
}
