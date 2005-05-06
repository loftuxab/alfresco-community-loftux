package org.alfresco.repo.dictionary.metamodel;

import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.ref.QName;


/**
 * Property Type Definition
 * 
 * @author David Caruana
 */
public interface M2PropertyType
{

    public QName getQName();
    
    public void setQName(QName qname);    
    
    // TODO:  public List/*M2ValueConstraint*/ getValueConstraints();

    /**
     * Gets the read-only Property Type Definition
     * 
     * @return the read-only definition
     */
    public PropertyTypeDefinition getPropertyTypeDefinition();
    
    public String getAnalyserClassName();
    
    public void setAnalyserClassName(String analyserClassName);
    
}
