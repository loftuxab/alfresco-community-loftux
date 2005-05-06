package org.alfresco.repo.dictionary.metamodel;

import java.util.List;

import org.alfresco.repo.dictionary.AssociationDefinition;

/**
 * Association Definition
 * 
 * @author David Caruana
 */
public interface M2Association
{
    public M2Class getContainerClass();

    public String getName();
    
    public void setName(String name);
    
    public boolean isProtected();
    
    public void setProtected(boolean isProtected);
    
    public boolean isMandatory();
    
    public void setMandatory(boolean isMandatory);
    
    public boolean isMultiValued();
    
    public void setMultiValued(boolean isMultiValued);

    public List<M2Class> getRequiredToClasses();
    
    /**
     * @return Returns a read-only association definition
     */
    public AssociationDefinition getAssociationDefintion();
}
