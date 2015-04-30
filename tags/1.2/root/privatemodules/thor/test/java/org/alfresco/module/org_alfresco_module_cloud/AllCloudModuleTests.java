/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud;

import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountAdminServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountRegistryTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.AccountUsageQuotaTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAOImplTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.domain.AccountDAOMemoryImplTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts.AccountRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts.EmailAddressRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts.SignupRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.analytics.AnalyticsTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLAuthenticationServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.SAMLConfigAdminServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAML2PostBindingImplTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCredentialResolverDelegateTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLXMLSignatureSecurityPolicyRuleTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SAMLArtefactRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SAMLConfigAdminRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SLORestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.webscripts.SSORestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.directory.DirectoryServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.EmailAddressServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.emailaddress.domain.InvalidDomainDAOImplTest;
import org.alfresco.module.org_alfresco_module_cloud.invitation.CloudInvitationServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.invitation.webscripts.CloudSiteInvitationRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.person.PeopleRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.person.PersonReplicationComponentTest;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.repo.dictionary.DictionaryDAOIntegrationTest;
import org.alfresco.module.org_alfresco_module_cloud.rest.api.tests.CloudApiTestSuite;
import org.alfresco.module.org_alfresco_module_cloud.rest.api.tests.CloudOpenCMISTCKTest;
import org.alfresco.module.org_alfresco_module_cloud.site.CloudSiteServiceImplTest;
import org.alfresco.module.org_alfresco_module_cloud.users.NetworkAdminRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.users.ResetPasswordRestApiTest;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.TenantBasicHTTPAuthenticatorTest;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.TransformContentTest;
import org.alfresco.module.org_alfresco_module_cloud.workflow.HyrbidWorkflowReviewTypeHandlerTest;
import org.alfresco.rest.framework.tests.core.InspectorTests;
import org.alfresco.rest.framework.tests.core.JsonJacksonTests;
import org.alfresco.rest.framework.tests.core.ParamsExtractorTests;
import org.alfresco.rest.framework.tests.core.ResourceLocatorTests;
import org.alfresco.rest.framework.tests.core.ResourceWebScriptHelperTests;
import org.alfresco.rest.framework.tests.core.SerializeTests;
import org.alfresco.rest.framework.tests.metadata.WriterTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is a holder for the various test classes associated with the Alfresco Cloud Module.
 * It is not (at the time of writing) intended to be incorporated into the automatic build
 * which will find the various test classes and run them individually.
 * 
 * @author Neil Mc Erlean
 * @since Thor
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    CollectionUtilsTest.class,
    
    EmailAddressServiceImplTest.class,
    InvalidDomainDAOImplTest.class,
    
    AccountRegistryTest.class,
    AccountDAOMemoryImplTest.class,
    AccountDAOImplTest.class,
    AccountServiceImplTest.class,
    AccountUsageQuotaTest.class,
    
    AccountAdminServiceImplTest.class,
    
    DirectoryServiceImplTest.class,
    PersonReplicationComponentTest.class,
    RegistrationServiceImplTest.class,
    CloudInvitationServiceImplTest.class,
    CloudSiteServiceImplTest.class,
    
    // Cloud SAML tests
    SAMLAuthenticationServiceImplTest.class,
    SAMLConfigAdminServiceImplTest.class,
    SAML2PostBindingImplTest.class,
    SAMLCredentialResolverDelegateTest.class,
    SAMLXMLSignatureSecurityPolicyRuleTest.class,
    SAMLArtefactRestApiTest.class,
    SAMLConfigAdminRestApiTest.class,
    SLORestApiTest.class,
    SSORestApiTest.class,
    
    TransformContentTest.class,
    
    org.alfresco.module.org_alfresco_module_cloud.networkadmin.scripts.NetworkAdminTest.class,
    org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdminTest.class,
    NetworkAdminRestApiTest.class,
    
    ResetPasswordRestApiTest.class,
    AccountRestApiTest.class,
    SignupRestApiTest.class,
    EmailAddressRestApiTest.class,
    CloudSiteInvitationRestApiTest.class,
    PeopleRestApiTest.class,
    
    AnalyticsTest.class,
    
    CloudApiTestSuite.class,
    CloudOpenCMISTCKTest.class,
    
    // REST Framework tests (see also AllRestFrameworkTest)
    InspectorTests.class,
    JsonJacksonTests.class,
    ParamsExtractorTests.class,
    ResourceLocatorTests.class,
    ResourceWebScriptHelperTests.class,
    SerializeTests.class,
    
    WriterTests.class,
    
    TenantBasicHTTPAuthenticatorTest.class,
    
    // Hybrid workflow tests
    HyrbidWorkflowReviewTypeHandlerTest.class,

    DictionaryDAOIntegrationTest.class
})
public class AllCloudModuleTests
{
    // Intentionally empty
}
