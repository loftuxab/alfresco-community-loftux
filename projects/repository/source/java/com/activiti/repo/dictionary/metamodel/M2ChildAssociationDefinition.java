package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.ChildAssociationDefinition;
import com.activiti.repo.dictionary.ClassRef;


/**
 * Default Read-Only Child Association Definition Implementation
 * 
 * @author David Caruana
 */
public class M2ChildAssociationDefinition extends M2AssociationDefinition
    implements ChildAssociationDefinition
{

    /*package*/ M2ChildAssociationDefinition(M2ChildAssociation m2Association)
    {
        super(m2Association);
        
        // Force load-on-demand of related entities
        ((M2ChildAssociation)getM2Association()).getDefaultType();
    }

    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.ChildAssociationDefinition#getDefaultType()
     */
    public ClassRef getDefaultType()
    {
        return ((M2ChildAssociation)getM2Association()).getDefaultType().getReference();
    }


}
