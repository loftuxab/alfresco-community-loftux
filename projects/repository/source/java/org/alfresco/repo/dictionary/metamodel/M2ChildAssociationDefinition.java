package org.alfresco.repo.dictionary.metamodel;

import org.alfresco.repo.dictionary.ChildAssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;


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
     * @see org.alfresco.repo.dictionary.ChildAssociationDefinition#getDefaultType()
     */
    public ClassDefinition getDefaultType()
    {
        M2Type defaultType = ((M2ChildAssociation)getM2Association()).getDefaultType();
        return defaultType.getClassDefinition();
    }


}
