package org.alfresco.module.org_alfresco_module_cloud_share;

public class SAMLAuthnResponseVerificationRegistration
{
    private String id;
    private String key;
    private String type;

    SAMLAuthnResponseVerificationRegistration(String id, String key, String type)
    {
        this.id = id;
        this.key = key;
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    public String getKey()
    {
        return key;
    }

    public String getType()
    {
        return type;
    }
}
