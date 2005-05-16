/**
 * Created on May 4, 2005
 */
package org.alfresco.repo.version.lightweight;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.RoutingContentServiceImpl;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.util.AspectMissingException;
;

/**
 * Lightweight version store content service implementation
 * 
 * @author Roy Wetherall
 */
public class ContentServiceImpl extends BaseImpl implements ContentService 
{
    /**
     * Error messages
     */
    private final static String MSG_UNSUPPORTED = 
        "This operation is not supported by a version store implementation of the content service.";   
    
    /**
     * The version content store
     */
    private ContentStore versionContentStore;
    
    /**
     * Sets the version content store
     * 
     * @param versionContentStore  the version content store
     */
    public void setVersionContentStore(ContentStore versionContentStore)
    {
        this.versionContentStore = versionContentStore;
    }
    
    /**
     * @see org.alfresco.repo.content.ContentService#getReader(org.alfresco.repo.ref.NodeRef)
     */
    public ContentReader getReader(NodeRef nodeRef)
    {
        ContentReader reader = null;
        
        // Check that the content aspect is present
        if (dbNodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_CONTENT) == false)
        {
            throw new AspectMissingException(DictionaryBootstrap.ASPECT_CONTENT, nodeRef);
        }
        
        // Get the content URL
        String contentUrl = (String) this.dbNodeService.getProperty(
                nodeRef,
                DictionaryBootstrap.PROP_QNAME_CONTENT_URL);
        
        // check that the URL is available
        if (contentUrl != null)
        {
            reader = versionContentStore.getReader(contentUrl);
        }
        
        return reader;
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
     * Returns a writer onto an imaginary temporary node reference
     */
    public ContentWriter getTempWriter()
    {
        return getWriter(RoutingContentServiceImpl.TEMP_NODEREF);
    }        
}
