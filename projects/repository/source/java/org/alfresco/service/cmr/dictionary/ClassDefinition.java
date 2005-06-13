package org.alfresco.service.cmr.dictionary;

import java.util.Map;

import org.alfresco.service.namespace.QName;

/**
 * Read-only definition of a Class.
 * 
 * @author David Caruana
 */
public interface ClassDefinition
{

    /**
     * @return the qualified name of the class
     */
    public QName getName();
    
    /**
     * @return the human-readable class title 
     */
    public String getTitle();
    
    /**
     * @return the human-readable class description 
     */
    public String getDescription();
    
    /**
     * @return  the super class (or null, if this is the root)
     */
    public QName getParentName();
    
    /**
     * @return true => aspect, false => type
     */
    public boolean isAspect();

    /**
     * @return the properties of the class, including inherited properties
     */
    public Map<QName, PropertyDefinition> getProperties();
    
    /**
     * Fetch all associations for which this is a source type, including child associations.
     * 
     * @return the associations including inherited ones
     * @see ChildAssociationDefinition
     */
    public Map<QName, AssociationDefinition> getAssociations();
    
    /**
     * Fetch only child associations for which this is a source type.
     *
     * @return all child associations applicable to this type, including those
     *         inherited from super types
     */
    public Map<QName, ChildAssociationDefinition> getChildAssociations();

    /**
     * Fetch all associations for which this is a target type, including child associations.
     * 
     * @return the associations including inherited ones
     */
    // TODO: public Map<QName, AssociationDefinition> getTargetAssociations();
    
}
