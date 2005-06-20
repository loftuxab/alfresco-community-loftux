package org.alfresco.repo.importer.view;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.repo.importer.ImporterException;
import org.alfresco.repo.importer.Progress;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
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
     * @param configuration
     * @param progress
     * @param elementName
     * @param parentRef
     * @param assocType
     */
    /*package*/ ParentContext(DictionaryService dictionary, Properties configuration, Progress progress, QName elementName, NodeRef parentRef, QName assocType)
    {
        super(dictionary, elementName, configuration, progress);
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
        super(parent.getDictionaryService(), elementName, parent.getConfiguration(), parent.getImporterProgress());
        
        // Ensure association is valid for node
        Set<QName> allAspects = new HashSet<QName>();
        for (AspectDefinition typeAspect : parent.getTypeDefinition().getDefaultAspects())
        {
            allAspects.add(typeAspect.getName());
        }
        allAspects.addAll(parent.getNodeAspects());
        TypeDefinition anonymousType = getDictionaryService().getAnonymousType(parent.getTypeDefinition().getName(), allAspects);
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
