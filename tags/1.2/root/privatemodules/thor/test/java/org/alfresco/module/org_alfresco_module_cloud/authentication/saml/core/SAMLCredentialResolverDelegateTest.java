/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.alfresco.util.ApplicationContextHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.springframework.context.ApplicationContext;

/**
 * Test code for {@link SAMLCredentialResolverDelegate}.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLCredentialResolverDelegateTest
{
    private static ApplicationContext TEST_CONTEXT;
    private static SAMLCredentialResolverDelegate samlCredentialResolverDelegate;

    @BeforeClass
    public static void initStaticData() throws Exception
    {
        TEST_CONTEXT = ApplicationContextHelper.getApplicationContext();
        samlCredentialResolverDelegate = (SAMLCredentialResolverDelegate)TEST_CONTEXT.getBean("samlCredentialResolverDelegate");
        assertNotNull(samlCredentialResolverDelegate);
    }

    @Test
    public void testResolveSingle() throws Exception
    {
        Credential defaultCredential = samlCredentialResolverDelegate.resolveSingle();

        Set<String> aliases = samlCredentialResolverDelegate.getKeyAliases();
        assertEquals("SAML key store MUST only have one alias.", 1, aliases.size());

        String alias = aliases.iterator().next();

        assertNotNull(defaultCredential);
        assertEquals(alias, defaultCredential.getEntityId());
        assertNotNull(defaultCredential.getPrivateKey());
        //private keys
        assertEquals(samlCredentialResolverDelegate.getKey(alias), defaultCredential.getPrivateKey());

        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new EntityIDCriteria(alias));

        Credential credential = samlCredentialResolverDelegate.resolveSingle(criteriaSet);
        assertNotNull(credential);
        assertEquals(alias, credential.getEntityId());
        assertEquals(samlCredentialResolverDelegate.getKey(alias), credential.getPrivateKey());

        CriteriaSet criteriaSet2 = new CriteriaSet();
        criteriaSet2.add(new EntityIDCriteria("nonexistence.alias"));

        Credential nullCredential = samlCredentialResolverDelegate.resolveSingle(criteriaSet2);
        assertNull(nullCredential);
    }
}
