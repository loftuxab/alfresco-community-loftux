package com.activiti.repo.dictionary.metamodel;

import java.util.List;

import com.activiti.repo.dictionary.AssociationDefinition;
import com.activiti.repo.dictionary.AssociationRef;


public interface M2Association
{

    public AssociationRef getReference();
    
    public M2Class getContainerClass();

    public String getName();
    
    public void setName(String name);
    
    public boolean isProtected();
    
    public void setProtected(boolean isProtected);
    
    public boolean isMandatory();
    
    public void setMandatory(boolean isMandatory);
    
    public boolean isMultiValued();
    
    public void setMultiValued(boolean isMultiValued);

    public List getRequiredToClasses();
    
    public AssociationDefinition getAssociationDefintion();
    
}
