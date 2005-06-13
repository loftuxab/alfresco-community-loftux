package org.alfresco.service.cmr.repository;


/**
 * A <tt>Content</tt> is a handle onto a specific content location.
 * 
 * @author Derek Hulley
 */
public interface Content
{    
    /**
     * @return Returns a URL identifying the specific location of the content.
     *      The URL must identify, within the context of the originating content
     *      store, the exact location of the content.
     * @throws ContentIOException
     */
    public String getContentUrl() throws ContentIOException;
    
    /**
     * Gets content's mimetype.
     * 
     * @return Returns a standard mimetype for the content or null if the mimetype
     *      is unkown
     */
    public String getMimetype();
    
    /**
     * Sets the content's mimetype.
     * 
     * @param mimetype the standard mimetype for the content - may be null
     */
    public void setMimetype(String mimetype);
    
    /**
     * Gets the content's encoding.
     * 
     * @return Returns a valid Java encoding, typically a character encoding, or
     *      null if the encoding is unkown
     */
    public String getEncoding();
    
    /**
     * Sets the content's encoding.
     * 
     * @param encoding a valid Java encoding, typically a character encoding -
     *      may be null
     */
    public void setEncoding(String encoding);
}
