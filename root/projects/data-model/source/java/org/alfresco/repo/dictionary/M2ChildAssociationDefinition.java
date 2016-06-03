package org.alfresco.repo.dictionary;

import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;


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

    
    public String getRequiredChildName()
    {
        return ((M2ChildAssociation)getM2Association()).getRequiredChildName();
    }

    
    public boolean getDuplicateChildNamesAllowed()
    {
        return ((M2ChildAssociation)getM2Association()).allowDuplicateChildName();
    }


    public boolean getPropagateTimestamps()
    {
        return ((M2ChildAssociation)getM2Association()).isPropagateTimestamps();
    }
}
