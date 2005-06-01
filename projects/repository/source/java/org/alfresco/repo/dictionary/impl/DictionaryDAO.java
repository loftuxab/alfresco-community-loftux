package org.alfresco.repo.dictionary.impl;

import java.util.Collection;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.ModelDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.ref.QName;

public interface DictionaryDAO extends ModelQuery
{
    
    public Collection<QName> getModels();
    
    public ModelDefinition getModel(QName name);
    
    public Collection<PropertyTypeDefinition> getPropertyTypes(QName model);
    
    public Collection<TypeDefinition> getTypes(QName model);

    public Collection<AspectDefinition> getAspects(QName model);

    public TypeDefinition getAnonymousType(QName type, Collection<QName> aspects);
    
    public void putModel(M2Model model);
    
}
