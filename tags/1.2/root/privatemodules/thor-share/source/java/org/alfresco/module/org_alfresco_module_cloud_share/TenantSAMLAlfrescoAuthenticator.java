package org.alfresco.module.org_alfresco_module_cloud_share;

public class TenantSAMLAlfrescoAuthenticator extends SAMLAlfrescoAuthenticator
{
    /**
     * @return the REST URL to be used for login requests
     */
    protected String getLoginURL()
    {
        return SAML_LOGIN + "/" + TenantUtil.getTenantName();
    }
}
