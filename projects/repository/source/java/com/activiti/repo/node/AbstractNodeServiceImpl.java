package com.activiti.repo.node;

import java.util.List;

import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.qname.RegexQNamePattern;
import com.activiti.util.debug.CodeMonkey;

/**
 * Provides common functionality for {@link com.activiti.repo.node.NodeService}
 * implementations.
 * <p>
 * Some of the overloaded simpler versions of methods are implemented by passing
 * through the defaults as required.
 * <p>
 * The callback handling is also provided as a convenience for implementations.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractNodeServiceImpl implements NodeService
{
    static
    {
        CodeMonkey.todo("Add helper methods for callback functionality");
    }
    
    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getChildAssocs(NodeRef, QNamePattern)
     */
    public final List<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getChildAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }
}
