/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import org.opensaml.saml2.core.StatusCode;

/**
 * SAML status code URIs.
 * <p>
 * Note: the JavaDoc description of the constants are copied from the saml-core-2.0 specification.
 * </p>
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public enum SAMLStatusCode
{
    /** The request succeeded */
    SUCCESS_URI(StatusCode.SUCCESS_URI),

    /** The request could not be performed due to an error on the part of the requester */
    REQUESTER_URI(StatusCode.REQUESTER_URI),

    /** The request could not be performed due to an error on the part of the SAML responder */
    RESPONDER_URI(StatusCode.RESPONDER_URI),

    /** The responding provider was unable to successfully authenticate the principal */
    AUTHN_FAILED_URI(StatusCode.AUTHN_FAILED_URI),

    /** The responding provider does not recognise the principal specified or implied by the request. */
    UNKNOWN_PRINCIPAL_URI(StatusCode.UNKNOWN_PRINCIPAL_URI);
    

    private String statusCodeURI;

    private SAMLStatusCode(String statusCodeURI)
    {
        this.statusCodeURI = statusCodeURI;
    }

    public String getStatusCodeURI()
    {
        return statusCodeURI;
    }

}
