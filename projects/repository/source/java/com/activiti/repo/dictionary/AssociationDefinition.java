package com.activiti.repo.dictionary;

import java.util.List;

import com.activiti.repo.ref.QName;

public interface AssociationDefinition
{

    public QName getName();

    public AssociationRef getReference();
    
    public ClassRef getContainerClass();
    
    public boolean isChild();
    
    public boolean isMultiValued();

    public boolean isMandatory();
    
    public boolean isProtected();

    public List/*ClassRef*/ getRequiredToClasses();
    
}
