package org.alfresco.repo.dictionary;

import java.util.Map;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.i18n.MessageLookup;
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

    /*package*/ M2AspectDefinition(ModelDefinition model, M2Aspect m2Aspect, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties, Map<QName, AssociationDefinition> modelAssociations)
    {
        super(model, m2Aspect, resolver, modelProperties, modelAssociations);
    }

    @Override
    public String getDescription(MessageLookup messageLookup)
    {
        String value = M2Label.getLabel(model, messageLookup, "aspect", name, "description");
        
        // if we don't have a description call the super class
        if (value == null)
        {
           value = super.getDescription(messageLookup);
        }
        
        return value;
    }

    @Override
    public String getTitle(MessageLookup messageLookup)
    {
        String value = M2Label.getLabel(model, messageLookup, "aspect", name, "title");
        
        // if we don't have a title call the super class
        if (value == null)
        {
           value = super.getTitle(messageLookup);
        }
        
        return value;
   }
}
