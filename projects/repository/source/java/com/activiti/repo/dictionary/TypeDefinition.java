package com.activiti.repo.dictionary;

import java.util.List;

public interface TypeDefinition extends ClassDefinition
{
 
    public List/*ClassRef*/ getDefaultAspects();
    
    public boolean getOrderedChildren();

}
