/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAML2PostBindingImplTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCredentialResolverDelegateTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLXMLSignatureSecurityPolicyRuleTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SAMLArtefactRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SAMLConfigAdminRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SLORestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SSORestApiTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is a holder for the various test classes associated with Cloud SAML.
 * It is not (at the time of writing) intended to be incorporated into the automatic build
 * which will find the various test classes and run them individually.
 * 
 * @author jkaabimofrad, janv
 * @since Cloud SAML
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    
    SAMLConfigAdminRestApiTest.class,
    SAMLArtefactRestApiTest.class,
    SSORestApiTest.class,
    SLORestApiTest.class,
    
    SAMLXMLSignatureSecurityPolicyRuleTest.class,
    SAML2PostBindingImplTest.class,
    SAMLCredentialResolverDelegateTest.class,
    SAMLConfigAdminServiceImplTest.class,
    SAMLAuthenticationServiceImplTest.class,
    

})
public class AllSamlServiceTests
{
    // Intentionally empty
}
