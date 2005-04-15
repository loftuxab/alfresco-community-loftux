package com.activiti.repo.dictionary.metamodel;

import java.util.List;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.TypeDefinition;


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

    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.TypeDefinition#getDefaultAspects()
     */
    public List<ClassDefinition> getDefaultAspects()
    {
        List<M2Aspect> defaultAspects = ((M2Type)getM2Class()).getDefaultAspects();
        return M2References.createClassRefList(defaultAspects);
    }

    
    /* (non-Javadoc)
     * @see com.activiti.repo.dictionary.TypeDefinition#getOrderedChildren()
     */
    public boolean getOrderedChildren()
    {
        return ((M2Type)getM2Class()).getOrderedChildren();
    }
    
}
