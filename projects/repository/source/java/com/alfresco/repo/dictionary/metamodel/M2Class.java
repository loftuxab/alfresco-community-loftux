package org.alfresco.repo.dictionary.metamodel;

import java.util.List;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.ref.QName;

/**
 * Class Definition
 * 
 * @author David Caruana
 */
public interface M2Class
{
    public QName getQName();

    public void setQName(QName qname);

    public M2Class getSuperClass();
    
    public void setSuperClass(M2Class superClass);
    
    public M2Property createProperty(String propertyName);
    
    /**
     * @return Returns a list of properties local to the class
     */
    public List<M2Property> getProperties();
    
    /**
     * @return Returns a list of properties including those inherited
     */
    public List<M2Property> getInheritedProperties();

    /**
     * @return Returns a list of associations local to the class
     */
    public List<M2Association> getAssociations();

    /**
     * @return Returns a list of associations both local to the class and those inheritied
     */
    public List<M2Association> getInheritedAssociations();
    
    public M2Association createAssociation(String associationName);
    
    public M2ChildAssociation createChildAssociation(String associationName);
    
    /**
     * @return Returns a read-only class definition
     */
    public ClassDefinition getClassDefinition();
}
