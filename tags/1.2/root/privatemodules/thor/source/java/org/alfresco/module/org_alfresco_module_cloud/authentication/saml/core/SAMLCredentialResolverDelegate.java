/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.alfresco.encryption.AlfrescoKeyStoreImpl;
import org.alfresco.encryption.KeyStoreParameters;
import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * This class loads a Java key store and delegates it to {@link CredentialResolver} which extracts {@link Credential}'s
 * from it.
 * 
 * @author jkaabimofrad
 * @since Cloud SAML
 */
public class SAMLCredentialResolverDelegate extends AlfrescoKeyStoreImpl implements SAMLCredentialResolver,
    ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(SAMLCredentialResolverDelegate.class);

    private KeyStoreCredentialResolver keyStoreCredentialResolver;
    private ApplicationContext applicationContext;
    private CriteriaSet defaultCriteriaSet;

    public SAMLCredentialResolverDelegate()
    {
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent refreshedEvent)
    {
        ApplicationContext context = refreshedEvent.getApplicationContext();
        if(context != null && context.equals(applicationContext))
        {
            if(logger.isDebugEnabled())
                logger.debug("Bootstrapping component " + this.getClass().getName());
            try
            {
                loadKeyStoreCredentialResolver();
            }
            catch(Exception e)
            {
                throw new AlfrescoRuntimeException("Couldn't load KeyStoreCredentialResolver. ", e);
            }
        }
    }

    private void loadKeyStoreCredentialResolver() throws Exception
    {
        // loads the key store
        super.init();

        KeyStoreParameters keyStoreParameters = getKeyStoreParameters();
        KeyInfoManager keyInfoManager = getKeyInfoManager(keyStoreParameters.getKeyMetaDataFileLocation());

        KeyStore ks = loadKeyStore(keyStoreParameters, keyInfoManager);

        Map<String, String> privateKeyPasswordsMap = new HashMap<String, String>();
        Set<String> aliases = getKeyAliases();

        if(aliases.size() > 1)
        {
            throw new AlfrescoRuntimeException("SAML key store cannot have more than one alias.");
        }

        String alias = aliases.iterator().next();

        privateKeyPasswordsMap.put(alias, keyInfoManager.getKeyInformation(alias).getPassword());

        keyStoreCredentialResolver = new KeyStoreCredentialResolver(ks, privateKeyPasswordsMap);

        {
            defaultCriteriaSet = new CriteriaSet();
            defaultCriteriaSet.add(new EntityIDCriteria(alias));
            defaultCriteriaSet.add(new UsageCriteria(UsageType.SIGNING));
        }

        if(logger.isDebugEnabled())
            logger.debug("Key store is loaded successfully.");
    }

    @Override
    public Iterable<Credential> resolve(CriteriaSet criteriaSet) throws SecurityException
    {
        return keyStoreCredentialResolver.resolve(criteriaSet);
    }

    @Override
    public Credential resolveSingle(CriteriaSet criteriaSet) throws SecurityException
    {
        return keyStoreCredentialResolver.resolveSingle(criteriaSet);
    }

    @Override
    public Credential resolveSingle() throws SecurityException
    {
        return resolveSingle(this.defaultCriteriaSet);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
