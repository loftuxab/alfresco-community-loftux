package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;

public interface PropertyDefinition
{

    public QName getName();

    public PropertyRef getReference();
    
    public QName getPropertyType();

    // TODO: Replace above with public PropertyTypeDefinition getPropertyType();
    
    public ClassRef getContainerClass();
    
    public boolean isMultiValued();

    public boolean isMandatory();
    
    public boolean isProtected();
    
    public boolean isIndexed();
    
    public boolean isStoredInIndex();
    
    public String getIndexTokeniserClassName();
    
}
