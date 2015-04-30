/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Subject;

/**
 * Domain-Specific Language (DSL) style builder class. Note that this class is NOT immutable, as the returned component
 * (<code>AuthnRequest</code>), is mutable.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 * 
 */
public class SAMLAuthnRequestBuilder
{

    private final AuthnRequest authnRequest;

    protected SAMLAuthnRequestBuilder()
    {
        this.authnRequest = SAMLUtil.buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
    }

    public static class Builder
    {
        private final SAMLAuthnRequestBuilder authnRequestBuilder;

        public Builder()
        {
            this.authnRequestBuilder = new SAMLAuthnRequestBuilder();
        }

        public Builder withDestinationURL(String idpSSOServiceURL)
        {
            this.authnRequestBuilder.getAuthnRequest().setDestination(idpSSOServiceURL);
            return this;
        }

        public Builder withSpAcsURL(String spAssertionConsumerServiceURL)
        {
            this.authnRequestBuilder.getAuthnRequest().setAssertionConsumerServiceURL(spAssertionConsumerServiceURL);
            return this;
        }

        public Builder withIssuer(String issuingEntityName)
        {
            Issuer issuer = SAMLUtil.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
            issuer.setValue(issuingEntityName);
            // if setFormat is omitted then the value urn:oasis:names:tc:SAML:2.0:nameid-format:entity is in effect
            issuer.setFormat(NameIDType.ENTITY);
            this.authnRequestBuilder.getAuthnRequest().setIssuer(issuer);
            return this;
        }

        public Builder withDefaultNameIDPolicy()
        {
            NameIDPolicy nameIdPolicy = SAMLUtil.buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
            nameIdPolicy.setAllowCreate(true);

            this.authnRequestBuilder.getAuthnRequest().setNameIDPolicy(nameIdPolicy);
            return this;
        }

        public Builder withSubject(String subjectValue, String nameIDType)
        {
            NameID nameid = SAMLUtil.generateNameID(subjectValue, nameIDType);

            Subject subject = SAMLUtil.buildXMLObject(Subject.DEFAULT_ELEMENT_NAME);
            subject.setNameID(nameid);
            this.authnRequestBuilder.getAuthnRequest().setSubject(subject);
            return this;
        }

        public AuthnRequest build()
        {
            AuthnRequest authnRequest = authnRequestBuilder.getAuthnRequest();
            authnRequest.setID(SAMLUtil.generateUUID());
            authnRequest.setIssueInstant(SAMLUtil.getJodaCurrentDateTime());

            return authnRequest;
        }
    }

    AuthnRequest getAuthnRequest()
    {
        return authnRequest;
    }
}
