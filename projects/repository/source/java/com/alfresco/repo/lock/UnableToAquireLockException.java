/**
 * Created on Apr 19, 2005
 */
package org.alfresco.repo.lock;

import java.text.MessageFormat;

import org.alfresco.repo.ref.NodeRef;

/**
 * @author Roy Wetherall
 */
public class UnableToAquireLockException extends RuntimeException
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3258689892710889781L;
    
    /**
     * Error message
     */
    private final static String ERROR_MESSAGE = "The node (id: {0})could not be locked since it" +
            " is already locked by antoher user.";

    /**
     * Constructor
     */
    public UnableToAquireLockException(NodeRef nodeRef)
    {
        super(MessageFormat.format(ERROR_MESSAGE, new Object[]{nodeRef.getId()}));
    }
}
