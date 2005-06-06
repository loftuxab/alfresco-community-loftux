package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;

/*package*/ class DelegateModelQuery implements ModelQuery
{

    private ModelQuery query;
    private ModelQuery delegate;
    
    
    /*package*/ DelegateModelQuery(ModelQuery query, ModelQuery delegate)
    {
        this.query = query;
        this.delegate = delegate;
    }

    public PropertyTypeDefinition getPropertyType(QName name)
    {
        PropertyTypeDefinition def = query.getPropertyType(name);
        if (def == null)
        {
            def = delegate.getPropertyType(name);
        }
        return def;
    }

    public TypeDefinition getType(QName name)
    {
        TypeDefinition def = query.getType(name);
        if (def == null)
        {
            def = delegate.getType(name);
        }
        return def;
    }

    public AspectDefinition getAspect(QName name)
    {
        AspectDefinition def = query.getAspect(name);
        if (def == null)
        {
            def = delegate.getAspect(name);
        }
        return def;
    }

    public ClassDefinition getClass(QName name)
    {
        ClassDefinition def = query.getClass(name);
        if (def == null)
        {
            def = delegate.getClass(name);
        }
        return def;
    }

    public PropertyDefinition getProperty(QName name)
    {
        PropertyDefinition def = query.getProperty(name);
        if (def == null)
        {
            def = delegate.getProperty(name);
        }
        return def;
    }

    public AssociationDefinition getAssociation(QName name)
    {
        AssociationDefinition def = query.getAssociation(name);
        if (def == null)
        {
            def = delegate.getAssociation(name);
        }
        return def;
    }
    
}
