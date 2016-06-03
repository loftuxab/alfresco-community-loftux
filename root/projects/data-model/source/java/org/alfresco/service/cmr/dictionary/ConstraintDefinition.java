package org.alfresco.service.cmr.dictionary;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.QName;

/**
 * Property constraint definition
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public interface ConstraintDefinition
{
    /**
     * @return defining model 
     */
    public ModelDefinition getModel();
    
    /**
     * @return Returns the qualified name of the constraint
     */
    public QName getName();
    
    /**
     * @deprecated The problem identified in MNT-413 will still exist
     * @see org.alfresco.service.cmr.dictionary.ConstraintDefinition#getTitle(org.alfresco.service.cmr.i18n.MessageLookup)
     */
    public String getTitle();

    /**
     * @deprecated The problem identified in MNT-413 will still exist
     * @see org.alfresco.service.cmr.dictionary.ConstraintDefinition#getDescription(org.alfresco.service.cmr.i18n.MessageLookup)
     */
    public String getDescription();
    
    /**
     * @return the human-readable class title 
     */
    public String getTitle(MessageLookup messageLookup);
    
    /**
     * @return the human-readable class description 
     */
    public String getDescription(MessageLookup messageLookup);
    
    /**
     * @return Returns the constraint implementation
     */
    public Constraint getConstraint();
    
    /**
     * @return Returns the referenced constraint definition, if any (null for explicit or inline constraint def)
     * 
     * @since 3.2R
     */
    public QName getRef();
}
