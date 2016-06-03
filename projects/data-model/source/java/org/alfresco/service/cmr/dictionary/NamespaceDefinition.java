package org.alfresco.service.cmr.dictionary;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Read-only definition of a Namespace.
 *
 */
@AlfrescoPublicApi
public interface NamespaceDefinition
{
    /**
     * @return  defining model
     */
    public ModelDefinition getModel();
    
    /**
     * @return the namespace URI
     */
    public String getUri();
    
    /**
     * @return the namespace Prefix
     */
    public String getPrefix();
}
