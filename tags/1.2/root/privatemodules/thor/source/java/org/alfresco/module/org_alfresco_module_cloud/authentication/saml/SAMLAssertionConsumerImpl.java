/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLUtil;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.LogoutResponse;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.StatusMessage;
import org.opensaml.saml2.core.StatusResponseType;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.extensions.surf.exception.AuthenticationException;
import org.springframework.extensions.webscripts.connector.User;

/**
 * SAML message consumer (AuthnResponse, LogoutRequest, LogoutResponse)
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 * 
 */
public class SAMLAssertionConsumerImpl implements SAMLAssertionConsumer
{
    private static final Log logger = LogFactory.getLog(SAMLAssertionConsumerImpl.class);

    private EmailAddressService emailAddressService;

    public void setEmailAddressService(EmailAddressService service)
    {
        this.emailAddressService = service;
    }

    @Override
    public User getUserFromAuthnResponse(Response samlResponse) throws AuthenticationException
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("SAML AuthnResponse processing started.");
        }
        
        try
        {
            SAMLUtil.validate(samlResponse);
        }
        catch (ValidationException e)
        {
            throw new SPAuthenticationException("Invalid SAML AuthnResponse Message.", e);
        }
        
        // Check for assertion and any other properties that we expect the IdP to supply as AuthResponse, as SAML
        // validator-suite does not check for assertions on successful authentication
        checkSamlResponseStatusCode(samlResponse);
        additionalChecksOnSamlResponse(samlResponse);
        
        Assertion assertion = samlResponse.getAssertions().get(0);
        List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
        Map<String, String> attributes = extractAttributeValue(attributeStatements);
        
        // Get the user's email from the response attribute statements
        String email = attributes.get(AlfrescoSAMLAttributes.Email.toString().toLowerCase());
        
        // Get session index - required for SingleLogoutRequest
        String idpSessionIndex = assertion.getAuthnStatements().get(0).getSessionIndex();

        /*
         * Based on the SAML spec, sessionIndex is only mandatory if the IdP supports SLO. Therefore, if the SSO
         * response doesn't have a sessionIndex, we set it to an empty string to limit the amount of changes in share.
         */
        if(idpSessionIndex == null)
        {
            idpSessionIndex = "";
        }
        
        if (email == null)
        {
            throw new SPAuthenticationException(
                "Invalid SAML AuthnResponse Message. 'Email' attibute does NOT exist in the AttributeStatements.");
        }
        
        //validate the email
        checkEmailValue(email, "Invalid SAML AuthnResponse Message. Email attibute value [" + email + "] is not a valid email address");
        email = email.toLowerCase();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SAML Response proceesing ended. User email: " + email);
        }
        
        // TODO we can set other properties of the User by iterating through attributes map.
        User user = new User(idpSessionIndex, null);
        user.setEmail(email);
        
        return user;
    }
    
    @Override
    public User getUserFromLogoutResponse(LogoutResponse samlResponse) throws AuthenticationException
    {
        if (logger.isTraceEnabled())
        {
            logger.debug("SAML Logout Response processing started.");
        }
        
        try
        {
            SAMLUtil.validate(samlResponse);
        }
        catch (ValidationException e)
        {
            throw new SPAuthenticationException("Invalid SAML Logout Response Message.", e);
        }
        
        checkSamlResponseStatusCode(samlResponse);
        
        String userId = AuthenticationUtil.getFullyAuthenticatedUser();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SAML Logout Response processing ended. User: " + userId);
        }
        
        return new User(userId, null);
    }
    
    @Override
    public User getUserFromLogoutRequest(LogoutRequest logoutRequest) throws AuthenticationException
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("SAML LogoutRequest proceesing started.");
        }
        
        try
        {
            SAMLUtil.validate(logoutRequest);
        }
        catch (ValidationException e)
        {
            throw new SPAuthenticationException("Invalid SAML Logout Request Message.", e);
        }
        //Check required logout request element
        NameID nameId= logoutRequest.getNameID();
        if(nameId == null)
        {
        throw new SPAuthenticationException("LogoutRequest did not contain NameID.");
        }        
    
        // Get the user's email from the logout request
        String email = nameId.getValue();        
        if (email == null)
        {
            throw new SPAuthenticationException(
                "Invalid SAML Logout Request Message. NameID value does NOT exist");
        }

        //validate the email
        checkEmailValue(email, "Invalid SAML Logout Request Message. NameID value [" + email + "] is not a valid email address");
        email = email.toLowerCase();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("SAML Logout Request (IdP-initiated) processing ended. User email: " + email);
        }
        
        return new User(email, null);
    }

    private Map<String, String> extractAttributeValue(List<AttributeStatement> attributeStatements)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for(AttributeStatement attributeStatement : attributeStatements)
        {
            for(Attribute attribute : attributeStatement.getAttributes())
            {
                // Name is required (form the SAML spec: <attribute name="Name" type="string" use="required"/>), 
                // no need to check for NPE.
                String attName = attribute.getName().toLowerCase();

                XMLObject attValue = attribute.getAttributeValues().get(0);
                if(attValue != null)
                {
                    if(attValue instanceof XSString)
                    {
                        map.put(attName, ((XSString)attValue).getValue());
                    }
                    // The default type of the attribute value is XSAny (if you don't specify the type).
                    // e.g. <saml:AttributeValue>john.doe@surfnet.test</saml:AttributeValue>
                    else if(attValue instanceof XSAny)
                    {
                        if(AlfrescoSAMLAttributes.Email.toString().toLowerCase().equals(attName))
                        {
                            map.put(attName, ((XSAny)attValue).getTextContent());
                        }
                    }
                }
            }
        }
        return map;
    }
    
    private void checkSamlResponseStatusCode(StatusResponseType samlResponse) throws SPAuthenticationException, IdPAuthenticationException
    {
        // Authn Response (SSO) or Logout Response (SLO)
        Status status = samlResponse.getStatus();
        StatusCode statusCode = null;
        if (status == null || (statusCode = status.getStatusCode()) == null
                    || !StatusCode.SUCCESS_URI.equals(StringUtils.trim(statusCode.getValue())))
        {
            String errorMessage = null;
            if (status == null)
            {
                errorMessage = "IdP has returned the response without a status!";
            }
            else if (statusCode == null)
            {
                errorMessage = "IdP has returned the response without a status code!";
            }
            else
            {
                errorMessage = "IdP has returned non-successful status code in the response: " + statusCode.getValue();
                StatusMessage statusMessage = status.getStatusMessage();
                if (statusMessage != null)
                {
                    errorMessage = errorMessage + " - " + statusMessage.getMessage();
                }
            }

            logger.error(errorMessage);

            throw new IdPAuthenticationException(errorMessage);
        }
    }
    
    private void additionalChecksOnSamlResponse(Response samlResponse) throws SPAuthenticationException
    {
        List<Assertion> assertions = null;
        Assertion assertion = null;
        if((assertions = samlResponse.getAssertions()).isEmpty())
        {
            throw new SPAuthenticationException("SAML Response did not contain an assertion.");
        }
        else if(samlResponse.getIssuer() == null)
        {
            throw new SPAuthenticationException("SAML Response did not contain any Issuer.");
        }
        else if((assertion = assertions.get(0)).getAuthnStatements().isEmpty())
        {
            throw new SPAuthenticationException("SAML Response did not contain an AuthnStatement.");
        }
        else if(assertion.getAttributeStatements().isEmpty())
        {
            throw new SPAuthenticationException("SAML Response did not contain an AttributeStatement.");
        }
    }
    
    private void checkEmailValue(String email, String errorMsg) throws SPAuthenticationException
    {
        // validate email address
        if(!emailAddressService.isAcceptedAddress(email))
        {
            throw new SPAuthenticationException(errorMsg);
        }
    }
}
