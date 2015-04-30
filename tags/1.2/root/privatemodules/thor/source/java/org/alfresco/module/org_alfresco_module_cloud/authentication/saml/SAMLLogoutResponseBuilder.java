/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLStatusCode;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;

/**
 * Domain-Specific Language (DSL) style builder class. Note that this class is NOT immutable, as the returned component
 * (<code>LogoutResponse</code>), is mutable.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 * 
 */
public final class SAMLLogoutResponseBuilder
{
    private final LogoutResponse logoutResponse;

    protected SAMLLogoutResponseBuilder()
    {
        this.logoutResponse = SAMLUtil.buildXMLObject(LogoutResponse.DEFAULT_ELEMENT_NAME);
    }

    public static class Builder
    {
        private final SAMLLogoutResponseBuilder logoutResponseBuilder;

        public Builder()
        {
            this.logoutResponseBuilder = new SAMLLogoutResponseBuilder();
        }

        public Builder withDestinationURL(String idpSloResponseServiceURL)
        {
            this.logoutResponseBuilder.getLogoutResponse().setDestination(idpSloResponseServiceURL);
            return this;
        }

        public Builder withInResponseTo(String idpSloRequestId)
        {
            this.logoutResponseBuilder.getLogoutResponse().setInResponseTo(idpSloRequestId);
            return this;
        }

        public Builder withStatusCode(SAMLStatusCode samlStatusCode)
        {
            Status status = SAMLUtil.buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
            StatusCode statusCode = SAMLUtil.buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
            statusCode.setValue(samlStatusCode.getStatusCodeURI());
            status.setStatusCode(statusCode);

            this.logoutResponseBuilder.getLogoutResponse().setStatus(status);
            return this;
        }

        public Builder withIssuer(String issuingEntityName)
        {
            Issuer issuer = SAMLUtil.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
            issuer.setValue(issuingEntityName);
            // if setFormat is omitted then the value urn:oasis:names:tc:SAML:2.0:nameid-format:entity is in effect
            this.logoutResponseBuilder.getLogoutResponse().setIssuer(issuer);
            return this;
        }

        public LogoutResponse build()
        {
            LogoutResponse logoutResponse = logoutResponseBuilder.getLogoutResponse();
            logoutResponse.setID(SAMLUtil.generateUUID());
            logoutResponse.setIssueInstant(SAMLUtil.getJodaCurrentDateTime());

            return logoutResponse;
        }
    }

    LogoutResponse getLogoutResponse()
    {
        return logoutResponse;
    }
}
