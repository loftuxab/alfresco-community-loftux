package org.alfresco.service.cmr.repository;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Represents a handle to read specific content.
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
 * @see org.alfresco.service.cmr.repository.ContentWriter
 * 
 * @author Derek Hulley
 */
public interface ContentReader extends Content
{
    /**
     * Use this method to register any interest in the 
     * {@link #getContentInputStream() input stream}.
     * <p>
     * This method can only be used before the input stream has been
     * retrieved.
     * 
     * @param listener a listener that will be called for input stream
     *      event notification
     */
    public void addListener(ContentStreamListener listener);
    
    /**
     * Convenience method to get another reader onto the underlying content.
     * 
     * @return Returns a reader onto the underlying content
     * @throws ContentIOException
     */
    public ContentReader getReader() throws ContentIOException;
    
    /**
     * Check if the {@link Content#getContentUrl() underlying content} is present.
     * 
     * @return Returns true if there is content at the URL refered to by this reader
     */
    public boolean exists();
    
    /**
     * Gets the length of the content that this reader references.
     * 
     * @return Returns the document byte length, or <code>OL</code> if the
     *      content doesn't {@link #exists() exist}.
     */
    public long getLength();

    /**
     * Provides low-level access to the underlying content.
     * <p>
     * Once the stream is provided to a client it should remain active
     * (subject to any timeouts) until closed by the client.
     * 
     * @return Returns a stream that can be read at will, but must be closed when completed
     * @throws ContentIOException
     */
    public InputStream getContentInputStream() throws ContentIOException;
    
    /**
     * Convenience method to find out if the input stream has been closed.
     * Once closed, the input stream cannot be reused.  This method could
     * be used to wait for a particular read operation to complete, for example.
     * 
     * @return Return true if the content input stream has been used and closed
     *      otherwise false.
     */
    public boolean isClosed();
    
    /**
     * Gets content from the repository.
     * <p>
     * All resources will be closed automatically.
     * <p>
     * Care must be taken that the bytes read from the stream are properly
     * decoded according to the {@link Content#getEncoding() encoding}
     * property.
     * 
     * @param os the stream to which to write the content
     * @throws ContentIOException
     * 
     * @see #getContentInputStream()
     */
    public void getContent(OutputStream os) throws ContentIOException;
    
    /**
     * Gets content from the repository direct to file
     * <p>
     * All resources will be closed automatically.
     * 
     * @param file the file to write the content to - it will be overwritten
     * @throws ContentIOException
     * 
     * @see #getContentInputStream()
     */
    public void getContent(File file) throws ContentIOException;

    /**
     * Gets content from the repository direct to <code>String</code>.
     * <p>
     * If the {@link Content#getEncoding() encoding } is known then it will be used
     * otherwise the default system <tt>byte[]</tt> to <tt>String</tt> conversion
     * will be used.
     * <p>
     * All resources will be closed automatically.
     * <p>
     * <b>WARNING: </b> This should only be used when the size of the content
     *                  is known in advance.
     * 
     * @return Returns a String representation of the content
     * @throws ContentIOException
     * 
     * @see #getContentInputStream()
     * @see String#String(byte[])
     */
    public String getContentString() throws ContentIOException;
}
