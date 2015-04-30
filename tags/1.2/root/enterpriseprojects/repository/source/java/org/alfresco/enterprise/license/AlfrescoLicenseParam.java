/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.license;

import java.util.Map;
import java.util.TreeMap;

import javax.security.auth.x500.X500Principal;

import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultKeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.ftp.AbstractLicenseParam;
import de.schlichtherle.util.ObfuscatedString;

/**
 * Provides the parameters of the Alfresco License to the LicenseManager.
 * 
 * @author davidc
 */
public class AlfrescoLicenseParam extends AbstractLicenseParam
{

    /** The key with which the extra parameter recording the maximum schema version can be looked up. */
    public static final String EXTRA_PARAM_MAX_SCHEMA = "maxSchemaVersion";

    /** The key with which the extra parameter recording the maximum number of user accounts can be looked up. */
    public static final String EXTRA_PARAM_MAX_USERS = "maxUsers";
    
    /** The key with which the extra parameter recording the maximum number of documents looked up. */
    public static final String EXTRA_PARAM_MAX_DOCS = "maxDocs";
    
    public static final String EXTRA_PARAM_LICENSE_MODE = "licenseMode";
    
    public static final String KEY_NAME = "alfresco2";

    private static final Log logger = LogFactory.getLog(DescriptorService.class);

    private boolean createTrialLicense = false;

    private static final String KEYSTORE_PATH = "alfresco.keystore";

    private static final String PUBLIC_STORE_PWD = new ObfuscatedString(new long[]
    {
        0x7D47AC5E71B3B560L, 0xD6F1405DC20AE70AL
    }).toString();

    private static final String CIPHER_PWD = new ObfuscatedString(new long[]
    {
        0xC4E797867F14159BL, 0xBF6FEFC73045027BL
    }).toString();

    private static final String TRIAL_KEY_PWD = new ObfuscatedString(new long[]
    {
        0xD1FABA88D02424CCL, 0xC334DBB6DBC0CDECL, 0x9ED5F4EDD0BE4BCCL
    }).toString();
    
    private static int ftpMaxUsers = 10;
    private static int ftpMaxDocs = 100000;

    /**
     * Creates the Alfresco License Parameters. The subject incorporates the server edition and major version number.
     * 
     * @param serverDescriptor
     *            the server descriptor
     */
    public AlfrescoLicenseParam(Descriptor serverDescriptor)
    {
        super(serverDescriptor.getEdition() + " - v" + serverDescriptor.getVersionMajor() + "."
                + serverDescriptor.getVersionMinor(), 
                null, 
                new DefaultKeyStoreParam(AlfrescoLicenseParam.class,
                        AlfrescoLicenseParam.KEYSTORE_PATH, 
                        KEY_NAME, 
                        AlfrescoLicenseParam.PUBLIC_STORE_PWD, 
                        null),
                new DefaultKeyStoreParam(AlfrescoLicenseParam.class, 
                        AlfrescoLicenseParam.KEYSTORE_PATH,
                        "alfrescoTrial", 
                        AlfrescoLicenseParam.PUBLIC_STORE_PWD, 
                        AlfrescoLicenseParam.TRIAL_KEY_PWD),
                        30, 
                        new DefaultCipherParam(AlfrescoLicenseParam.CIPHER_PWD));
    }
    
    /**
     * Overrides the flag granting the creation of a trial license
     * 
     * @param createTrialLicense
     *            is the granting of trial licenses allowed?
     */
    public void setCreateTrialLicense(boolean createTrialLicense)
    {
        this.createTrialLicense = createTrialLicense;
    }

    /*
     * (non-Javadoc)
     * @see de.schlichtherle.license.ftp.LicenseParam#isFTPEligible()
     */
    public boolean isFTPEligible()
    {
        return this.createTrialLicense;
    }

    /*
     * (non-Javadoc)
     * @see de.schlichtherle.license.ftp.LicenseParam#createFTPLicenseContent()
     */
    @Override
    public LicenseContent createFTPLicenseContent()
    {
        if (AlfrescoLicenseParam.logger.isInfoEnabled())
        {
            AlfrescoLicenseParam.logger.info("Alfresco license: Creating time limited trial license");
        }

        final LicenseContent result = new LicenseContent();
        final X500Principal holder = new X500Principal("O=Trial User");
        result.setHolder(holder);
        final X500Principal issuer = new X500Principal(
                "CN=Unknown, OU=Unknown, O=Alfresco, L=Maidenhead, ST=Berkshire, C=UK");
        result.setIssuer(issuer);
        result.setConsumerType("System");
        result.setConsumerAmount(1);

        // Let's assume the 30 days is limiting enough and there's no need to lock a trial license to a schema version
        Map<String, Object> extra = new TreeMap<String, Object>();
        extra.put(EXTRA_PARAM_MAX_SCHEMA, Long.MAX_VALUE);
        //extra.put(EXTRA_PARAM_MAX_USERS, ftpMaxUsers);
        //extra.put(EXTRA_PARAM_MAX_DOCS, ftpMaxDocs);
        //extra.put(EXTRA_PARAM_LICENSE_MODE, "TEAM");
        extra.put(EXTRA_PARAM_LICENSE_MODE, "ENTERPRISE");
        result.setExtra(extra);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see de.schlichtherle.license.ftp.LicenseParam#removeFTPEligibility()
     */
    public void removeFTPEligibility()
    {
        this.createTrialLicense = false;
    }

    /*
     * (non-Javadoc)
     * @see de.schlichtherle.license.ftp.LicenseParam#ftpGranted(de.schlichtherle.license.LicenseContent)
     */
    public void ftpGranted(final LicenseContent content)
    {
    }
}
