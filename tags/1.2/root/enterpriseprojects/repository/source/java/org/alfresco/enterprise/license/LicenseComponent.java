/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.license;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.SyncModel;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.patch.AppliedPatch;
import org.alfresco.repo.descriptor.DescriptorDAO;
import org.alfresco.repo.domain.patch.AppliedPatchDAO;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transaction.TransactionServiceImpl;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseException;
import org.alfresco.service.license.LicenseService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.springframework.context.ApplicationContext;
import org.alfresco.service.license.LicenseIntegrityException;
import org.springframework.extensions.surf.util.I18NUtil;

import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseNotaryException;
import de.schlichtherle.license.NoLicenseInstalledException;
import de.schlichtherle.xml.GenericCertificateIntegrityException;

/**
 * Alfresco Enterprise Network implementation of License Service.
 * 
 * @author davidc
 */
public class LicenseComponent implements LicenseService
{
    /** The application context from which other objects are resolved and to which notifications are broadcast. */
    private final ApplicationContext context;

    private LicenseDescriptor licenseDescriptor = null;
    private final QName vetoName = QName.createQName(NamespaceService.APP_MODEL_1_0_URI, "LicenseComponent");

    private final TransactionServiceImpl transactionService;
    private final AlfrescoLicenseManager licenseManager;
    private final JobLockService jobLockService;
    private final DescriptorDAO currentDescriptorDAO;
    private boolean failed = false;

    /** Did the license fail to verify with a possible temporary problem?. */
    private boolean failedTemp = false;

    private static final Log logger = LogFactory.getLog(DescriptorService.class);
    private static final Log loggerInternal = LogFactory.getLog(LicenseComponent.class);

    private enum OnFailure
    {
        INVALIDATE_LICENSE, KEEP_CURRENT_LICENCE
    }
    
    /**
     * Construct.
     * 
     * @param context
     *            application context
     */
    public LicenseComponent(final ApplicationContext context)
    {
        this.context = context;
        this.transactionService = (TransactionServiceImpl) context.getBean("transactionService");
        ImporterBootstrap systemBootstrap = (ImporterBootstrap) context.getBean("systemBootstrap");
        AppliedPatchDAO patchDao = (AppliedPatchDAO) context.getBean("appliedPatchDAO");
        this.currentDescriptorDAO = (DescriptorDAO) context.getBean("currentRepoDescriptorDAO");
        this.jobLockService = (JobLockService) context.getBean("jobLockService");
        
        RetryingTransactionCallback<Descriptor> getDescriptorCallback = new RetryingTransactionCallback<Descriptor>()
        {
            @Override
            public Descriptor execute() throws Throwable
            {
                return currentDescriptorDAO.getDescriptor();
            }
        };
        Descriptor descriptor = transactionService.getRetryingTransactionHelper().doInTransaction(getDescriptorCallback, true, false);
        
        final boolean trialEligibility = getTrialEligibility(systemBootstrap, patchDao, descriptor);
        this.licenseManager = new AlfrescoLicenseManager(trialEligibility, context);
    }

