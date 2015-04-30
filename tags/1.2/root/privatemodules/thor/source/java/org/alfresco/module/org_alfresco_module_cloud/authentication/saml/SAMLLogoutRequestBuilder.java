/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.util.Set;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.SessionIndex;
import org.springframework.util.StringUtils;

/**
 * Domain-Specific Language (DSL) style builder class
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 * 
 */
public class SAMLLogoutRequestBuilder
{
    private LogoutRequest logoutRequest;
    
    protected SAMLLogoutRequestBuilder()
    {
        this.logoutRequest = SAMLUtil.buildXMLObject(LogoutRequest.DEFAULT_ELEMENT_NAME);
    }
    
    public static class Builder
    {
        private SAMLLogoutRequestBuilder logoutRequestBuilder;
        
        public Builder()
        {
            this.logoutRequestBuilder = new SAMLLogoutRequestBuilder();
        }
        
        public Builder withDestinationURL(String idpSLORequestServiceURL)
        {
            this.logoutRequestBuilder.getLogoutRequest().setDestination(idpSLORequestServiceURL);
            return this;
        }
        
        public Builder withIssuer(String issuingEntityName)
        {
            Issuer issuer = SAMLUtil.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
            issuer.setValue(issuingEntityName);
            // if setFormat is omitted then the value urn:oasis:names:tc:SAML:2.0:nameid-format:entity is in effect
            issuer.setFormat(NameIDType.ENTITY);
            this.logoutRequestBuilder.getLogoutRequest().setIssuer(issuer);
            return this;
        }
        
        public Builder withNameID(String userId)
        {
            NameID nameId = SAMLUtil.buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
            nameId.setValue(userId);
            
            this.logoutRequestBuilder.getLogoutRequest().setNameID(nameId);
            return this;
        }
        
        public LogoutRequest build(String idpSessionIndex)
        {
            LogoutRequest logoutRequest = logoutRequestBuilder.getLogoutRequest();
            logoutRequest.setID(SAMLUtil.generateUUID());
            logoutRequest.setIssueInstant(SAMLUtil.getJodaCurrentDateTime());
            
            // CLOUD-1191 - TODO is CSV ok (or is comma significant in sessionIndex) ... note: see also TenantSAMLIDPAuthnResponseController
            Set<String> sessionIndexSet = StringUtils.commaDelimitedListToSet(idpSessionIndex);
            for (String sessionIndex : sessionIndexSet)
            {
                SessionIndex sessionIndexElement = SAMLUtil.buildXMLObject(SessionIndex.DEFAULT_ELEMENT_NAME);
                sessionIndexElement.setSessionIndex(sessionIndex);
                logoutRequest.getSessionIndexes().add(sessionIndexElement);
            }
            
            return logoutRequest;
        }
    }
    
    LogoutRequest getLogoutRequest()
    {
        return logoutRequest;
    }
}
