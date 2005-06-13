/**
 * Created on Apr 19, 2005
 */
package org.alfresco.service.cmr.lock;

import java.text.MessageFormat;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Runtime exception class
 * 
 * @author Roy Wetherall
 */
public class UnableToReleaseLockException extends
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
    public UnableToReleaseLockException(NodeRef nodeRef)
    {
        super(MessageFormat.format(ERROR_MESSAGE, new Object[]{nodeRef.getId()}));
    }
}
