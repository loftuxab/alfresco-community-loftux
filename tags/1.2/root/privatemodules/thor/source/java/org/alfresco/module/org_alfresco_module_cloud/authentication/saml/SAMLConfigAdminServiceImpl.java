/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.module.org_alfresco_module_cloud.authentication.saml;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.module.org_alfresco_module_cloud.authentication.saml.core.SAMLCertificateUtil;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.node.SystemNodeUtils;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.Configuration;
import org.opensaml.xml.security.credential.CollectionCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.impl.ExplicitKeySignatureTrustEngine;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * SAML Config Admin Service - note: primarily delegated to Network Admin(s)
 * 
 * @author janv, jkaabimofrad
 * @since Cloud SAML
 * 
 */
public class SAMLConfigAdminServiceImpl implements SAMLConfigAdminService
{
    private static Log logger = LogFactory.getLog(SAMLConfigAdminServiceImpl.class);

    private static final String ATTR_KEY_SAML_CONFIG_ROOT = ".tenantSAMLConfig";
    private static final String ATTR_KEY_SAML_ENABLED = "samlEnabled";
    private static final String ATTR_KEY_SAML_IDP_SSO_URL = "idpSsoURL";
    private static final String ATTR_KEY_SAML_IDP_SLO_REQUEST_URL = "idpSloRequestURL";
    private static final String ATTR_KEY_SAML_IDP_SLO_RESPONSE_URL = "idpSloResponseURL";
    private static final String ATTR_KEY_SAML_SP_ISSUER_NAME = "spIssuerName";

    // TODO see CLOUD-776 and CLOUD-779 (not used yet)
    private static final String ATTR_KEY_SAML_AUTO_PROVISION_ENABLED = "autoProvisionEnabled";
    private static final String ATTR_KEY_SAML_ALFRESCO_LOGIN_CREDENTIAL_ENABLED = "alfrescoLoginCredentialEnabled";

    private static final String FOLDER_NAME = "system.certificate_container.childname";

    private AttributeService attributeService;
    private ImporterBootstrap importerBootstrap;
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private Repository repositoryHelper;
    private ContentService contentService;
    // cache is tenant-aware (using EhCacheAdapter shared cache)
    private SimpleCache<String, SAMLTrustEngineInfo> samlTrustEngineCache;

    public void setAttributeService(AttributeService service)
    {
        this.attributeService = service;
    }

    public void setImporterBootstrap(ImporterBootstrap bootstrap)
    {
        this.importerBootstrap = bootstrap;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void setRepositoryHelper(Repository repositoryHelper)
    {
        this.repositoryHelper = repositoryHelper;
    }

    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    public void setSamlTrustEngineCache(SimpleCache<String, SAMLTrustEngineInfo> samlTrustEngineCache)
    {
        this.samlTrustEngineCache = samlTrustEngineCache;
    }

    /**
     * Checks that all necessary properties and services have been provided.
     */
    public void init()
    {
        PropertyCheck.mandatory(this, "attributeService", attributeService);
        PropertyCheck.mandatory(this, "importerBootstrap", importerBootstrap);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
        PropertyCheck.mandatory(this, "repositoryHelper", repositoryHelper);
        PropertyCheck.mandatory(this, "contentService", contentService);
        PropertyCheck.mandatory(this, "samlTrustEngineCache", samlTrustEngineCache);
    }

    private void recordAnalytics_SAML_OnOff(boolean isEnabled)
    {
        // note: will also record current "uid" and "aid" (based on fully authenticated user and their implied home network)
        Analytics.record_SAML_OnOff(isEnabled);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return isEnabled(TenantUtil.getCurrentDomain());
    }

    public boolean isEnabled(final String tenantDomain)
    {
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<Boolean>()
        {
            public Boolean doWork()
            {
                return getPrimitiveBoolean(attributeService.getAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_ENABLED));
            }
        }, tenantDomain);
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(final boolean enabled)
    {
        final String tenantDomain = TenantUtil.getCurrentDomain();
        
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Object>()
        {
            public Object doWork()
            {
                attributeService.setAttribute(enabled, ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain, ATTR_KEY_SAML_ENABLED);
                
                if(logger.isInfoEnabled())
                {
                    logger.info("Set SAML config: enabled=" + enabled + " [" + tenantDomain + "]");
                }
                
                return null;
            }
        }, tenantDomain);
        
