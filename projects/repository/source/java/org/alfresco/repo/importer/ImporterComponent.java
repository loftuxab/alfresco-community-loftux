package org.alfresco.repo.importer;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
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
     * @see org.alfresco.repo.importer.ImporterService#importNodes(java.io.InputStream, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, org.alfresco.repo.importer.ImporterProgress)
     */
    public void importNodes(InputStream inputStream, NodeRef parentRef, QName childAssocType, ImporterProgress progress)
    {
        ParameterCheck.mandatory("Input stream", inputStream);
        ParameterCheck.mandatory("Parent", parentRef);
        ParameterCheck.mandatory("Child association type", childAssocType);

        viewImporter.importNodes(inputStream, parentRef, childAssocType, progress);
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImporterService#importNodes(java.io.InputStream, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.repo.importer.ImporterProgress)
     */
    public void importNodes(InputStream inputStream, NodeRef parentRef, ImporterProgress progress)
    {
        ParameterCheck.mandatory("Parent", parentRef);

        // Determine if only one child association type exists
        QName nodeType = nodeService.getType(parentRef);
        Set<QName> nodeAspects = nodeService.getAspects(parentRef);
        TypeDefinition anonymousType = dictionaryService.getAnonymousType(nodeType, nodeAspects);
        Map<QName, ChildAssociationDefinition> childAssocDefs = anonymousType.getChildAssociations();
        if (childAssocDefs.size() > 1)
        {
            throw new ImporterException("Can not determine child association type to use - Parent " + parentRef + " supports multiple child association types: " + childAssocDefs.toString());
        }
            
        // Extract child association type to use and import
        QName childAssocType = childAssocDefs.keySet().iterator().next();
        importNodes(inputStream, parentRef, childAssocType, progress);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImporterService#importNodes(java.io.InputStream, org.alfresco.service.cmr.repository.StoreRef, org.alfresco.repo.importer.ImporterProgress)
     */
    public void importNodes(InputStream inputStream, StoreRef storeRef, ImporterProgress progress)
    {
        ParameterCheck.mandatory("Store Reference", storeRef);
        
        // Import under root node
        NodeRef rootRef = nodeService.getRootNode(storeRef);
        importNodes(inputStream, rootRef, progress);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.ImporterService#importNodes(java.io.InputStream, org.alfresco.service.cmr.repository.StoreRef, java.lang.String, org.alfresco.service.namespace.QName, org.alfresco.repo.importer.ImporterProgress)
     */
    public void importNodes(InputStream inputStream, StoreRef storeRef, String parentPath, QName childAssocType, ImporterProgress progress)
    {
        ParameterCheck.mandatory("Parent Path", parentPath);

        // Select node based on specified path
        NodeRef rootRef = nodeService.getRootNode(storeRef);
        List<ChildAssociationRef> childAssocRefs = nodeService.selectNodes(rootRef, parentPath, null, namespaceService, false);
        if (childAssocRefs.size() == 1)
        {
            throw new ImporterException("Path " + parentPath + " with store " + storeRef + " did not find a parent - the path must resolve to one parent");
        }
        if (childAssocRefs.size() > 1)
        {
            throw new ImporterException("Path " + parentPath + " with store " + storeRef + " found too many parents - the path must resolve to one parent");
        }

        // Extract parent to import under
        ChildAssociationRef childAssocRef = childAssocRefs.get(0);
        NodeRef parentRef = childAssocRef.getChildRef();
        importNodes(inputStream, parentRef, childAssocType, progress);
    }
    
}
