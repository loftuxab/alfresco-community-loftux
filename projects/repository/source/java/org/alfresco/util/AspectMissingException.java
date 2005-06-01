/**
 * Created on Apr 22, 2005
 */
package org.alfresco.util;

import java.text.MessageFormat;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Used to indicate that an aspect is missing from a node.
 * 
 * @author Roy Wetherall
 */
public class AspectMissingException extends RuntimeException
{
    private static final long serialVersionUID = 3257852099244210228L;
    
    private QName missingAspect;
    private NodeRef nodeRef;

    /**
     * Error message
     */
    private static final String ERROR_MESSAGE = "The {0} aspect is missing from this node (id: {1}).  " +
            "It is required for this operation.";
    
    /**
     * Constructor
     */
    public AspectMissingException(QName missingAspect, NodeRef nodeRef)
    {
        super(MessageFormat.format(ERROR_MESSAGE, new Object[]{missingAspect.toString(), nodeRef.getId()}));
        this.missingAspect = missingAspect;
        this.nodeRef = nodeRef;
    }

    public QName getMissingAspect()
    {
        return missingAspect;
    }
    
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
}
