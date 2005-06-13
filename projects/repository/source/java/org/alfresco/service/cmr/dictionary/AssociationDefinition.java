package org.alfresco.service.cmr.dictionary;

import org.alfresco.service.namespace.QName;


/**
 * Read-only definition of an Association.
 *  
 * @author David Caruana
 *
 */
public interface AssociationDefinition
{
    /**
     * @return  the qualified name
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
     * Is this a child association?
     * 
     * @return true => child,  false => general relationship
     */
    public boolean isChild();
    
    /**
     * Is this association maintained by the Repository?
     * 
     * @return true => system maintained, false => client may maintain 
     */
    public boolean isProtected();

    /**
     * @return the source class
     */
    public ClassDefinition getSourceClass();

    /**
     * @return the role of the source class in this association? 
     */
    public QName getSourceRoleName();
    
    /**
     * Is the source class optional in this association?
     *  
     * @return true => cardinality > 0
     */
    public boolean isSourceMandatory();

    /**
     * Can there be many source class instances in this association? 
     * 
     * @return true => cardinality > 1, false => cardinality of 0 or 1
     */
    public boolean isSourceMany();

    /**
     * @return the target class  
     */
    public ClassDefinition getTargetClass();
    
    /**
     * @return the role of the target class in this association? 
     */
    public QName getTargetRoleName();
    
    /**
     * Is the target class optional in this association?
     *  
     * @return true => cardinality > 0
     */
    public boolean isTargetMandatory();

    /**
     * Can there be many target class instances in this association? 
     * 
     * @return true => cardinality > 1, false => cardinality of 0 or 1
     */
    public boolean isTargetMany();

}
