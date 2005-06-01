package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.ChildAssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;


/**
 * Default Read-Only Association Definition Implementation
 * 
 * @author David Caruana
 */
/*package*/ class M2ChildAssociationDefinition extends M2AssociationDefinition
    implements ChildAssociationDefinition
{

    
    /**
     * Construct read-only Association Definition
     * 
     * @param m2Association  association definition
     * @return  the read-only definition
     */
    
    /*package*/ M2ChildAssociationDefinition(ClassDefinition classDef, M2ChildAssociation assoc, NamespacePrefixResolver resolver)
    {
        super(classDef, assoc, resolver);
    }

    
    public String getRequiredChildName()
    {
        return ((M2ChildAssociation)getM2Association()).getRequiredChildName();
    }

    public boolean getDuplicateChildNamesAllowed()
    {
        return ((M2ChildAssociation)getM2Association()).allowDuplicateChildName();
    }
        
}
