package org.alfresco.repo.dictionary.impl;

import java.util.Map;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;

/**
 * Default Read-Only Aspect Definition Implementation
 * 
 * @author David Caruana
 */
/*package*/ class M2AspectDefinition extends M2ClassDefinition
    implements AspectDefinition
{

    
    /*package*/ M2AspectDefinition(M2Aspect m2Aspect, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties)
    {
        super(m2Aspect, resolver, modelProperties);
    }

    
}
