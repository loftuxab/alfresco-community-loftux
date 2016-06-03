package org.alfresco.repo.dictionary;

import java.util.Map;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Type Definition
 * 
 * @author David Caruana
 */
/*package*/ class M2TypeDefinition extends M2ClassDefinition
    implements TypeDefinition
{
    /*package*/ M2TypeDefinition(ModelDefinition model, M2Type m2Type, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties, Map<QName, AssociationDefinition> modelAssociations)
    {
        super(model, m2Type, resolver, modelProperties, modelAssociations); 
    }
    
    @Override
    public String getDescription(MessageLookup messageLookup)
    {
        String value = M2Label.getLabel(model, messageLookup, "type", name, "description");
        
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
        String value = M2Label.getLabel(model, messageLookup, "type", name, "title");
        
        // if we don't have a title call the super class
        if (value == null)
        {
           value = super.getTitle(messageLookup);
        }
        
        return value;
   }
}
