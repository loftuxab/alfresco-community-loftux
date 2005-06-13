package org.alfresco.service.cmr.repository;

/**
 * Listens for notifications w.r.t. content.  This includes receiving notifications
 * of the opening and closing of the content streams.
 * 
 * @author Derek Hulley
 */
public interface ContentStreamListener
{
    /**
     * Called when the stream associated with a reader or writer is closed
     * 
     * @throws ContentIOException
     */
    public void contentStreamClosed() throws ContentIOException;
}
