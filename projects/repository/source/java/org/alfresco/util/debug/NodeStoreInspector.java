/**
 * Created on Apr 22, 2005
 */
package org.alfresco.util.debug;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.ref.qname.RegexQNamePattern;

/**
 * Debug class that has methods to inspect the contents of a node store.
 * 
 * @author Roy Wetherall
 */
public class NodeStoreInspector
{
    /**
     * Dumps the contents of a store to a string.
     * 
     * @param nodeService   the node service
     * @param storeRef      the store reference
     * @return              string containing textual representation of the contents of the store
     */
    public static String dumpNodeStore(NodeService nodeService, StoreRef storeRef)
    {
        StringBuilder builder = new StringBuilder();
        
        if (nodeService.exists(storeRef) == true)
        {
            NodeRef rootNode = nodeService.getRootNode(storeRef);            
            builder.append(outputNode(0, nodeService, rootNode));            
        }
        else
        {
            builder.
                append("The store ").
                append(storeRef.toString()).
                append(" does not exist.");
        }
        
        return builder.toString();
    }
    
    /**
     * Output the node 
     * 
     * @param iIndent
     * @param nodeService
     * @param nodeRef
     * @return
     */
    private static String outputNode(int iIndent, NodeService nodeService, NodeRef nodeRef)
    {
        StringBuilder builder = new StringBuilder();
        
		try
		{
	        QName nodeType = nodeService.getType(nodeRef);
	        builder.
	            append(getIndent(iIndent)).
	            append("node: ").
	            append(nodeRef.getId()).
	            append(" (").
	            append(nodeType.getLocalName());
			
			Collection<QName> aspects = nodeService.getAspects(nodeRef);
			for (QName aspect : aspects) 
			{
				builder.
					append(", ").
					append(aspect.getLocalName());
			}
			
	        builder.append(")\n");        
		
	        Map<QName, Serializable> props = nodeService.getProperties(nodeRef);
	        for (QName name : props.keySet())
	        {
				String valueAsString = "null";
				Serializable value = props.get(name);
				if (value != null)
				{
					valueAsString = value.toString();
				}
				
	            builder.
	                append(getIndent(iIndent+1)).
	                append("@").
	                append(name.getLocalName()).
	                append(" = ").
	                append(valueAsString).
	                append("\n");
	            
	        }
	        
	        try
	        {
	            Collection<ChildAssocRef> childAssocRefs = nodeService.getChildAssocs(nodeRef);
	            for (ChildAssocRef childAssocRef : childAssocRefs)
	            {
	                builder.
	                    append(getIndent(iIndent+1)).
	                    append("-> ").
	                    append(childAssocRef.getQName().getLocalName()).
	                    append("\n");
	                
	                builder.append(outputNode(iIndent+2, nodeService, childAssocRef.getChildRef()));
	            }
	        }
	        catch (Exception exception)
	        {
	            // Ignore for now since this means it is not a container type
				exception.printStackTrace();
	        }
			
			try
	        {
	            Collection<NodeAssocRef> assocRefs = nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
	            for (NodeAssocRef assocRef : assocRefs)
	            {
	                builder.
	                    append(getIndent(iIndent+1)).
	                    append("-> associated to ").
	                    append(assocRef.getTargetRef().getId()).
	                    append("\n");
	            }
	        }
	        catch (Exception exception)
	        {
	            // Ignore for now since this means it is not a container type
				exception.printStackTrace();
	        }
		}
		catch (InvalidNodeRefException invalidNode)
		{
		}
        
        return builder.toString();
    }
    
    /**
     * Get the indent
     * 
     * @param iIndent  the indent value
     * @return         the indent string
     */
    private static String getIndent(int iIndent)
    {
        StringBuilder builder = new StringBuilder(iIndent*3);
        for (int i = 0; i < iIndent; i++)
        {
            builder.append("   ");            
        }
        return builder.toString();
    }

}
