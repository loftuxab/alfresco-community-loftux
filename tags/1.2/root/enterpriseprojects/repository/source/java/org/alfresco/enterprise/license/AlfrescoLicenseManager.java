/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.license;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.alfresco.repo.descriptor.DescriptorDAO;
import org.alfresco.repo.descriptor.LicenseResourceComponent;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseNotary;
import de.schlichtherle.license.ftp.LicenseManager;

/**
 * Alfresco implementation of License Manager. Note: Stores verified license files in Alfresco Repository.
 * 
 * @author davidc
 * @author dward
 */
public class AlfrescoLicenseManager extends LicenseManager
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(AlfrescoLicenseManager.class);
    
    private final DescriptorDAO currentRepoDescriptorDAO;
    private final Descriptor serverDescriptor;
    /** Is a trial license allowed? */
    private boolean trialEligibility;
    /** The application context. */
    private final ApplicationContext applicationContext;
    /** Where to find license files */
    private LicenseResourceComponent licenseResources;

    /**
     * The Constructor.
     * 
     * @param trialEligibility
     *            Is a trial license allowed?
     * @param context
     *            the application context
     */
    public AlfrescoLicenseManager(boolean trialEligibility, ApplicationContext context)
    {
        super();
        this.trialEligibility = trialEligibility;
        DescriptorDAO serverDescriptorDAO = (DescriptorDAO) context.getBean("serverDescriptorDAO");
        this.serverDescriptor = serverDescriptorDAO.getDescriptor();
        setLicenseParam(new AlfrescoLicenseParam(this.serverDescriptor));
        this.currentRepoDescriptorDAO = (DescriptorDAO) context.getBean("currentRepoDescriptorDAO");
        this.licenseResources = (LicenseResourceComponent) context.getBean("licenseResourceComponent");
        if(licenseResources == null)
        {
            logger.debug("license resources not found on context - create one on the fly");
            this.licenseResources = new LicenseResourceComponent();
        }
        this.applicationContext = context;
    }

    public void setLicenseResourceComponent(LicenseResourceComponent licensePaths)
    {
        this.licenseResources = licensePaths;
    }

    public LicenseResourceComponent getLicensePaths()
    {
        return licenseResources;
    }

    /**
     * Validates the license content.
     * 
     * @param content
     *            the license content
     *            
     * @throws LicenseContentException
     *             on validation error
     */
    @SuppressWarnings("unchecked")
    @Override
    protected synchronized void validate(final LicenseContent content) throws LicenseContentException
    {
        super.validate(content);
        final Map<String, Object> extra = (Map<String, Object>) content.getExtra();
        // From v3.0 onwards, we would expect the license to contain extra parameters
        if (extra == null)
        {
            throw new LicenseContentException("exc.invalidSubject");
        }
        // From v3.0 onwards, the maxSchemaVersion parameter must be there
        final Long maxSchemaVersion = (Long) extra.get(AlfrescoLicenseParam.EXTRA_PARAM_MAX_SCHEMA);
        if (maxSchemaVersion == null)
        {
            throw new LicenseContentException("exc.invalidSubject");
        }
        // Ensure we are not trying to run on a newer schema than we should
        if (this.serverDescriptor.getSchema() > maxSchemaVersion)
        {
            throw new LicenseContentException("exc.invalidSubject");
        }
    }
    
    /**
     * Attempts to load a new license from the input stream.  If the license is invalid it reloads the existing license.
     * 
     * @param licenseStream
     * @return
     * @throws Exception
     */
    public synchronized Pair<LicenseContent, Exception> verify(InputStream licenseStream) throws Exception
    {
        LicenseNotary notary = getLicenseNotary();
        Exception cause = null;
        LicenseContent content = null;
        
        // Do not yet allow auto-granting of a free trial license
        if (logger.isDebugEnabled())
        {
            logger.debug("Initial license vaidation WITHOUT FTP eligiblity set");
        }
        ((AlfrescoLicenseParam) getLicenseParam()).setCreateTrialLicense(false);

        if(licenseStream != null)
        {
            try
            {
                content = installLicenseFile(licenseStream);
                if (content != null)
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("New licence loaded from input stream");
                    }
                    return new Pair<LicenseContent, Exception>(content, cause);
                }
                else
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("No license in input stream");
                    }
                }
            }
            // Remember any validation failures from this license as we may yet report this as the cause
            catch (Exception e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Invalid license (" + e.getMessage() + ") in input stream");
                }
                // Don't log it because it'll be handled later and the logging can fail
                cause = e;
            }
        }
        // No new, valid license if we get here
        
        // Now attempt to validate the embedded license 
        content = verifyExisting(notary, cause);

        //Should only reach here if the license is valid.
        return new Pair<LicenseContent, Exception>(content, cause);
        
    }

    @Override
    protected synchronized LicenseContent verify(LicenseNotary notary) throws Exception
    {
        String lookingAtLicenseLocation = null;
        // Do not yet allow auto-granting of a free trial license
        if (logger.isDebugEnabled())
        {
            logger.debug("Initial license vaidation WITHOUT FTP eligiblity set");
        }
        ((AlfrescoLicenseParam) getLicenseParam()).setCreateTrialLicense(false);

        Exception cause = null;
        LicenseContent content = null;

        try
        {
            // First, check for new license to install in shared area. Allow renames.
            lookingAtLicenseLocation = licenseResources.getSharedLicenseLocation();
            content = installLicenseFile(lookingAtLicenseLocation, true);
            if (content != null)
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("New licence loaded from path: " + lookingAtLicenseLocation);
                }
                return content;
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No license on path: " + lookingAtLicenseLocation);
                }
            }

            // First, check for new license to install in shared area. Allow renames.
            lookingAtLicenseLocation = licenseResources.getExternalLicenseLocation();
            content = installLicenseFile(licenseResources.getExternalLicenseLocation(), true);
            if (content != null)
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("New licence loaded from path: " + lookingAtLicenseLocation);
                }
                return content;
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No license on path: " + lookingAtLicenseLocation);
                }
            }
        }
        // Remember any validation failures from this license as we may yet report this as the cause
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Invalid license (" + e.getMessage() + ") on path: " + lookingAtLicenseLocation);
            }
            // Don't log it because it'll be handled later and the logging can fail
            cause = e;
        }
        
        // No new, valid license if we get here
        
        // Now attempt to validate the embedded license 
        content = verifyExisting(notary, cause);

        //Should only reach here if the license is valid.
        return content;
    }
    
    /**
     * Verifies and reloads the existing license if the new one is invalid.
     * 
     * @param notary
     * @param cause The existing exception thrown by the invalid new license
     * @return
     * @throws Exception
     */
    private LicenseContent verifyExisting(LicenseNotary notary, Exception cause) throws Exception
    {
        LicenseContent content = null;

        
        try
        {
            return super.verify(notary);
        }
        catch (Exception exc)
        {
            String lookingAtLicenseLocation = null;
            try
            {
                // Installed license doesn't exist or is invalid. Try the embedded license.  No rename.
                lookingAtLicenseLocation = licenseResources.getEmbeddedLicenseLocation();
                content = installLicenseFile(lookingAtLicenseLocation, false);
                if (content != null)
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("New licence loaded from embedded path: " + lookingAtLicenseLocation);
                    }
                }
                else
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("No license on path: " + lookingAtLicenseLocation);
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Invalid embedded license (" + e.getMessage() + ") on path: " + lookingAtLicenseLocation);
            }

            // If all else fails, grant a trial license if possible or fall back to the original verification error
            if (content == null)
            {
                if (this.trialEligibility)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Repeat license vaidation with FTP eligiblity set");
                    }
                    // This time validate with trial eligibility
                    ((AlfrescoLicenseParam) getLicenseParam()).setCreateTrialLicense(true);
                    return super.verify(notary);
                }
                else
                {
                    throw cause == null ? exc : cause;
                }
            }
            return content;
        }
    }

    /**
     * Gets the license key from the repository.
     * 
     * @return the license key
     */
    @Override
    protected synchronized byte[] getLicenseKey()
    {
        return this.currentRepoDescriptorDAO.getLicenseKey();
    }

    /**
     * Persists the license key in the repository.
     * 
     * @param key
     *            the new license key
     */
    @Override
    protected synchronized void setLicenseKey(final byte[] key)
    {
        this.currentRepoDescriptorDAO.updateLicenseKey(key);
    }

    /**
     * Decode and install a license from a resource with the given pattern if possible.
     * 
     * @param pattern
     *            the pattern
     * @param rename
     *            should the pattern be resolved to a file and renamed on install?
     * @return the decoded license content, or <code>null</code> if it was not located
     * @throws Exception
     *             if a license is located but doesn't install
     */
    private LicenseContent installLicenseFile(String pattern, boolean rename) throws Exception
    {
        Resource[] resources = null;
        try
        {
            resources = this.applicationContext.getResources(pattern);
            if (resources.length > 1)
            {
                if (logger.isWarnEnabled())
                {
                    StringBuilder resMsg = new StringBuilder();
                    for (final Resource resource : resources)
                    {
                        resMsg.append('[').append(resource.getDescription()).append("] ");
                    }
                    logger.warn("Found more than one license file to install. The licenses found are: " + resMsg);
                }
                return null;
            }
            if (resources.length == 0)
            {
                return null;
            }
        }
        catch (FileNotFoundException e)
        {
            // A normal case, no need to log
            return null;
        }
        catch (Exception e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Error searching for license resources with pattern \"" + pattern + "\"", e);
            }
            return null;
        }

        /**
         *  Read the license bytes and install / decode them
         */
        final Resource licenseResource = resources[0];
        InputStream licenseStream = licenseResource.getInputStream();
        
        // The install method persists the current license
        LicenseContent content = installLicenseFile(licenseStream);
        
        if (logger.isInfoEnabled())
        {
            logger.info("Successfully installed license from " + resources[0].getDescription());
        }
        
        /**
         * Now rename the license that we have successfully installed
         */
        if (rename)
        {
            AlfrescoTransactionSupport.bindListener(new TransactionListenerAdapter()
            {
                public void afterCommit()
                {
                    try
                    {
                        final File licenseFile = licenseResource.getFile();

                        final File dest = new File(licenseFile.getAbsolutePath() + ".installed");
                        if (!licenseFile.renameTo(dest))
                        {
                            if (logger.isWarnEnabled())
                            {
                                logger.warn("Failed to rename installed license file " + licenseFile.getName() + " to " + dest.getName());
                            }
                        }
                    }
                    catch (final Exception e)
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("Alfresco license: Failed to rename installed license file " + licenseResource.getDescription(), e);
                        }
                    }
                }
            });
        }

        return content;
    }

    /**
     * Loads the license from the input stream and returns the license content.
     */
    private LicenseContent installLicenseFile(InputStream licenseStream) throws Exception
    {

        ByteArrayOutputStream out = new ByteArrayOutputStream(2024);
        byte[] buff = new byte[2024];
        int bytesRead;
        while ((bytesRead = licenseStream.read(buff)) != -1)
        {
            out.write(buff, 0, bytesRead);
        }
        licenseStream.close();
        out.close();
        
        // The install method persists the current license
        LicenseContent content = install(out.toByteArray(), getLicenseNotary());
        
        return content;
    }
}