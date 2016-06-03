package org.alfresco.repo.security.authentication;

import org.alfresco.api.AlfrescoPublicApi;

@AlfrescoPublicApi
public class TicketExpiredException extends AuthenticationException
{

    /**
     * 
     */
    private static final long serialVersionUID = 3257572801815590969L;

    public TicketExpiredException(String msg)
    {
        super(msg);
    }

    public TicketExpiredException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}
