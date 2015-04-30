/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

/**
 * This immutable class holds the required attributes for setting SSO and SLO
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public final class SAMLConfigSettings
{

    private final boolean ssoEnabled;
    private final String idpSsoURL;
    private final String idpSloRequestURL;
    private final String idpSloResponseURL;
    private final byte[] encodedCertificate;
    // Immutable
    private final SAMLCertificateInfo certificateInfo;
    private final Boolean autoProvisionEnabled;
    private final Boolean alfrescoLoginCredentialEnabled;
    private final String issuer;

    public static class Builder
    {

        private final boolean ssoEnabled;
        private String idpSsoURL;
        private String idpSloRequestURL;
        private String idpSloResponseURL;
        private byte[] encodedCertificate = new byte[0];
        private SAMLCertificateInfo certificateInfo;
        private Boolean autoProvisionEnabled;
        private Boolean alfrescoLoginCredentialEnabled;
        private String issuer;

        public Builder(boolean ssoEnabled)
        {
            this.ssoEnabled = ssoEnabled;
        }

        public Builder idpSsoURL(String idpSsoURL)
        {
            this.idpSsoURL = idpSsoURL;
            return this;
        }

        public Builder idpSloRequestURL(String idpSloRequestURL)
        {
            this.idpSloRequestURL = idpSloRequestURL;
            return this;
        }

        public Builder idpSloResponseURL(String idpSloResponseURL)
        {
            this.idpSloResponseURL = idpSloResponseURL;
            return this;
        }

        public Builder encodedCertificate(byte[] encodedCertificate)
        {
            if(encodedCertificate != null)
            {
                this.encodedCertificate = encodedCertificate;
            }
            return this;
        }

        public Builder certificateInfo(SAMLCertificateInfo certificateInfo)
        {
            this.certificateInfo = certificateInfo;
            return this;
        }

        public Builder autoProvisionEnabled(Boolean autoProvisionEnabled)
        {
            this.autoProvisionEnabled = autoProvisionEnabled;
            return this;
        }

        public Builder alfrescoLoginCredentialEnabled(Boolean alfrescoLoginCredentialEnabled)
        {
            this.alfrescoLoginCredentialEnabled = alfrescoLoginCredentialEnabled;
            return this;
        }
        
        public Builder issuer(String issuer)
        {
            this.issuer = issuer;
            return this;
        }

        public SAMLConfigSettings build()
        {
            return new SAMLConfigSettings(this);
        }
    }

    /**
     * Initialises a newly created <code>SAMLConfigSettings</code> object
     * 
     * @param builder
     *            the builder object
     */
    private SAMLConfigSettings(Builder builder)
    {
        this.ssoEnabled = builder.ssoEnabled;
        this.idpSsoURL = builder.idpSsoURL;
        this.idpSloRequestURL = builder.idpSloRequestURL;
        this.idpSloResponseURL = builder.idpSloResponseURL;
        this.issuer = builder.issuer;
        // defensive copy
        this.encodedCertificate = builder.encodedCertificate.clone();
        this.certificateInfo = builder.certificateInfo;

        // TODO see CLOUD-776 and CLOUD-779 (not used yet)
        this.autoProvisionEnabled = builder.autoProvisionEnabled;
        this.alfrescoLoginCredentialEnabled = builder.alfrescoLoginCredentialEnabled;
    }

    /**
     * Is Single-Sign-On enabled/disabled
     * 
     * @return true if enabled
     */
    public boolean isSsoEnabled()
    {
        return ssoEnabled;
    }

    /**
     * Gets IdP's Single-Sign-On request service URL
     * 
     * @return a string representation of the Single-Sign-On service URL
     */
    public String getIdpSsoURL()
    {
        return idpSsoURL;
    }

    /**
     * Gets IdP's Single-Log-Out request service URL
     * 
     * @return a string representation of the Single-Log-Out request service URL
     */
    public String getIdpSloRequestURL()
    {
        return idpSloRequestURL;
    }

    /**
     * Gets IdP's Single-Log-Out response service URL
     * 
     * @return a string representation of the Single-Log-Out response service URL
     */
    public String getIdpSloResponseURL()
    {
        return idpSloResponseURL;
    }

    /**
     * Is auto provision enabled/disabled
     * 
     * @return true if enabled, false if disabled and <i>null</i> if it was not set
     */
    public Boolean isAutoProvisionEnabled()
    {
        return autoProvisionEnabled;
    }

    /**
     * Is alfresco login credential enabled/disabled
     * 
     * @return true if enabled, false if disabled and <i>null</i> if it was not set
     */
    public Boolean isAlfrescoLoginCredentialEnabled()
    {
        return alfrescoLoginCredentialEnabled;
    }

    /**
     * Gets IdP's certificate
     * 
     * @return the encoded certificate
     */
    public byte[] getEncodedCertificate()
    {
        return encodedCertificate.clone();
    }

    /**
     * Gets IdP's certificate validity information.
     * 
     * @return {@link SAMLCertificateInfo} object
     */
    public SAMLCertificateInfo getCertificateInfo()
    {
        return certificateInfo;
    }

    /**
     * Gets Issuer entity id (Service provider name)
     * 
     * @return Issuer id
     */
    public String getIssuer()
    {
        return issuer;
    }

    // private boolean getBoolean(Boolean bool)
    // {
    // return bool == null ? false : bool;
    // }
    

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(300);
        sb.append("SAML Settings:").append("[ssoEnabled=").append(ssoEnabled).append(", idpSsoURL=").append(idpSsoURL)
            .append(", idpSloRequestURL=").append(idpSloRequestURL).append(", idpSloResponseURL=")
            .append(idpSloResponseURL).append(", issuer=").append(issuer)
            // .append(", autoProvisionEnabled=").append(getBoolean(autoProvisionEnabled))
            // .append(", alfrescoLoginCredentialEnabled=").append(getBoolean(alfrescoLoginCredentialEnabled))
            .append("]");
        return sb.toString();
    }
}