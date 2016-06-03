
package org.alfresco.service.cmr.dictionary;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.QName;

/**
 *
 * @author Nick Smith
 */
@AlfrescoPublicApi
public interface ClassAttributeDefinition
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
     * @return the human-readable title 
     */
    public String getTitle(MessageLookup messageLookup);
    
    /**
     * @return the human-readable description 
     */
    public String getDescription(MessageLookup messageLookup);
    
    /**
     * Is this association or property maintained by the Repository?
     * 
     * @return true => system maintained, false => client may maintain 
     */
    public boolean isProtected();

}
