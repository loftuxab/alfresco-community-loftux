package org.alfresco.repo.importer;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;


/**
 * Default implementation of the Importer Service
 *  
 * @author David Caruana
 *
 */
public class ImporterComponent
    implements ImporterService
{
    // Default importer
    // TODO: Allow registration of plug-in importers (by namespace)
    private Importer viewImporter;

    // Supporting services
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;

    
    /**
     * @param viewImporter  the default importer
     */
    public void setViewImporter(Importer viewImporter)
    {
        this.viewImporter = viewImporter;
    }
    
    /**
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * @param namespaceService  the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImporterService#importNodes(java.io.InputStream, org.alfresco.repo.importer.Location, java.util.Properties, org.alfresco.repo.importer.Progress)
     */
    public void importNodes(InputStream inputStream, Location location, Properties configuration, Progress progress)
    {
        ParameterCheck.mandatory("Input stream", inputStream);
        ParameterCheck.mandatory("Location", location);
        
        // Establish node to import within
        NodeRef nodeRef = location.getNodeRef();
        if (nodeRef == null)
        {
            // If a specific node has not been provided, default to the root
            nodeRef = nodeService.getRootNode(location.getStoreRef());
        }
        
        // Resolve to path within node, if one specified
        String path = location.getPath();
        if (path != null && path.length() >0)
        {
            List<NodeRef> nodeRefs = nodeService.selectNodes(nodeRef, path, null, namespaceService, false);
            if (nodeRefs.size() == 0)
            {
                throw new ImporterException("Path " + path + " with node " + nodeRef + " does not exist - the path must resolve to a valid location");
            }
            if (nodeRefs.size() > 1)
            {
                throw new ImporterException("Path " + path + " with node " + nodeRef + " found too many locations - the path must resolve to one location");
            }
            nodeRef = nodeRefs.get(0);
        }
        
        // TODO: Check Node actually exists
        
        // Establish child association type to import under
        QName childAssocType = location.getChildAssocType();
        if (childAssocType == null)
        {
            // Determine if only one child association type exists
            QName nodeType = nodeService.getType(nodeRef);
            Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
            TypeDefinition anonymousType = dictionaryService.getAnonymousType(nodeType, nodeAspects);
            Map<QName, ChildAssociationDefinition> childAssocDefs = anonymousType.getChildAssociations();
            if (childAssocDefs.size() > 1)
            {
                throw new ImporterException("Can not determine child association type to use - location " + nodeRef + " supports multiple child association types: " + childAssocDefs.toString());
            }
            childAssocType = childAssocDefs.keySet().iterator().next();
        }

        // Perform import
        viewImporter.importNodes(inputStream, nodeRef, childAssocType, configuration, progress);
    }
    
    
}