        recordAnalytics_SAML_OnOff(enabled);
    }

    protected String getTenantDomain(String tenantDomain)
    {
        ParameterCheck.mandatory("tenantDomain", tenantDomain);
        return tenantDomain.toLowerCase(I18NUtil.getLocale());
    }

    /**
     * {@inheritDoc}
     */
    public SAMLConfigSettings getSamlConfigSettings(final String tenantDomain)
    {
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<SAMLConfigSettings>()
        {
            public SAMLConfigSettings doWork()
            {
                boolean samlEnabled = getPrimitiveBoolean(attributeService.getAttribute(ATTR_KEY_SAML_CONFIG_ROOT,
                    tenantDomain, ATTR_KEY_SAML_ENABLED));
                String idpSsoURL = (String)attributeService.getAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_IDP_SSO_URL);
                String idpSloRequestURL = (String)attributeService.getAttribute(ATTR_KEY_SAML_CONFIG_ROOT,
                    tenantDomain, ATTR_KEY_SAML_IDP_SLO_REQUEST_URL);
                String idpSloResponseURL = (String)attributeService.getAttribute(ATTR_KEY_SAML_CONFIG_ROOT,
                    tenantDomain, ATTR_KEY_SAML_IDP_SLO_RESPONSE_URL);
                boolean autoProvisionEnabled = getPrimitiveBoolean(attributeService.getAttribute(
                    ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain, ATTR_KEY_SAML_AUTO_PROVISION_ENABLED));
                boolean alfrescoLoginCredentialEnabled = getPrimitiveBoolean(attributeService.getAttribute(
                    ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain, ATTR_KEY_SAML_ALFRESCO_LOGIN_CREDENTIAL_ENABLED));
                String spIssuerName = (String)attributeService.getAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                            ATTR_KEY_SAML_SP_ISSUER_NAME);

                X509Certificate certificate = getCertificateImpl(tenantDomain);
                SAMLCertificateInfo certificateInfo = certificate != null ? new SAMLCertificateInfo(certificate) : null;

                return new SAMLConfigSettings.Builder(samlEnabled).idpSsoURL(idpSsoURL)
                    .idpSloRequestURL(idpSloRequestURL).idpSloResponseURL(idpSloResponseURL)
                    .autoProvisionEnabled(autoProvisionEnabled)
                    .alfrescoLoginCredentialEnabled(alfrescoLoginCredentialEnabled).issuer(spIssuerName)
                    .certificateInfo(certificateInfo).build();
            }
        }, tenantDomain);
    }

    /**
     * {@inheritDoc}
     */
    public void setSamlConfigs(final SAMLConfigSettings samlConfigSettings)
    {
        final String tenantDomain = TenantUtil.getCurrentDomain();
        
        final boolean isEnabled = samlConfigSettings.isSsoEnabled();
        
        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork()
            {
                byte[] cert = samlConfigSettings.getEncodedCertificate();
                // no need to check for null
                if(cert.length > 0)
                {
                    // Store the tenant's certificate
                    setCertificate(cert);
                }

                attributeService
                    .setAttribute(isEnabled, ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain, ATTR_KEY_SAML_ENABLED);
                setAttribute(samlConfigSettings.getIdpSsoURL(), ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_IDP_SSO_URL);
                setAttribute(samlConfigSettings.getIdpSloRequestURL(), ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_IDP_SLO_REQUEST_URL);
                setAttribute(samlConfigSettings.getIdpSloResponseURL(), ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_IDP_SLO_RESPONSE_URL);
                setAttribute(samlConfigSettings.isAutoProvisionEnabled(), ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_AUTO_PROVISION_ENABLED);
                setAttribute(samlConfigSettings.isAlfrescoLoginCredentialEnabled(), ATTR_KEY_SAML_CONFIG_ROOT,
                    tenantDomain, ATTR_KEY_SAML_ALFRESCO_LOGIN_CREDENTIAL_ENABLED);
                setAttribute(samlConfigSettings.getIssuer(), ATTR_KEY_SAML_CONFIG_ROOT,
                    tenantDomain, ATTR_KEY_SAML_SP_ISSUER_NAME);

                if(logger.isInfoEnabled())
                {
                    logger.info(samlConfigSettings.toString() + " [" + tenantDomain + "]");
                }
                return null;
            }
        }, tenantDomain);

        recordAnalytics_SAML_OnOff(isEnabled);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteSamlConfigs()
    {
        final String tenantDomain = TenantUtil.getCurrentDomain();

        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork()
            {
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain, ATTR_KEY_SAML_ENABLED);
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain, ATTR_KEY_SAML_IDP_SSO_URL);
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_IDP_SLO_REQUEST_URL);
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_IDP_SLO_RESPONSE_URL);
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_AUTO_PROVISION_ENABLED);
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_ALFRESCO_LOGIN_CREDENTIAL_ENABLED);
                attributeService.removeAttribute(ATTR_KEY_SAML_CONFIG_ROOT, tenantDomain,
                    ATTR_KEY_SAML_SP_ISSUER_NAME);

                NodeRef existingCert = findTenantCertificateNodeRef(tenantDomain);
                if(existingCert != null)
                {
                    nodeService.deleteNode(existingCert);
                    // remove from cache
                    samlTrustEngineCache.remove(tenantDomain);
                }

                if(logger.isInfoEnabled())
                {
                    logger.info("SAML config settings have been deleted" + " [" + tenantDomain + "]");
                }
                return null;
            }
        }, tenantDomain);

        // removal => disabled
        recordAnalytics_SAML_OnOff(false);
    }

    /**
     * {@inheritDoc}
     */
    public void setCertificate(final byte[] encodedCertificate)
    {
        final String tenantDomain = TenantUtil.getCurrentDomain();

        TenantUtil.runAsSystemTenant(new TenantRunAsWork<Void>()
        {
            public Void doWork()
            {
                X509Certificate certificate = SAMLCertificateUtil.generateCertificate(encodedCertificate);

                // Checks that the certificate is valid
                SAMLCertificateUtil.validateCertificate(certificate);

                // find if the tenant already has a certificate, if a certificate does exist, delete it.
                NodeRef existingCert = findTenantCertificateNodeRef(tenantDomain);
                if(existingCert != null)
                {
                    if(logger.isInfoEnabled())
                    {
                        logger.info("Deleted " + tenantDomain + "'s certificate, before storing new one.");
                    }
                    nodeService.deleteNode(existingCert);
                    // remove from cache
                    samlTrustEngineCache.remove(tenantDomain);
                }

                final NodeRef certNodeRef = createSAMLCertificateNode();
                ContentWriter contentWriter = contentService.getWriter(certNodeRef, ContentModel.PROP_CONTENT, true);
                contentWriter.setMimetype(MimetypeMap.MIMETYPE_BINARY);
                contentWriter.setEncoding("UTF-8");
                contentWriter.putContent(SAMLCertificateUtil.encodeCertificate(certificate));

                return null;
            }
        }, tenantDomain);

        if(logger.isInfoEnabled())
        {
            logger.info("Stored " + tenantDomain + "'s certificate.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public X509Certificate getCertificate()
    {
        return getCertificate(TenantUtil.getCurrentDomain());
    }

    /**
     * {@inheritDoc}
     */
    public X509Certificate getCertificate(final String tenantDomain)
    {
        X509Certificate certificate = getCertificateImpl(tenantDomain);

        if(certificate == null)
        {
            throw new AlfrescoRuntimeException("Certificate does not exist for the tenant: " + tenantDomain);
        }

        // Make sure the certificate is not expired
        if(SAMLCertificateUtil.isCertificateExpired(certificate))
        {
            throw new SAMLCertificateExpiredException("Certificate is Expired. Expiration date="
                + certificate.getNotAfter());
        }
        return certificate;
    }

    /**
     * {@inheritDoc}
     */
    public TrustEngine<Signature> getTrustEngine(final String tenantDomain)
    {
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<TrustEngine<Signature>>()
        {
            public TrustEngine<Signature> doWork()
            {
                TrustEngine<Signature> engine = null;
                SAMLTrustEngineInfo trustEngineInfo = samlTrustEngineCache.get(tenantDomain);

                if(trustEngineInfo != null)
                {
                    /*
                     * Check certificate expiration date, if it is expired, remove from cache, and get a new one from repo
                     * (provided that the network's Admin updated the expired certificate.)
                     */
                    if(SAMLCertificateUtil.isCertificateExpired(trustEngineInfo.getBasicX509Credential()
                        .getEntityCertificate()))
                    {
                        if(logger.isDebugEnabled())
                            logger.debug("The cached  [" + tenantDomain
                                + "] certificate is expired. Getting a new certificate from Repo.");

                        samlTrustEngineCache.remove(tenantDomain);
                        engine = createTrustEngine(tenantDomain);

                        if(logger.isDebugEnabled())
                            logger.debug("Retrieved [" + tenantDomain + "] new certificate from Repo.");
                    }
                    else
                    {
                        engine = trustEngineInfo.getTrustEngine();
                    }
                }
                else
                {
                    engine = createTrustEngine(tenantDomain);
                }

                return engine;
            }
        }, tenantDomain);
    }

    private X509Certificate getCertificateImpl(final String tenantDomain)
    {
        X509Certificate certificate = TenantUtil.runAsSystemTenant(new TenantRunAsWork<X509Certificate>()
        {
            public X509Certificate doWork()
            {
                NodeRef certNodeRef = findTenantCertificateNodeRef(tenantDomain);

                if(certNodeRef == null)
                {
                    return null;
                }

                ContentReader reader = contentService.getReader(certNodeRef, ContentModel.PROP_CONTENT);
                X509Certificate cert = SAMLCertificateUtil.generateCertificate(reader.getContentInputStream());

                return cert;
            }
        }, tenantDomain);

        return certificate;
    }

    private void setAttribute(Serializable serializableObj, String key1, String key2, String key3)
    {
        if(serializableObj != null)
        {
            attributeService.setAttribute(serializableObj, key1, key2, key3);
        }

    }

    private NodeRef getOrCreateSAMLCertificateContainer()
    {
        String name = importerBootstrap.getConfiguration().getProperty(FOLDER_NAME);
        QName container = QName.createQName(name, namespaceService);

        NodeRef systemCertificateContainer = SystemNodeUtils.getSystemChildContainer(container, nodeService,
            repositoryHelper);

        if(systemCertificateContainer == null)
        {
            if(logger.isInfoEnabled())
                logger.info("Lazy creating the Certificate System Container " + name);

            systemCertificateContainer = SystemNodeUtils.getOrCreateSystemChildContainer(container, nodeService,
                repositoryHelper).getFirst();
        }
        return systemCertificateContainer;
    }

    private NodeRef createSAMLCertificateNode()
    {
        final String tenantDomain = TenantUtil.getCurrentDomain();

        final NodeRef certContainer = getOrCreateSAMLCertificateContainer();

        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, tenantDomain);

        ChildAssociationRef newChildAssoc = nodeService.createNode(certContainer, ContentModel.ASSOC_CHILDREN,
            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, tenantDomain), ContentModel.TYPE_CONTENT,
            properties);

        return newChildAssoc.getChildRef();
    }

    private NodeRef findTenantCertificateNodeRef(final String tenantDomain)
    {
        return TenantUtil.runAsSystemTenant(new TenantRunAsWork<NodeRef>()
        {
            public NodeRef doWork()
            {
                final NodeRef certContainer = getOrCreateSAMLCertificateContainer();

                // Find the container, under system
                List<ChildAssociationRef> certRefs = nodeService.getChildAssocs(certContainer,
                    ContentModel.ASSOC_CHILDREN,
                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, tenantDomain));

                NodeRef tenantCertNodeRef = null;
                if(certRefs.size() > 0)
                {
                    tenantCertNodeRef = certRefs.get(0).getChildRef();
                }
                return tenantCertNodeRef;
            }
        }, tenantDomain);
    }

    private boolean getPrimitiveBoolean(Object obj)
    {
        if(obj instanceof Boolean)
        {
            return (Boolean)obj;
        }
        return false;
    }

    private TrustEngine<Signature> createTrustEngine(String tenantDomain)
    {
        BasicX509Credential basicX509Cred = new BasicX509Credential();
        basicX509Cred.setEntityCertificate(getCertificate(tenantDomain));
        basicX509Cred.setEntityId(tenantDomain);

        List<Credential> trustedCredentials = new ArrayList<Credential>(1);
        trustedCredentials.add(basicX509Cred);

        TrustEngine<Signature> trustEngine = new ExplicitKeySignatureTrustEngine(new CollectionCredentialResolver(
            trustedCredentials), Configuration.getGlobalSecurityConfiguration().getDefaultKeyInfoCredentialResolver());

        // add to cache
        samlTrustEngineCache.put(tenantDomain, new SAMLTrustEngineInfo(trustEngine, basicX509Cred));

        return trustEngine;
    }

    /**
     * A simple POJO to hold information about a tenant's Java X509 Certificate and <code>TrustEngine</code> for
     * validating IdP's signature. Note that this class is NOT immutable.
     * 
     * @author jkaabimofrad
     * @since Cloud SAML
     */
    private static class SAMLTrustEngineInfo
    {
        private final TrustEngine<Signature> trustEngine;
        private final BasicX509Credential basicX509Credential;

        SAMLTrustEngineInfo(TrustEngine<Signature> trustEngine, BasicX509Credential basicX509Credential)
        {
            this.trustEngine = trustEngine;
            this.basicX509Credential = basicX509Credential;
        }

        TrustEngine<Signature> getTrustEngine()
        {
            return trustEngine;
        }

        BasicX509Credential getBasicX509Credential()
        {
            return basicX509Credential;
        }
    }
}
