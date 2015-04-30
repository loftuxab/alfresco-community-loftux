/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.util.Map;

/**
 * SAML ResultMap with Request/Response ID (for logging)
 * 
 * @author janv
 * @since Cloud SAML
 */
public final class SAMLResultMap
{
    private String samlID;
    private Map<String,String> resultMap;
    
    public SAMLResultMap(String samlID, Map<String,String> resultMap)
    {
        this.samlID = samlID;
        this.resultMap = resultMap;
    }
    
    public String getSamlID()
    {
        return samlID;
    }
    
    public Map<String, String> getResultMap()
    {
        return resultMap;
    }
}