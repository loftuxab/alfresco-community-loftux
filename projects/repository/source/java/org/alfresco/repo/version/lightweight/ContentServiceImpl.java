/**
 * Created on May 4, 2005
 */
package org.alfresco.repo.version.lightweight;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.ref.NodeRef;
;

/**
 * Lightweight version store content service implementation
 * 
 * @author Roy Wetherall
 */
public class ContentServiceImpl implements ContentService 
{
    /**
     * Error messages
     */
    private final static String MSG_UNSUPPORTED = 
        "This operation is not supported by a version store implementation of the content service.";   
    	
	/**
	 * The content service
	 */
	protected ContentService contentService;

	/**
	 * Set the content service
	 * 
	 * @param contentService  the content service
	 */
	public void setContentService(ContentService contentService) 
	{
		this.contentService = contentService;
	}
	
    /**
     * @see org.alfresco.repo.content.ContentService#getReader(org.alfresco.repo.ref.NodeRef)
     */
    public ContentReader getReader(NodeRef nodeRef)
    {
		// Delegate the call to the content service
		return this.contentService.getReader(nodeRef);
    }

	/**
     * @throws UnsupportedOperationException the content cannot be written to
     */
    public ContentWriter getWriter(NodeRef nodeRef)
	{
        // Error since the content of a versioned node can not written to
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
	}

	/**
     * @throws UnsupportedOperationException the content cannot be written to
     */
    public ContentWriter getUpdatingWriter(NodeRef nodeRef)
    {
        // Error since the content of a versioned node can not written to
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Returns a writer onto an anonymous location
     */
    public ContentWriter getTempWriter()
    {
        //return versionContentStore.getWriter();
		return this.contentService.getTempWriter();
    }        
}
