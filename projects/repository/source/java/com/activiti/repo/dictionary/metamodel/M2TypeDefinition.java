package com.activiti.repo.dictionary.metamodel;

import java.util.List;

import com.activiti.repo.dictionary.TypeDefinition;

public class M2TypeDefinition extends M2ClassDefinition
    implements TypeDefinition
{

    public M2TypeDefinition(M2Type m2Type)
    {
        super(m2Type);
    }

    
    public List getDefaultAspects()
    {
        List defaultAspects = ((M2Type)getM2Class()).getDefaultAspects();
        return M2References.createClassRefList(defaultAspects);
    }

    public boolean getOrderedChildren()
    {
        return ((M2Type)getM2Class()).getOrderedChildren();
    }
    
    
}
