package org.alfresco.module.org_alfresco_module_cloud_share;

public class SAMLAuthnResponseVerification
{
    private boolean authenticated;
    private String username;
    private String ticket;
    private String idpSessionIndex;
    private SAMLAuthnResponseVerificationRegistration registration;

    public SAMLAuthnResponseVerification(boolean authenticated, String username, String ticket, String idpSessionIndex, SAMLAuthnResponseVerificationRegistration registration)
    {
        this.authenticated = authenticated;
        this.username = username;
        this.ticket = ticket;
        this.idpSessionIndex = idpSessionIndex;
        this.registration = registration;
    }

    public boolean isAuthenticated()
    {
        return authenticated;
    }

    public String getUsername()
    {
        return username;
    }

    public String getTicket()
    {
        return ticket;
    }

    public String getIdpSessionIndex()
    {
        return idpSessionIndex;
    }

    public SAMLAuthnResponseVerificationRegistration getRegistration()
    {
        return registration;
    }
}
