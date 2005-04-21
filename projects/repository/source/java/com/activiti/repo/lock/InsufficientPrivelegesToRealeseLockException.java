/**
 * Created on Apr 19, 2005
 */
package com.activiti.repo.lock;

import java.text.MessageFormat;

import com.activiti.repo.ref.NodeRef;

/**
 * Runtime exception class
 * 
 * @author Roy Wetherall
 */
public class InsufficientPrivelegesToRealeseLockException extends
        RuntimeException
{
    /**
     * Serial verison UID
     */
    private static final long serialVersionUID = 3257565088071432243L;
    
    /**
     * Error message
     */
    private static final String ERROR_MESSAGE = 
        "You have insufficent priveleges to realese the " +
        "lock on the node (id: {0}).  The node is locked by " +
        "another user.";

    /**
     * Constructor
     */
    public InsufficientPrivelegesToRealeseLockException(NodeRef nodeRef)
    {
        super(MessageFormat.format(ERROR_MESSAGE, new Object[]{nodeRef.getId()}));
    }
}
