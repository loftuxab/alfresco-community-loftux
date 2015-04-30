package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.BasicSAMLMessageContext;

/**
 * 
 * @author jkaabimofrad
 * 
 */
public class AlfrescoSAMLMessageContext extends BasicSAMLMessageContext<SAMLObject, SAMLObject, SAMLObject>
{

    private String samlResponse;
    private String samlRequest;
    private String signature;
    private String spAcsURL;
    private String tenantDomain;

    /**
     * Default constructor
     */
    public AlfrescoSAMLMessageContext()
    {
    }

    public String getSamlResponse()
    {
        return samlResponse;
    }

    public void setSamlResponse(String samlResponse)
    {
        this.samlResponse = samlResponse;
    }

    public String getSamlRequest()
    {
        return samlRequest;
    }

    public void setSamlRequest(String samlRequest)
    {
        this.samlRequest = samlRequest;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getSpAcsURL()
    {
        return spAcsURL;
    }

    public void setSpAcsURL(String spAcsURL)
    {
        this.spAcsURL = spAcsURL;
    }

    public String getTenantDomain()
    {
        return tenantDomain;
    }

    public void setTenantDomain(String tenantDomain)
    {
        this.tenantDomain = tenantDomain;
    }
}
