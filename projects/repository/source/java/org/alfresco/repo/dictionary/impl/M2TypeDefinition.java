package org.alfresco.repo.dictionary.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;


/**
 * Default Read-only Type Definition Implementation
 * 
 * @author David Caruana
 */
/*package*/ class M2TypeDefinition extends M2ClassDefinition
    implements TypeDefinition
{
    
    private List<QName> defaultAspectNames = new ArrayList<QName>();
    private List<AspectDefinition> defaultAspects = new ArrayList<AspectDefinition>();
    private List<AspectDefinition> inheritedDefaultAspects = new ArrayList<AspectDefinition>();


    /*package*/ M2TypeDefinition(M2Type m2Type, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties)
    {
        super(m2Type, resolver, modelProperties);

        // Resolve qualified names
        for (String aspectName : m2Type.getMandatoryAspects())
        {
            QName name = QName.createQName(aspectName, resolver);
            if (!defaultAspectNames.contains(name))
            {
                defaultAspectNames.add(name);
            }
        }
    }
    

    @Override
    /*package*/ void resolveDependencies(ModelQuery query)
    {
        super.resolveDependencies(query);
        
        for (QName aspectName : defaultAspectNames)
        {
            AspectDefinition aspect = query.getAspect(aspectName);
            if (aspect == null)
            {
                throw new DictionaryException("Mandatory aspect " + aspectName.toPrefixString() + " of class " + name.toPrefixString() + " is not found");
            }
            defaultAspects.add(aspect);
        }
    }


    @Override
    /*package*/ void resolveInheritance(ModelQuery query)
    {
        super.resolveInheritance(query);
        
        // Retrieve parent type
        TypeDefinition parentType = (parentName == null) ? null : query.getType(parentName);
        
        // Build list of inherited default aspects
        if (parentType != null)
        {
            inheritedDefaultAspects.addAll(parentType.getDefaultAspects());
        }
        
        // Append list of defined default aspects
        for (AspectDefinition def : defaultAspects)
        {
            if (!inheritedDefaultAspects.contains(def))
            {
                inheritedDefaultAspects.add(def);
            }
        }
    }
    

    /**
     * @see M2References#createAspectDefList(Collection<? extends M2Aspect>)
     */
    public List<AspectDefinition> getDefaultAspects()
    {
        return inheritedDefaultAspects;
    }
    
}
