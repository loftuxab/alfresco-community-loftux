package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.AspectDefinition;

/**
 * Default Read-Only Aspect Definition Implementation
 * 
 * @author David Caruana
 */
public class M2AspectDefinition extends M2ClassDefinition
    implements AspectDefinition
{
    /*package*/ M2AspectDefinition(M2Aspect m2Aspect)
    {
        super(m2Aspect);
    }
}
