/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.util.UUID;
import javax.xml.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.ValidatorSuite;

/**
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 * 
 */
public class SAMLUtil
{

    @SuppressWarnings("unchecked")
    public static <T extends XMLObject> T buildXMLObject(QName qname)
    {
        return (T)((XMLObjectBuilder<XMLObject>)Configuration.getBuilderFactory().getBuilder(qname)).buildObject(qname);
    }

    public static Issuer generateIssuer(String issuerName)
    {
        Issuer issuer = buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue(issuerName);
        // if omitted then the value urn:oasis:names:tc:SAML:2.0:nameid-format:entity is in effect
        issuer.setFormat(NameIDType.ENTITY);

        return issuer;
    }

    public static NameID generateNameID(String newValue, String nameIDType)
    {
        NameID nameID = buildXMLObject(NameID.DEFAULT_ELEMENT_NAME);
        nameID.setValue(newValue);
        nameID.setFormat(nameIDType);

        return nameID;
    }

    /**
     * 
     * @param nameidFormat
     *            if null, the default format will be used
     * @return nameIdPolicy object
     */
    public static NameIDPolicy generateNameIDPolicy(String nameidFormat)
    {
        // Create NameIDPolicy
        NameIDPolicy nameIdPolicy = buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);

        if(!(StringUtils.isEmpty(nameidFormat)))
        {
            nameIdPolicy.setFormat(nameidFormat);
        }
        nameIdPolicy.setAllowCreate(true);

        return nameIdPolicy;
    }
    
    // Generate service endpoint (can be SSO or SLO depending on requested "service")
    public static Endpoint generateEndpoint(QName service, String idpServiceLocation, String spServiceLocation)
    {
        Endpoint samlEndpoint = buildXMLObject(service);
        samlEndpoint.setLocation(idpServiceLocation);
        // binding -> Required
        samlEndpoint.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        
        if (StringUtils.isNotEmpty(spServiceLocation))
        {
            samlEndpoint.setResponseLocation(spServiceLocation);
        }
        
        return samlEndpoint;
    }

    public static void validate(XMLObject xmlObject) throws ValidationException
    {
        ValidatorSuite schemaValidator = Configuration.getValidatorSuite("saml2-core-schema-validator");
        schemaValidator.validate(xmlObject);
        ValidatorSuite specValidator = Configuration.getValidatorSuite("saml2-core-spec-validator");
        specValidator.validate(xmlObject);
    }
    
    public static DateTime getJodaCurrentDateTime()
    {
        return new DateTime();
    }

    public static String generateUUID()
    {
        /*
         * String value xs:ID type, as defined in the XML standard and referenced in SAML, is not allowed to start with
         * a digit. There is a restriction which applies only to the first character of the string specifying that it
         * must be a letter or "_" only.
         */
        String id = UUID.randomUUID().toString();

        // check if the first character is a digit
        if(id.matches("^[\\d].*"))
        {

            return "_" + id.substring(1);
        }
        return id;
    }
}
