package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.ChildAssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;


/**
 * Compiled Association Definition.
 * 
 * @author David Caruana
 */
/*package*/ class M2ChildAssociationDefinition extends M2AssociationDefinition
    implements ChildAssociationDefinition
{

    /**
     * Construct
     * @param classDef  class definition
     * @param assoc  child assocation
     * @param resolver  namespace resolver
     */
    /*package*/ M2ChildAssociationDefinition(ClassDefinition classDef, M2ChildAssociation assoc, NamespacePrefixResolver resolver)
    {
        super(classDef, assoc, resolver);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ChildAssociationDefinition#getRequiredChildName()
     */
    public String getRequiredChildName()
    {
        return ((M2ChildAssociation)getM2Association()).getRequiredChildName();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ChildAssociationDefinition#getDuplicateChildNamesAllowed()
     */
    public boolean getDuplicateChildNamesAllowed()
    {
        return ((M2ChildAssociation)getM2Association()).allowDuplicateChildName();
    }
        
}
