package org.alfresco.repo.dictionary.metamodel;

import java.util.List;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;

/**
 * Default Read-only Type Definition Implementation
 * 
 * @author David Caruana
 */
public class M2TypeDefinition extends M2ClassDefinition
    implements TypeDefinition
{

    /*package*/ M2TypeDefinition(M2Type m2Type)
    {
        super(m2Type);
    }

    /**
     * @see M2References#createAspectDefList(Collection<? extends M2Aspect>)
     */
    public List<AspectDefinition> getDefaultAspects()
    {
        List<M2Aspect> defaultAspects = ((M2Type)getM2Class()).getDefaultAspects();
        return M2References.createAspectDefList(defaultAspects);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.TypeDefinition#getOrderedChildren()
     */
    public boolean getOrderedChildren()
    {
        return ((M2Type)getM2Class()).getOrderedChildren();
    }
}
