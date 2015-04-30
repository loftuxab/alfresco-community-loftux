package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.security.cert.X509Certificate;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.util.ParameterCheck;
import org.joda.time.DateTime;

/**
 * An Immutable class which holds information about Java X509 certificate's validity.
 * 
 * @author jkaabimofrad
 * 
 */
public final class SAMLCertificateInfo
{

    enum Status
    {
        VALID, EXPIRED
    }

    private final Status status;
    // DateTime is Immutable
    private final DateTime expiryDate;

    /**
     * Constructor
     * 
     * @param certificate
     *            a Java X.509 certificate
     */
    public SAMLCertificateInfo(X509Certificate certificate)
    {
        ParameterCheck.mandatory("certificate", certificate);
        this.expiryDate = new DateTime(certificate.getNotAfter());
        this.status = SAMLCertificateUtil.isCertificateExpired(certificate) ? Status.EXPIRED : Status.VALID;
    }

    /**
     * Gets certificate's expiration date.
     * 
     * @return <code>org.joda.time.DateTime</code> with the default ISOChronology (ISO8601 standard)
     */
    public DateTime getExpiryDate()
    {
        return expiryDate;
    }

    /**
     * Gets certificate's validity status.
     * 
     * @return a string representation of the certificat's status. e.g. valid
     */
    public String getStatus()
    {
        return status.toString().toLowerCase();
    }
}
