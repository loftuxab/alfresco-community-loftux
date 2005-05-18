/*
 * Created on 18-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.node;

import org.alfresco.repo.dictionary.NamespaceService;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

public class NodeServiceXPath extends BaseXPath
{

    /**
     * 
     */
    private static final long serialVersionUID = 3834032441789592882L;

    public NodeServiceXPath(String arg0, NodeService nodeService, NamespaceService namespaceService) throws JaxenException
    {
        super(arg0, new DocumentNavigator(nodeService, namespaceService));
       
    }
}
