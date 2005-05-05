/**
 * Created on May 4, 2005
 */
package com.activiti.repo.version.lightweight;

import com.activiti.repo.content.ContentReader;
import com.activiti.repo.content.ContentService;
import com.activiti.repo.content.ContentStore;
import com.activiti.repo.content.ContentWriter;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.ref.NodeRef;
import com.activiti.util.AspectMissingException;
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
     * @see com.activiti.repo.content.ContentService#getReader(com.activiti.repo.ref.NodeRef)
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
     * @see com.activiti.repo.content.ContentService#getWriter(com.activiti.repo.ref.NodeRef)
     */
    public ContentWriter getWriter(NodeRef nodeRef)
    {
        // Error since the content of a versioned node can not written to
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }        
}
