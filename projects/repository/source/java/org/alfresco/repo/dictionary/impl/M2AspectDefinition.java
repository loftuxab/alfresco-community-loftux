package org.alfresco.repo.dictionary.impl;

import java.util.Map;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Aspect Definition.
 * 
 * @author David Caruana
 */
/*package*/ class M2AspectDefinition extends M2ClassDefinition
    implements AspectDefinition
{

    /*package*/ M2AspectDefinition(M2Aspect m2Aspect, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties, Map<QName, AssociationDefinition> modelAssociations)
    {
        super(m2Aspect, resolver, modelProperties, modelAssociations);
    }
    
}
