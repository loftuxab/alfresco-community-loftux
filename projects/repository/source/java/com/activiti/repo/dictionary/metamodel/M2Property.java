package com.activiti.repo.dictionary.metamodel;

import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;


/**
 * Property Definition
 * 
 * @author David Caruana
 */
public interface M2Property
{
    public PropertyRef getReference();
    
    public M2Class getContainerClass();

    public String getName();
    
    public void setName(String name);
    
    public M2PropertyType getType();
    
    public void setType(M2PropertyType type);
    
    public boolean isProtected();
    
    public void setProtected(boolean isProtected);
    
    public boolean isMandatory();
    
    public void setMandatory(boolean isMandatory);
    
    public boolean isMultiValued();
    
    public void setMultiValued(boolean isMultiValued);
    
    public boolean isIndexed();
    
    public void setIndexed(boolean isIndexed);
    
    public boolean isStoredInIndex();
    
    public void setStoredInIndex(boolean isStoredInIndex);
    
    public String getIndexTokeniserClassName();
    
    public void setIndexTokeniserClassName(String indexTokeniserName);

    // TODO: public List getDefaultValues();
    
    // TODO:  public List/*M2ValueConstraint*/ getValueConstraints();
    
    // TODO: IndexTokenizer Class setter/getters??
    
    /**
     * Gets the Read-only Property Definition
     * 
     * @return  the read-only property definition
     */
    public PropertyDefinition getPropertyDefinition();
    
}
