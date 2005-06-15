package org.alfresco.repo.content;

import java.nio.channels.FileChannel;

import org.alfresco.service.cmr.repository.ContentIOException;

/**
 * Supplementary interface for content readers and writers that allow random-access to
 * the underlying content.
 * <p>
 * The use of this interface by a client <b>may</b> preclude the use of any other
 * access to the underlying content - this depends on the underlying implementation.
 * 
 * @author Derek Hulley
 */
public interface RandomAccessContent
{
    /**
     * @return Returns true if the content can be written to 
     */
    public boolean canWrite();
    
    /**
     * Get a channel to access the content.  The channel's behaviour is similar to that
     * when a <tt>FileChannel</tt> is retrieved using {@link java.io.RandomAccessFile#getChannel()}.
     * 
     * @return Returns a channel to access the content
     * @throws ContentIOException
     *
     * @see #canWrite()
     * @see java.io.RandomAccessFile#getChannel()
     */
    public FileChannel getChannel() throws ContentIOException;
}
