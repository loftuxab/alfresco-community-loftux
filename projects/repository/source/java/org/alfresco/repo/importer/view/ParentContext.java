package org.alfresco.repo.importer.view;

import java.util.Map;

import org.alfresco.repo.importer.ImporterException;
import org.alfresco.repo.importer.ImporterProgress;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Maintains state about the parent context of the node being imported.
 * 
 * @author David Caruana
 *
 */
/*package*/ class ParentContext extends ElementContext
{
    private NodeRef parentRef;
    private QName assocType;

    
    /**
     * Construct
     * 
     * @param dictionary
     * @param progress
     * @param elementName
     * @param parentRef
     * @param assocType
     */
    /*package*/ ParentContext(DictionaryService dictionary, ImporterProgress progress, QName elementName, NodeRef parentRef, QName assocType)
    {
        super(dictionary, elementName, progress);
        this.parentRef = parentRef;
        this.assocType = assocType;
    }
    
    /**
     * Construct 
     * 
     * @param elementName
     * @param parent
     * @param childDef
     */
    /*package*/ ParentContext(QName elementName, NodeContext parent, ChildAssociationDefinition childDef)
    {
        super(parent.getDictionaryService(), elementName, parent.getImporterProgress());
        
        // Ensure association is valid for node
        TypeDefinition anonymousType = getDictionaryService().getAnonymousType(parent.getTypeDefinition().getName(), parent.getNodeAspects());
        Map<QName, ChildAssociationDefinition> nodeAssociations = anonymousType.getChildAssociations();
        if (nodeAssociations.containsKey(childDef.getName()) == false)
        {
            throw new ImporterException("Association " + childDef.getName() + " is not valid for node " + parent.getTypeDefinition().getName());
        }
        
        parentRef = parent.getNodeRef();
        assocType = childDef.getName();
    }
    
    /**
     * @return  the parent ref
     */    
    /*package*/ NodeRef getParentRef()
    {
        return parentRef;
    }
    
    /**
     * @return  the child association type
     */
    /*package*/ QName getAssocType()
    {
        return assocType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ParentContext[parent=" + parentRef + ",assocType=" + getAssocType() + "]";
    }
    
}
