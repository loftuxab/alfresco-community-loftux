/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.jscript.app;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.jscript.app.CustomResponse;
import org.alfresco.util.UrlUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Return the base URL of the Alfresco Office Services
 *
 * @author: skopf
 */
public class AosCustomResponse implements CustomResponse
{
    private static Log logger = LogFactory.getLog(AosCustomResponse.class);

    private SysAdminParams sysAdminParams;

    private String baseUrlOverwrite;

    // used to build the default site name automatically based on the context
    // keep this in sync with org.alfresco.enterprise.repo.officeservices.service.Const.DEFAULT_SITE_PATH_IN_CONTEXT
    public static final String DEFAULT_SITE_PATH_IN_CONTEXT = "/aos";

    /**
     * Setter for sysAdminParams
     *
     * @param sysAdminParams
     */
    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    /**
     * Setter for aosBaseUrl
     *
     * @param aosBaseUrl
     */
    public void setBaseUrlOverwrite(String baseUrlOverwrite)
    {
        this.baseUrlOverwrite = baseUrlOverwrite;
    }

    /**
     * Populates the DocLib webscript response with custom metadata
     *
     * @return JSONObject or null
     */
    public Serializable populate()
    {
        try
        {

            Map<String, Serializable> jsonObj = new LinkedHashMap<String, Serializable>(4);
            if( (baseUrlOverwrite != null) && (baseUrlOverwrite.length() > 0) )
            {
                jsonObj.put("baseUrl", baseUrlOverwrite);
            }
            else
            {
                StringBuilder sb = new StringBuilder(UrlUtil.getAlfrescoUrl(sysAdminParams));
                sb.append(DEFAULT_SITE_PATH_IN_CONTEXT);
                jsonObj.put("baseUrl", sb.toString() );
            }
            return (Serializable)jsonObj;
        }
        catch (Exception e)
        {
            logger.warn("Could not add custom AOS response to DocLib webscript");
        }
        return null;
    }
}
