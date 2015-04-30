/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * SAML Config Admin DELETE
 * 
 * This class is the controller for the "saml-config-admin.delete" web scripts.
 * 
 * @author jkaabimofrad
 * 
 */
public class SAMLConfigAdminDelete extends AbstractSAMLConfigAdminWebScript
{

    private static final Log logger = LogFactory.getLog(SAMLConfigAdminDelete.class);

    @Override
    protected Map<String, Object> unprotectedExecuteImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // delete saml configs
        samlConfigAdminService.deleteSamlConfigs();

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("success", true);

        if(logger.isDebugEnabled())
        {
            logger.debug("SAMLConfigAdminDelete: " + model);
        }
        return model;
    }

}
