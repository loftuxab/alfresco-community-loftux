package org.alfresco.repo.policy;


/**
 * Base Exception of Policy Exceptions.
 * 
 * @author David Caruana
 */
public class PolicyException extends RuntimeException
{
    private static final long serialVersionUID = 3761122726173290550L;

    
    public PolicyException(String msg)
    {
       super(msg);
    }
    
    public PolicyException(String msg, Throwable cause)
    {
       super(msg, cause);
    }

}
