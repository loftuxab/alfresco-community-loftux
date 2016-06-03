package org.alfresco.service.cmr.dictionary;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.QName;


/**
 * Read-only definition of an Association.
 *  
 * @author David Caruana
 *
 */
@AlfrescoPublicApi
public interface AssociationDefinition extends ClassAttributeDefinition
{
    
    /**
     * @return  defining model
     */
    public ModelDefinition getModel();
    
    /**
     * @return  the qualified name
     */
    public QName getName();

    /**
     * @deprecated The problem identified in MNT-413 will still exist
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#getTitle(org.alfresco.service.cmr.i18n.MessageLookup)
     */
    public String getTitle();

    /**
     * @deprecated The problem identified in MNT-413 will still exist
     * @see org.alfresco.service.cmr.dictionary.AssociationDefinition#getDescription(org.alfresco.service.cmr.i18n.MessageLookup)
     */
    public String getDescription();

    /**
     * @return the human-readable title 
     */
    public String getTitle(MessageLookup messageLookup);
    
    /**
     * @return the human-readable description 
     */
    public String getDescription(MessageLookup messageLookup);
    
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
     * Is the target class is mandatory, it is enforced?
     *  
     * @return true => enforced
     */
    public boolean isTargetMandatoryEnforced();

    /**
     * Can there be many target class instances in this association? 
     * 
     * @return true => cardinality > 1, false => cardinality of 0 or 1
     */
    public boolean isTargetMany();

}
