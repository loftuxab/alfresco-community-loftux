package org.alfresco.service.cmr.security;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Enumeration used to indicate access status.
 * 
 * @author Andy Hind
 */
@AlfrescoPublicApi
public enum AccessStatus
{
    DENIED, ALLOWED, UNDETERMINED;
}
