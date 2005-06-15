/*
 * Created on 14-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

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
