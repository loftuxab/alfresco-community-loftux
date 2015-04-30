/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLCertificateExpiredException;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLCertificateNotYetValidException;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.DateTime;
import org.springframework.util.FileCopyUtils;

/**
 * A utility class for working with Java X509 certificates.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLCertificateUtil
{

    private static final String BEGIN_BOUNDARY = "-----BEGIN CERTIFICATE-----";
    private static final String END_BOUNDARY = "-----END CERTIFICATE-----";
    private static final Base64 CHUNKED_ENCODER = new Base64(76);

    /**
     * Decodes a single Java certificate from base64 encoded form.
     * 
     * @param base64Cert
     *            base64-encoded certificate
     * 
     * @return a decoded {@code byte[]}
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code base64Cert} argument is invalid
     * 
     * @throws SAMLCertificateException
     *             thrown if PEM-formated string is used and the certificate cannot be decoded from PEM-format
     */
    public static byte[] decodeCertificate(String base64Cert)
    {
        ParameterCheck.mandatoryString("base64Cert", base64Cert);
        if(!Base64.isBase64(base64Cert))
        {
            throw new IllegalArgumentException(base64Cert + " is not base64 encoded.");
        }
        // check if the string is PEM-Formated
        if(base64Cert.startsWith(BEGIN_BOUNDARY))
        {
            try
            {
                return decodeCertificateFromPEM(new ByteArrayInputStream(base64Cert.getBytes()));
            }
            catch(IOException io)
            {
                throw new SAMLCertificateException("Could not decode the certificate in PEM-format.", io);
            }
        }

        return CHUNKED_ENCODER.decode(base64Cert);
    }

    /**
     * Generates a single X.509 certificate object from DER or PEM format form.
     * 
     * @param certificate
     *            DER or PEM format certificate
     * 
     * @return a native Java X509 certificate
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code derCertificate} argument is null
     * 
     * @throws CertificateConstructionException
     *             thrown if the certificate cannot be constructed
     */
    public static X509Certificate generateCertificate(byte[] certificate)
    {
        ParameterCheck.mandatory("cert", certificate);
        try
        {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());

            if(certFactory == null)
            {
                throw new CertificateConstructionException("Could not get an instance of certificate factory type.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(certificate);

            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(bais);
            if(cert == null)
            {
                throw new CertificateConstructionException("Could not construct a certificate from source.");
            }
            return cert;
        }
        catch(CertificateException ce)
        {
            throw new CertificateConstructionException("Could not construct a certificate.", ce);
        }

    }

    /**
     * Generates a single X.509 certificate object from an InputStream.
     * 
     * @param certStream
     *            the input stream containing the certificate
     * 
     * @return a native Java X509 certificate
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code certStream} argument is null
     * 
     * @throws CertificateConstructionException
     *             thrown if the certificate cannot be constructed
     * 
     * @throws SAMLCertificateException
     *             thrown if the certificate cannot be loaded
     */
    public static X509Certificate generateCertificate(InputStream certStream)
    {
        byte[] cert = loadCertificate(certStream);
        if(Base64.isBase64(cert) && !(new String(cert).startsWith(BEGIN_BOUNDARY)))
        {
            return generateCertificate(CHUNKED_ENCODER.decode(cert));
        }
        return generateCertificate(cert);
    }

    /**
     * Loads a single X.509 certificate from an InputStream.
     * 
     * @param certStream
     *            the input stream containing the certificate
     * 
     * @return a decoded {@code byte[]}
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code certStream} argument is null
     * 
     * @throws SAMLCertificateException
     *             thrown if the certificate cannot be loaded
     */
    public static byte[] loadCertificate(InputStream certStream)
    {
        ParameterCheck.mandatory("certStream", certStream);
        try
        {
            // Closes the stream when done.
            return FileCopyUtils.copyToByteArray(certStream);
        }
        catch(IOException ioe)
        {
            throw new SAMLCertificateException("Failed to load the certificate.", ioe);
        }
    }

    private static byte[] decodeCertificateFromPEM(InputStream cert) throws IOException
    {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(cert));

        try
        {
            while(bufferedReader.ready())
            {
                StringBuilder sb = new StringBuilder(850);

                String tempLine;
                while(((tempLine = bufferedReader.readLine()) != null && !tempLine.startsWith(BEGIN_BOUNDARY)))
                {
                    continue;
                }
                if(tempLine == null)
                {
                    throw new IOException("Malformed PEM data encountered. Missing " + BEGIN_BOUNDARY + " boundary");
                }
                while(((tempLine = bufferedReader.readLine()) != null) && !tempLine.startsWith(END_BOUNDARY))
                {
                    sb.append(tempLine);
                }
                if(tempLine == null)
                {
                    throw new IOException("Malformed PEM data encountered. Missing " + END_BOUNDARY + " boundary");
                }

                return CHUNKED_ENCODER.decode(sb.toString());
            }
        }
        finally
        {
            bufferedReader.close();
        }

        return new byte[0];
    }

    /**
     * Encodes a single Java X.509 certificate to base64 encoded form.
     * 
     * @param certificate
     *            a Java X.509 certificate
     * 
     * @return a base64 encoded string representation of the X.509 certificate
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code certificate} argument is null
     * 
     * @throws SAMLCertificateException
     *             thrown if the certificate cannot be encoded
     */
    public static String encodeCertificate(X509Certificate certificate)
    {
        ParameterCheck.mandatory("certificate", certificate);
        byte[] encodedCertificate = null;
        try
        {
            // encoded as ASN.1 DER format
            encodedCertificate = certificate.getEncoded();
        }
        catch(CertificateEncodingException e)
        {
            throw new SAMLCertificateException("Could not encode the certificate.");
        }

        return CHUNKED_ENCODER.encodeToString(encodedCertificate).trim();
    }

    /**
     * Checks that the certificate is currently valid.
     * 
     * @param certificate
     *            a Java X.509 certificate
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code certificate} argument is null
     * 
     * @throws SAMLCertificateExpiredException
     *             thrown if the certificate is Expired
     * 
     * @throws SAMLCertificateNotYetValidException
     *             thrown if the certificate is not yet valid
     */
    public static void validateCertificate(X509Certificate certificate)
    {
        ParameterCheck.mandatory("certificate", certificate);
        try
        {
            certificate.checkValidity();
        }
        catch(CertificateExpiredException cee)
        {
            throw new SAMLCertificateExpiredException("Certificate is Expired.", cee);
        }
        catch(CertificateNotYetValidException cne)
        {
            throw new SAMLCertificateNotYetValidException("Certificate is not yet valid.", cne);
        }
    }

    /**
     * Checks that the certificate is not expired.
     * 
     * @param certificate
     *            a Java X.509 certificate
     * @return true if the certificate is expired
     * 
     * @throws IllegalArgumentException
     *             thrown if the {@code certificate} argument is null
     */
    public static boolean isCertificateExpired(X509Certificate certificate)
    {
        ParameterCheck.mandatory("certificate", certificate);
        // DateTime default Chronology is ISOChronology (ISO8601 standard).
        // TODO this accurate to millisecond. Is it really necessary to be this accurate? or should it be checking only
        // for date?
        return new DateTime(certificate.getNotAfter()).isBeforeNow();
    }
}
