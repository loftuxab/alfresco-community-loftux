package org.alfresco.repo.content;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a handle to write specific content.
 * <p>
 * Implementations of this interface <b>might</b> be <code>Serializable</code>
 * but client code could should check suitability before attempting to serialize
 * it.
 * <p>
 * Implementations that are able to provide inter-VM streaming, such as accessing
 * WebDAV, would be <code>Serializable</code>.  An accessor that has to access a
 * local file on the server could not provide inter-VM streaming unless it specifically
 * makes remote calls and opens sockets, etc.
 * 
 * @see org.alfresco.repo.content.ContentReader
 * 
 * @author Derek Hulley
 */
public interface ContentWriter
{
    /**
     * Use this method to register any interest in the 
     * {@link #getContentOutputStream() output stream}.
     * <p>
     * This method can only be used before the output stream has been
     * retrieved.
     * 
     * @param listener a listener that will be called for output stream
     *      event notification
     */
    public void addListener(ContentStreamListener listener);
    
    /**
     * @return Returns a URL identifying the specific location of the content.
     *      The URL must identify, within the context of the originating content
     *      store, the exact location of the content.
     * @throws ContentIOException
     */
    public String getContentUrl() throws ContentIOException;
    
    /**
     * Provides low-level access to write to repository content.
     * <p>
     * The stream returned to the client should remain open (subject to timeouts)
     * until closed by the client.  All lock detection, read-only access and other
     * concurrency issues are dealt with during this operation.  It remains
     * possible that implementations will throw exceptions when the stream is closed.
     * <p>
     * The stream will notify any listeners according to the listener interface.
     * <p>
     * For transactional purposes, only the thread that actually performs the
     * {@link OutputStream#write(int) write} operations needs to be engaged in
     * a transaction.  The thread that aquires an instance of this writer will
     * usually be the same thread that pushes the content to the repository - this
     * is definitely true for all the <code>putXXX</code> convenience methods.
     * There is, however, no reason why the stream could not be passed to another
     * thread for writing.  It is up to the thread that ultimately calls
     * {@link OutputStream#close() close} to be engaged in a transaction.
     * 
     * @return Returns a stream with which to write content
     * @throws ContentIOException
     */
    public OutputStream getContentOutputStream() throws ContentIOException;
    
    /**
     * Puts content to the repository
     * <p>
     * All resources will be closed automatically.
     * 
     * @param is the input stream from which the content will be read
     * @throws ContentIOException
     * 
     * @see #getContentOutputStream()
     */
    public void putContent(InputStream is) throws ContentIOException;
    
    /**
     * Puts content to the repository direct from file
     * <p>
     * All resources will be closed automatically.
     * 
     * @param file the file to load the content from
     * @throws ContentIOException
     * 
     * @see #getContentOutputStream()
     */
    public void putContent(File file) throws ContentIOException;
    
    /**
     * Puts content to the repository direct from <code>String</code>
     * <p>
     * All resources will be closed automatically.
     * 
     * @param content a string representation of the content
     * @throws ContentIOException
     * 
     * @see #getContentOutputStream()
     */
    public void putContent(String content) throws ContentIOException;
}
