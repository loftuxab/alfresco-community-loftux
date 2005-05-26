/*
 * Created on 27-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene;

import org.alfresco.error.AlfrescoRuntimeException;

public class QueryParameterisationException extends AlfrescoRuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryParameterisationException(String msg)
    {
        super(msg);
    }

    public QueryParameterisationException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}
