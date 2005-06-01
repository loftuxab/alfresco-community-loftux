package org.alfresco.repo.dictionary.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ChildAssociationDefinition;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;

/*package*/ class M2AnonymousTypeDefinition implements TypeDefinition
{
    
    private TypeDefinition type;
    private Map<QName,PropertyDefinition> properties = new HashMap<QName,PropertyDefinition>();
    private Map<QName,AssociationDefinition> associations = new HashMap<QName,AssociationDefinition>();
    private Map<QName,ChildAssociationDefinition> childassociations = new HashMap<QName,ChildAssociationDefinition>();
    
    
    /*package*/ M2AnonymousTypeDefinition(TypeDefinition type, Collection<AspectDefinition> aspects)
    {
        this.type = type;
        
        // Combine features of type and aspects
        properties.putAll(type.getProperties());
        associations.putAll(type.getAssociations());
        childassociations.putAll(type.getChildAssociations());
        for (AspectDefinition aspect : aspects)
        {
            properties.putAll(aspect.getProperties());
            associations.putAll(aspect.getAssociations());
            childassociations.putAll(aspect.getChildAssociations());
        }
    }
    

    public List<AspectDefinition> getDefaultAspects()
    {
        return type.getDefaultAspects();
    }

    public QName getName()
    {
        return QName.createQName(NamespaceService.ALFRESCO_DICTIONARY_URI, "anonymous/" + type.getName().getLocalName());
    }

    public String getTitle()
    {
        return type.getTitle();
    }

    public String getDescription()
    {
        return type.getDescription();
    }

    public QName getParentName()
    {
        return type.getParentName();
    }

    public boolean isAspect()
    {
        return type.isAspect();
    }

    public Map<QName, PropertyDefinition> getProperties()
    {
        return Collections.unmodifiableMap(properties);
    }

    public Map<QName, AssociationDefinition> getAssociations()
    {
        return Collections.unmodifiableMap(associations);
    }

    public Map<QName, ChildAssociationDefinition> getChildAssociations()
    {
        return Collections.unmodifiableMap(childassociations);
    }

}
