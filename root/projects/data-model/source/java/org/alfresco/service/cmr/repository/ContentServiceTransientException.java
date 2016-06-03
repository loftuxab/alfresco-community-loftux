package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;   

/**
 * This exception should be thrown when a content operation could not be performed due to
 * a transient condition and where it is possible that a subsequent request to execute the
 * same action might succeed, all other things not having changed.
 * <p/>
 * An example of this would be the case where a request to create a thumbnail
 * has failed because the necessary thumbnailing software is not available e.g. because the OpenOffice.org process
 * is not currently running.
 * 
 * @author Neil Mc Erlean
 * @since 4.0.1
 */
@AlfrescoPublicApi
public class ContentServiceTransientException extends ContentIOException
{
    private static final long serialVersionUID = 3258130249983276087L;
    
    public ContentServiceTransientException(String msg)
    {
        super(msg);
    }
    
    public ContentServiceTransientException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
