/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.security.cert.X509Certificate;
import org.alfresco.error.AlfrescoRuntimeException;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.signature.Signature;

/**
 * SAML Config Admin Service - note: in case of Cloud, delegated to Network Admin(s)
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 * 
 */
public interface SAMLConfigAdminService
{
    /**
     * Is SAML-enabled/disabled (for current Tenant)
     * 
     * @return true if SAML-enabled
     */
    public boolean isEnabled();

    /**
     * Is SAML-enabled/disabled (for specified Tenant)
     * 
     * @return true if SAML-enabled
     */
    public boolean isEnabled(String tenantDomain);

    /**
     * Set SAML-enabled/disabled (for current Tenant)
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled);

    /**
     * Get SAML configurations (for specified Tenant).
     * 
     * @param tenantDomain
     *            tenant domain
     * 
     * @return <code>SAMLConfigSettings</code> object
     */
    public SAMLConfigSettings getSamlConfigSettings(String tenantDomain);

    /**
     * Sets SAML configurations (for current Tenant).
     * 
     * @param samlConfigSettings
     * 
     * @throws CertificateConstructionException
     *             thrown if the certificate cannot be constructed
     * 
     * @throws SAMLCertificateException
     *             thrown if the certificate cannot be encoded
     * 
     * @throws SAMLCertificateExpiredException
     *             thrown if the certificate is expired
     * 
     * @throws SAMLCertificateNotYetValidException
     *             thrown if the certificate is not yet valid
     */
    public void setSamlConfigs(SAMLConfigSettings samlConfigSettings);

    /**
     * Deletes SAML configurations (for current Tenant).
     */
    public void deleteSamlConfigs();

    /**
     * Sets the current tenant's certificate, overwriting its prior value if it already exists.
     * 
     * @param encodedCertificate
     *            encoded certificate
     * 
     * @throws CertificateConstructionException
     *             thrown if the certificate cannot be constructed
     * 
     * @throws SAMLCertificateException
     *             thrown if the certificate cannot be encoded
     * 
     * @throws SAMLCertificateExpiredException
     *             thrown if the certificate is expired
     * 
     * @throws SAMLCertificateNotYetValidException
     *             thrown if the certificate is not yet valid
     */
    public void setCertificate(byte[] encodedCertificate);

    /**
     * Gets the Java X.509 certificate for the specified Tenant.
     * 
     * @param tenantDomain
     *            tenant domain
     * @return X.509 certificate object
     * 
     * @throws AlfrescoRuntimeException
     *             thrown if the certificate does not exist for the specified tenant
     * 
     * @throws SAMLCertificateExpiredException
     *             thrown if the certificate is expired
     */
    public X509Certificate getCertificate(String tenantDomain);

    /**
     * Gets the Java X.509 certificate (for current Tenant).
     * 
     * @return X.509 certificate object
     * 
     * @throws AlfrescoRuntimeException
     *             thrown if the certificate does not exist for the current tenant
     * 
     * @throws SAMLCertificateExpiredException
     *             thrown if the certificate is expired
     */
    public X509Certificate getCertificate();

    /**
     * Gets a trust engine for the specified tenant. This trust engine is used to validate the IdP's signature.
     * 
     * @param tenantDomain
     *            tenant domain
     * @return {@code TrustEngine<Signature> object}
     */
    public TrustEngine<Signature> getTrustEngine(String tenantDomain);

}
