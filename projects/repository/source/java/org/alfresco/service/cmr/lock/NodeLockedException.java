/**
 * Created on Apr 19, 2005
 */
package org.alfresco.service.cmr.lock;

import java.text.MessageFormat;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Node locked exception class
 * 
 * @author Roy Wetherall
 */
public class NodeLockedException extends RuntimeException
{    
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3762254149525582646L;
    
    /**
     * Error message
     */
    private static final String ERROR_MESSAGE = "Can not perform operation since " +
            "the node (id:{0}) is locked by another user.";

    /**
     * @param message
     */
    public NodeLockedException(NodeRef nodeRef)
    {
        super(MessageFormat.format(ERROR_MESSAGE, new Object[]{nodeRef.getId()}));
    }   
}