    /**
     * Verify license. Called on bootstrap.
     * 
     * @throws LicenceException
     */
    public synchronized void verifyLicense()
    {
        final RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
        txnHelper.setForceWritable(true);

        final QName lockQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "verifyLicense");
        final RetryingTransactionCallback<String> lockCallback = new RetryingTransactionCallback<String>()
        {
            public String execute() throws Exception
            {
                QName lockQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "verifyLicense");
                final String lockToken = jobLockService.getLock(lockQName, 5000, 1000, 100);
                return lockToken;
            }
        };

        final String lockToken = txnHelper.doInTransaction(lockCallback);

        final RetryingTransactionCallback<Void> unlockCallback = new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Exception
            {
                jobLockService.releaseLock(lockToken, lockQName);
                return null;
            }
        };

        // Job Locking not comprehensive or fool-proof but it may help ease cluster start-up.
        try
        {
            // verify existing license
            if (licenseDescriptor == null)
            {
                verify();

                // construct scheduler job for period license verify
                try
                {
                    final Scheduler scheduler = (Scheduler) context.getBean("schedulerFactory");
                    final JobDetail jobDetail = new JobDetail("vlj", Scheduler.DEFAULT_GROUP, VerifyLicenseJob.class);
                    jobDetail.getJobDataMap().put("licenseComponent", this);
                    final Trigger trigger = TriggerUtils.makeHourlyTrigger();
                    // Start one minute from now - I think we could change this to one hour
                    // now that I have fixed the dependence that HeartBeat had on there being
                    // a second verification. However it make testing license verification simpler.
                    trigger.setStartTime(new Date(System.currentTimeMillis() + 60L * 1000L));
                    trigger.setName("vlt");
                    trigger.setGroup(Scheduler.DEFAULT_GROUP);

                    // Unschedule in case it was scheduled in an earlier retry of the transaction
                    scheduler.unscheduleJob("vlt", Scheduler.DEFAULT_GROUP);
                    scheduler.scheduleJob(jobDetail, trigger);
                }
                catch (final SchedulerException e)
                {
                    throw new LicenseException("Failed to initialise License Component", e);
                }
            }
            else
            {
                verify();
            }
        }
        finally
        {
            txnHelper.doInTransaction(unlockCallback);
        }
    }

    @Override
    public boolean isLicenseValid()
    {
        return licenseDescriptor != null;
    }

    /**
     * Verify License.
     * <p/>
     * Note: if a license hasn't been already been installed then install a free trial period
     * <p/>
     * Side effect: licenseSucceeded or licenseFailed callbacks triggered by this method.  The repository may be locked read only. 
     * 
     * @throws LicenseException if the license is invalid
     */
    private synchronized void verify() throws LicenseException
    {
        final RetryingTransactionCallback<LicenseDescriptor> verifyLicenseCallback = new RetryingTransactionCallback<LicenseDescriptor>()
        {
            public LicenseDescriptor execute() throws Exception
            {
                try
                {
                    loggerInternal.debug("Verifying license");

                    final LicenseContent licenseContent = licenseManager.verify();
                    LicenseDescriptor descriptor = new LicenseContentDescriptor(licenseContent);

                    return descriptor;
                }
                catch (final GenericCertificateIntegrityException  e)
                {
                    throw new LicenseIntegrityException(e.getMessage(), e);
                }
            }
        };

        try
        {
            LicenseDescriptor descriptor = AuthenticationUtil.runAs(new RunAsWork<LicenseDescriptor>()
            {
                public LicenseDescriptor doWork() throws Exception
                {
                    // Call the Licence Manager to verify the license.
                    RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
                    txnHelper.setForceWritable(true);
                    return txnHelper.doInTransaction(verifyLicenseCallback);
                }
            }, AuthenticationUtil.getSystemUserName());

            // Call license succeeded after the verify transaction
            licenseSucceeded(descriptor);
        }
        catch (AlfrescoRuntimeException are)
        {
            Throwable t = are.getCause();

            if (t != null)
            {
                if (t instanceof NoLicenseInstalledException)
                {
                    licenseFailed((NoLicenseInstalledException) t, OnFailure.KEEP_CURRENT_LICENCE);
                }
                else if (t instanceof LicenseNotaryException)
                {
                    licenseFailed((LicenseNotaryException) t, OnFailure.INVALIDATE_LICENSE);
                }
                else if (t instanceof LicenseContentException)
                {
                    licenseFailed((LicenseContentException) t, OnFailure.INVALIDATE_LICENSE);
                }
                else if (t instanceof LicenseIntegrityException)
                {
                    licenseFailed((LicenseIntegrityException) t, OnFailure.INVALIDATE_LICENSE);
                }
                else if (t instanceof LicenseException)
                {
                    licenseFailed((LicenseException) t, OnFailure.KEEP_CURRENT_LICENCE);
                }
                else
                {
                    /*
                     * We should not be here, unless there is a wrapped Error or RuntimeException.
                     * 
                     * We can't say that the license is invalid and make the decision to become read only.
                     * The exception could be something transient, such as unable to communicate with the repo.
                     */
                    logger.warn("Unexpected exception caught", are);
                    licenseFailed(are, OnFailure.KEEP_CURRENT_LICENCE);
                }
            }
            throw new LicenseException("Unable to verify license", are);
        }
    }

    /**
     * Verify License.
     * <p/>
     * note: if a license hasn't been already been installed then install a free trial period
     * @throws LicenseException         if the license fails to be verified
     */
    private boolean verify(final InputStream licenseStream)
    {
        final RetryingTransactionCallback<Pair<LicenseDescriptor, Exception>> verifyLicenseCallback = new RetryingTransactionCallback<Pair<LicenseDescriptor, Exception>>()
        {
            public Pair<LicenseDescriptor, Exception> execute() throws Exception
            {
                LicenseDescriptor descriptor = null;
                Pair<LicenseDescriptor, Exception> returnPair = null;
                try
                {
                    loggerInternal.debug("Verifying license");

                    Pair<LicenseContent, Exception> pair = licenseManager.verify(licenseStream);
                    final LicenseContent licenseContent = pair.getFirst();
                    descriptor = new LicenseContentDescriptor(licenseContent);

                    returnPair = new Pair<LicenseDescriptor, Exception>(descriptor, pair.getSecond());
                    licenseSucceeded(descriptor);
                }
                catch (final GenericCertificateIntegrityException  e)
                {
                    throw new LicenseIntegrityException(e.getMessage(), e);
                }				
                catch (final NoLicenseInstalledException e)
                {
                    licenseFailed(e, OnFailure.KEEP_CURRENT_LICENCE);
                }
                catch (final LicenseNotaryException e)
                {
                    licenseFailed(e, OnFailure.INVALIDATE_LICENSE);
                }
                catch (final LicenseContentException e)
                {
                    licenseFailed(e, OnFailure.INVALIDATE_LICENSE);
                }
                catch (final LicenseException e)
                {
                    licenseFailed(e, OnFailure.KEEP_CURRENT_LICENCE);
                }
                return returnPair;
            }
        };

        try
        {
            Pair<LicenseDescriptor, Exception> pair = AuthenticationUtil.runAs(new RunAsWork<Pair<LicenseDescriptor, Exception>>()
            {
                public Pair<LicenseDescriptor, Exception> doWork() throws Exception
                {
                    // Call the Licence Manager to verify the license.
                    RetryingTransactionHelper txnHelper = transactionService.getRetryingTransactionHelper();
                    txnHelper.setForceWritable(true);
                    return txnHelper.doInTransaction(verifyLicenseCallback);
                }
            }, AuthenticationUtil.getSystemUserName());
        
            return (pair.getSecond() == null);
        }
        catch (Exception are)
        {
            Throwable t = are.getCause();
            if (t != null && t instanceof LicenseIntegrityException)
            {
                licenseFailed((LicenseIntegrityException) t, OnFailure.INVALIDATE_LICENSE);
            }
            else
            {
                /*
                 * We should not be here, unless there is a wrapped Error or RuntimeException.
                 * 
                 * We can't say that the license is invalid and make the decision to become read only.
                 * The exception could be something transient, such as unable to communicate with the repo.
                 */
                logger.warn("Unexpected exception caught", are);
                licenseFailed(are, OnFailure.KEEP_CURRENT_LICENCE);
            }

            return false;
        }
    }

    private synchronized boolean isInitialVerify()
    {
        return licenseDescriptor == null && !failed;
    }
    
    /**
     * Resets state when the license has been verified successfully
     */
    private synchronized void licenseSucceeded(LicenseDescriptor descriptor)
    {
        boolean setAllowWrite = true;
        boolean initialVerify = isInitialVerify();
        licenseDescriptor = descriptor;

        if (initialVerify)
        {
            LicenseComponent.logger.debug(
                "Alfresco license: Initial read of Alfresco License");
        }
        else if (failed)
        {
            LicenseComponent.logger.error(
                "Alfresco license: Alfresco Repository NO LONGER restricted to read-only capability");
            failed = false;
        }
        else if (failedTemp)
        {
            LicenseComponent.logger.warn(
                "Alfresco license: Temporary problem NO LONGER exists");
            failedTemp = false;
            setAllowWrite = false; // should already be read write
        }
        // else still valid or we have a new license that is also valid 
        
        loggerInternal.debug("License succeeded.  Calling callbacks.");
        for(LicenseChangeHandler callback : callbacks)
        {
            callback.onLicenseChange(descriptor);
        }
             
        if (setAllowWrite)
        {
             if (failed)
             {
                 logger.debug("Removing license veto on write transactions");
             }
             transactionService.setAllowWrite(true, vetoName);
        }
        
        this.context.publishEvent(new ValidLicenseEvent(this.context, descriptor));

    }

    /**
     * Handle case where license is found to be invalid.
     * 
     * @param e                     the exception
     * @param action
     *            the action to be performed, assuming the license is currently valid.
     *            If not currently valid (on startup) we always invalidate and make the
     *            repo read only
     * @throws LicenseException     the license exception
     */
    protected synchronized void licenseFailed(final Exception e, OnFailure action) throws LicenseException
    {
        // If we have not validated the licence before (startup), we invalidate.
        boolean licenseHasBeenValid = licenseDescriptor != null;
        if (!licenseHasBeenValid)
        {
            action = OnFailure.INVALIDATE_LICENSE;
        }

        // Mark transactions as read-only
        if (action == OnFailure.INVALIDATE_LICENSE && !failed)
        {
            licenseDescriptor = null;
            this.transactionService.setAllowWrite(false, vetoName);
        }

        // Log state without using the exception and then with it (just in case that
        // throws an exception)
        if (action == OnFailure.INVALIDATE_LICENSE)
        {
            if (!failed)
            {
                LicenseComponent.logger.error("Alfresco license: Failed to verify license - " +
                        (licenseHasBeenValid
                        ? "Licence no longer valid!"
                        : "Invalid License!"));
                    
                LicenseComponent.loggerInternal.debug("Alfresco license: Failed due to ", e);
                    
                if (currentDescriptorDAO.getLicenseKey() == null)
                {
                        LicenseComponent.logger.error("There is no license in the content store or it can't be reached.");
                }
                LicenseComponent.logger.error(e.getLocalizedMessage());
            }
        }
        else
        {
            if (!failedTemp)
            {
                LicenseComponent.logger.warn(
                        "Alfresco license: Failed to verify license - Possible temporary problem");
                LicenseComponent.loggerInternal.debug("Alfresco license: Failed due to ", e);
            }
        }

        if (action == OnFailure.INVALIDATE_LICENSE && !failed)
        {
            // Log message to say if we have made repository read only
            LicenseComponent.logger.error(
                "Alfresco license: Restricted Alfresco Repository to read-only capability");
        }

        // Publish the event
        if (action == OnFailure.INVALIDATE_LICENSE && !failed)
        {
            this.context.publishEvent(new InvalidLicenseEvent(this.context, e));

            loggerInternal.debug("License failed.  Calling callbacks.");
            for(LicenseChangeHandler callback : callbacks)
            {
                callback.onLicenseFail();
            }
            
        }

        // Set flags so we don't repeat these messages too much, set the repo
        // read only or publish the event again.
        if (action == OnFailure.INVALIDATE_LICENSE)
        {
            failed = true;
            failedTemp = false;
        }
        else
        {
            failed = false;
            failedTemp = true;
        }
    }


    @Override
    public synchronized LicenseDescriptor getLicense()
    {
        // If not verified for the first time, do so.
        if (isInitialVerify())
        {
            try
            {
                verify();
            }
            catch (LicenseException le)
            {
                // do nothing here.   Exception logged in licenseFailed.
            }
        }
        return licenseDescriptor;
    }

    /**
     * Called to shutdown the license component gracefully.
     */
    public void shutdown()
    {
    }

    /**
     * Determine if eligible for trial license creation.
     * 
     * @param currentDescriptor
     *            the current descriptor
     * @param systemBootstrap
     *            the system bootstrap service
     * @param appliedPatchDao
     *            the patch DAO
     * @return true => trial license may be created
     */
    private boolean getTrialEligibility(ImporterBootstrap systemBootstrap, AppliedPatchDAO appliedPatchDao,
            Descriptor currentDescriptor)
    {
        // from clean (open)
        // from clean (enterprise)
        // ==> systemBootstrap == true

        // upgrade from 1.2.1 open to 1.2.1 enterprise
        // ==> patch = true, schema >= 12, versionEdition = open

        // upgrade from 1.2.1 open to 1.3.0 enterprise
        // ==> patch = true, schema >= 12, versionEdition = open

        // upgrade from 1.2.1 enterprise to 1.3.0 enterprise
        // ==> patch = true, schema >= 12, versionEdition = license

        // upgrade from 1.2 open to 1.2.1+ enterprise
        // ==> patch = false, schema < 12, versionEdition = null

        // upgrade from 1.2 enterprise to 1.2.1+ enterprise
        // ==> patch = false, schema < 12, versionEdition = null

        // first determine if the system store has been bootstrapped in this startup sequence
        // if so, a trial license may be created
        boolean trialEligibility = systemBootstrap.hasPerformedBootstrap();

        if (LicenseComponent.loggerInternal.isDebugEnabled())
        {
            LicenseComponent.loggerInternal.debug("Alfresco license: System store bootstrapped: " + trialEligibility);
        }

        // if not, then this could be a pre-installed repository that has yet to be patched with a license
        if (!trialEligibility && currentDescriptor != null)
        {
            final AppliedPatch patch = appliedPatchDao.getAppliedPatch("patch.descriptorUpdate");

            // versionEdition = open
            // patch = true, schema >= 12

            // versionEdition = null
            // patch = false, schema < 12

            // versionEdition = license
            // not eligible

            final int schema = currentDescriptor.getSchema();
            final String edition = currentDescriptor.getEdition();

            if (LicenseComponent.loggerInternal.isDebugEnabled())
            {
                LicenseComponent.loggerInternal.debug("Alfresco license: patch applied: " + (patch != null));
                LicenseComponent.loggerInternal.debug("Alfresco license: schema: " + schema);
                LicenseComponent.loggerInternal.debug("Alfresco license: edition: " + edition);
            }

            if (edition == null)
            {
                trialEligibility = patch == null && schema < 12;
            }
            else
            {
                trialEligibility = patch != null && schema >= 12 && edition.equals("Community Network");
            }
        }

        if (LicenseComponent.loggerInternal.isDebugEnabled())
        {
            LicenseComponent.loggerInternal.debug("Alfresco license: trial eligibility: " + trialEligibility);
        }

        return trialEligibility;
    }

    /**
     * Job for period license verification.
     * 
     * @author davidc
     */
    public static class VerifyLicenseJob implements Job
    {
        public void execute(final JobExecutionContext context) throws JobExecutionException
        {
            final JobDataMap jdm = context.getJobDetail().getJobDataMap();
            final LicenseComponent license = (LicenseComponent) jdm.get("licenseComponent");
            
            try
            {
                license.verify();
            }
            catch (LicenseException le)
            {
                // do nothing here.   Exception logged in licenseFailed.
            }
        }
    }

    Set<LicenseChangeHandler> callbacks = new HashSet<LicenseChangeHandler>(); 
    @Override
    public void registerOnLicenseChange(LicenseChangeHandler callback)
    {
        callbacks.add(callback);
    }

    /**
     * Attempt to reload licenses
     */
    @Override
    public String loadLicense()
    {
        try
        {
            verify();
            // TODO: Get the license path from the verify call
            return I18NUtil.getMessage("system.license.msg.reloaded");
        }
        catch (Exception e)
        {
            if (loggerInternal.isDebugEnabled())
            {
                loggerInternal.debug("Failed to reload license: " + e.getMessage());
            }
            else
            {
                logger.warn("Failed to reload license: " + e.getMessage());
            }
            return I18NUtil.getMessage("system.license.err.reloadFailed", e.getMessage());
            // do nothing here.  Exception logged in licenseFailed.
        }
    }

    @Override
    public String loadLicense(InputStream licenseStream)
    {
        if(verify(licenseStream))
        {
            return INPUTSTREAM_SUCCESS;
        }
        else
        {
            return INPUTSTREAM_FAIL;
        }
    }
    
}
