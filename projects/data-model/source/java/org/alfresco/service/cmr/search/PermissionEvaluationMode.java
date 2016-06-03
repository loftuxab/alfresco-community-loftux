package org.alfresco.service.cmr.search;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Enum to control how permissions are evaluated.
 * 
 * @author Andy Hind
 */
@AlfrescoPublicApi
public enum PermissionEvaluationMode
{
    EAGER, NONE; 
}