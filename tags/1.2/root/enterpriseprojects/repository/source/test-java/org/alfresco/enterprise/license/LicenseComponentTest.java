/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.alfresco.enterprise.heartbeat.HeartBeat;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.descriptor.DescriptorServiceImpl;
import org.alfresco.repo.descriptor.ServerDescriptorDAOImpl;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.service.cmr.admin.RepoUsage.LicenseMode;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.schlichtherle.license.LicenseContentException;
import org.alfresco.service.license.LicenseIntegrityException;

/**
 * Tests for the LicenseComponent. Because LicenseComponent would normally rename *.lic files on the classpath after
 * installing them, this class manages its own classpath and ApplicationContext bootstrapping, adding a test specific
 * 'sandbox' directory on to the classpath.
 * 
 * @author dward
 */
public class LicenseComponentTest extends TestCase implements ApplicationListener
{
    private static final String[] CONFIG_LOCATIONS = new String[]
    {
        "classpath:alfresco/application-context.xml", "classpath:license-application-context-test.xml",
        "classpath*:alfresco/license-application-context-override.xml"
    };

    private static final File SOURCE_DIR;

    static
    {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // Find the source directory on the classpath
        Resource[] resources;
        try
        {
            resources = resolver.getResources("classpath*:/licensetest");
            SOURCE_DIR = resources[0].getFile();
        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

    }
    
    private static Log logger = LogFactory.getLog(LicenseComponentTest.class);

    /** The sandbox directory to be added to the classpath and tidied after text execution. */
    private File sandBoxDir;
    /** The context, to be refreshed during test execution and closed on completion. */
    private ClassPathXmlApplicationContext context;
    /** In the event of an InvalidLicenseEvent will be populated with the causing exception. */
    private Throwable invalidLicenseCause;

    @Override
    protected void setUp() throws Exception
    {
        sandBoxDir = TempFileProvider.getTempDir();

        // Copy this test's files to a sandbox directory
        copyFileRecurse(new File(SOURCE_DIR, getName()), sandBoxDir);

        // Put the sandbox directory at the front of the classpath
        ClassLoader classLoader = new URLClassLoader(new URL[]
        {
            sandBoxDir.toURL()
        }, Thread.currentThread().getContextClassLoader());
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, false);
        context.setClassLoader(classLoader);
        context.addApplicationListener(this);
        this.context = context;
        
        // Force more users for FTP
        Field ftpMaxUsersField = AlfrescoLicenseParam.class.getDeclaredField("ftpMaxUsers");
        ftpMaxUsersField.setAccessible(true);
        ftpMaxUsersField.set(null, Integer.MAX_VALUE);

    }

    @Override
    protected void tearDown() throws Exception
    {
        this.invalidLicenseCause = null;
        deleteFileRecurse(sandBoxDir);
        context.close();
    }
    
    LicenseDescriptor currentLicense = null; 

    public synchronized void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof InvalidLicenseEvent)
        {
            this.invalidLicenseCause = ((InvalidLicenseEvent) event).getCause();
        }
        if (event instanceof ValidLicenseEvent)
        {
            currentLicense = ((ValidLicenseEvent) event).getLicenseDescriptor();
        }
    }

    public synchronized Throwable getInvalidLicenseCause()
    {
        return this.invalidLicenseCause;
    }

    /**
     * Tests that when no license exists and the existing repository meets the criteria, a 30 day license is issued.
     * Also ensures that when the issued license has a maxSchemaVersion in range, the repository is writable (not
     * possible to do this with the other unit test licenses or else they would be usable!)
     */
    public void testFreeTrialPeriod() throws Exception
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        DescriptorService descriptorService = (DescriptorService) context.getBean("descriptorComponent");
        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        assertNotNull("ServerDescriptor is null", serverDescriptor);
        LicenseDescriptor licenseDescriptor = descriptorService.getLicenseDescriptor();
        assertNotNull("LicenseDescriptor is null", licenseDescriptor);
        assertEquals(
                serverDescriptor.getEdition() + " - v" + serverDescriptor.getVersionMajor() + "." + serverDescriptor.getVersionMinor(),
                licenseDescriptor.getSubject());
        assertFalse("trial license should not allow cluster", licenseDescriptor.isClusterEnabled());
        TransactionService transactionService = (TransactionService) context.getBean("transactionService");
        // Because we have overriden the schema number to match the build, we should find that the repository is
        // writeable
        assertFalse(transactionService.isReadOnly());
        
        // Check load license callback
        assertNotNull("license callback not working", currentLicense);
        
    }

    /**
     * Tests that a license issued for the correct edition and version of a server is verified successfully. Also tests
     * that when the maxSchemaVersion of that license is smaller than some of the schema versions of known patches, the
     * repository is in read only mode (this prevents tampering with the configured schema version).
     * 
     * @throws Exception
     *             the exception
     */
    public void testLicense() throws Exception
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        DescriptorService descriptorService = (DescriptorService) context.getBean("descriptorComponent");

        // Ensure the heart beat service has been enabled by looking at the private member variable!
        Field heartBeatField = DescriptorServiceImpl.class.getDeclaredField("heartBeat");
        heartBeatField.setAccessible(true);
        assertNotNull(heartBeatField.get(descriptorService));
        HeartBeat heartBeat = (HeartBeat) heartBeatField.get(descriptorService);
        assertTrue("Heartbeat should be enabled", heartBeat.isEnabled());

        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        assertNotNull("ServerDescriptor is null", serverDescriptor);
        LicenseDescriptor licenseDescriptor = descriptorService.getLicenseDescriptor();
        assertNotNull("LicenseDescriptor is null", licenseDescriptor);
        assertEquals(
                serverDescriptor.getEdition() + " - v" + serverDescriptor.getVersionMajor() + "." + serverDescriptor.getVersionMinor(),
                licenseDescriptor.getSubject());
        TransactionService transactionService = (TransactionService) context.getBean("transactionService");
        // Because the licensed schema number is going to be lower than the current build (to prevent misuse of the test
        // license) we should find that the repository is read only
        assertTrue(transactionService.isReadOnly());
    }

    /**
     * Tests that an otherwise valid but expired license does not pass validation.
     */
    public void testExpiredLicense() throws Throwable
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        Throwable cause = getInvalidLicenseCause();
        assertTrue("Expected LicenseException", cause != null);
        if (!(cause instanceof LicenseContentException))
        {
            throw cause;
        }
    }

    /**
     * Tests that an embedded license issued for the correct edition and version of a server is verified successfully,
     * despite there being an expired license in the extensions directory! Other checks as per {@link #testLicense()}.
     */
    public void testEmbedded() throws Exception
    {
        logger.debug("Test: " + getName());
        
        testLicense();
    }

    /**
     * Tests that a valid license for a different minor version does not pass validation.
     */
    public void testBadVersion() throws Throwable
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        Throwable cause = getInvalidLicenseCause();
        assertTrue("Expected LicenseException", cause != null);
        if (!(cause instanceof LicenseContentException))
        {
            throw cause;
        }
    }

    /**
     * Tests that a otherwise valid license with a schema number smaller than the current version does not pass
     * validation.
     */
    public void testBadSchema() throws Throwable
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        Throwable cause = getInvalidLicenseCause();
        assertTrue("Expected LicenseException", cause != null);
        if (!(cause instanceof LicenseContentException))
        {
            throw cause;
        }
    }
    
    /**
     * Tests that a otherwise valid license signed with a wrong certificate does not work
     * validation.
     */
    public void testWrongCertificate() throws Throwable
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        Throwable cause = getInvalidLicenseCause();
        assertTrue("Expected LicenseException", cause != null);
        if (!(cause instanceof LicenseIntegrityException))
        {
            throw cause;
        }
    }

//    /**
//     * Tests that a otherwise valid license with a number of users smaller than the number of user accounts does not
//     * pass validation.
//     * 
//     * @throws Exception
//     *             the exception
//     */
//    public void testMaxUsers() throws Throwable
//    {
//        context.refresh();
//        Throwable cause = getInvalidLicenseCause();
//        assertTrue("Expected LicenseException", cause != null);
//        if (!(cause instanceof LicenseException))
//        {
//            throw cause;
//        }
//    }

    /**
     * Tests that a license issued for the correct edition and version of a server with a disableHeartBeat parameter is
     * verified successfully. Also tests that the hearbeat service is disabled in this instance.
     */
    public void testNoHeartBeat() throws Exception
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        DescriptorService descriptorService = (DescriptorService) context.getBean("descriptorComponent");

        // Ensure the heart beat service has been disabled by looking at the private member variable!
        Field heartBeatField = DescriptorServiceImpl.class.getDeclaredField("heartBeat");
        heartBeatField.setAccessible(true);
        assertNotNull(heartBeatField.get(descriptorService));
        HeartBeat heartBeat = (HeartBeat) heartBeatField.get(descriptorService);
        assertFalse("Heartbeat should not be enabled", heartBeat.isEnabled());

        // Ensure everything else is above board license wise!
        Descriptor serverDescriptor = descriptorService.getServerDescriptor();
        assertNotNull("ServerDescriptor is null", serverDescriptor);
        LicenseDescriptor licenseDescriptor = descriptorService.getLicenseDescriptor();
        assertNotNull("LicenseDescriptor is null", licenseDescriptor);
        assertEquals(
                serverDescriptor.getEdition() + " - v" + serverDescriptor.getVersionMajor() + "." + serverDescriptor.getVersionMinor(),
                licenseDescriptor.getSubject());
        TransactionService transactionService = (TransactionService) context.getBean("transactionService");
        // Because the licensed schema number is going to be lower than the current build (to prevent misuse of the test
        // license) we should find that the repository is read only
        assertTrue(transactionService.isReadOnly());
    }
    
    
//    /**
//     * Tests that an error not related to the licence is not reated as a license exception.
//     * 
//     * @throws Exception
//     *             the exception
//     */
//    public void testNonLicenseException() throws Exception
//    {
//        context.refresh();
//        
//        LicenseComponent licenseComponent = (LicenseComponent) context.getBean("licenseService");
//        
//        //AlfrescoLicenseManager licenseManager = (AlfrescoLicenseManager) context.getBean("licenseService");
//        
//        MockDescriptorDAO currentRepoDescriptorDAO = (MockDescriptorDAO) context.getBean("currentRepoDescriptorDAO");
//        
//        currentRepoDescriptorDAO.setInjectedException(new AlfrescoRuntimeException("injected test exception"));
//        
//        licenseComponent.verifyLicense();
//        
//        assertTrue("license cause is not null", getInvalidLicenseCause() == null);
//    }

    /**
     * Tests uploading and applying a license through the input stream
     * 
     * @throws Exception
     *             the exception
     */
    public void testInputStreamLoadLicense() throws Exception
    {
        logger.debug("Test: " + getName());
        
        context.refresh();
        DescriptorService descriptorService = (DescriptorService) context.getBean("descriptorComponent");

        //Get valid and invalid licenses
        File licenseDir = new File(SOURCE_DIR, getName() + "/alfresco");
        InputStream badLicense = new FileInputStream(new File(licenseDir, "alfresco2-unit10expired.lic"));
        InputStream goodLicense = new FileInputStream(new File(licenseDir, "alfresco2-unit10.lic"));
        
        //First load an invalid license.  It will fail, as will the reload sinces there is no existing license
        String result = descriptorService.loadLicense(badLicense);
        assertEquals(LicenseService.INPUTSTREAM_FAIL, result);
        //Next load a valid license.
        result = descriptorService.loadLicense(goodLicense);
        assertEquals(LicenseService.INPUTSTREAM_SUCCESS, result);
        //Finally load an invalid license.  It will fail, however the reload will be successful since the last test installed a vaild license.
        result = descriptorService.loadLicense(goodLicense);
        assertEquals(LicenseService.INPUTSTREAM_FAIL, result);
    }

    /**
     * Tests loading a license with cluster enabled
     * validation.
     */
    public void testClusterEnabled() throws Throwable
    {
        logger.debug("Test: " + getName());
               
        context.refresh();
        DescriptorService descriptorService = (DescriptorService) context.getBean("descriptorComponent");
        LicenseDescriptor licenseDescriptor = descriptorService.getLicenseDescriptor();
        
        assertNotNull("license descriptor is null", licenseDescriptor);
        assertTrue("license not cluster enabled", licenseDescriptor.isClusterEnabled());
     
    }

    /**
     * Tests MNT-11033 case. Deadlock between child application context and license initialization.
     */
    @SuppressWarnings("deprecation")
    public void testMNT_11033() throws Throwable
    {
        context = new ClassPathXmlApplicationContext(ApplicationContextHelper.CONFIG_LOCATIONS, false);
        context.refresh();
        final ChildApplicationContextFactory sysAdminSubsystem = (ChildApplicationContextFactory) context.getBean("sysAdmin");
        final LicenseComponent licenseComponent = (LicenseComponent) context.getBean("licenseService");
        sysAdminSubsystem.stop();
        
        String verifyLicenseThreadName = getName() + "_verifyLicenseThread";
        Thread verifyLicenseThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                licenseComponent.verifyLicense();
            }
        }, verifyLicenseThreadName);
        
        String startSysAdminThreadName = getName() + "_startSysAdminThread";
        Thread startSysAdminThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                sysAdminSubsystem.start();
            }
        }, startSysAdminThreadName);
        
        verifyLicenseThread.setDaemon(true);
        startSysAdminThread.setDaemon(true);
        
        // start problematic threads
        verifyLicenseThread.start();
        startSysAdminThread.start();
        
        // wait 10 seconds for threads termination
        verifyLicenseThread.join(10000);
        startSysAdminThread.join(10000);
        
        // check whether threads are still running
        if (verifyLicenseThread.isAlive() && startSysAdminThread.isAlive())
        {
            StringBuilder msg = new StringBuilder("Deadlock found.\n");
            
            ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
            
            // find deadlocked threads
            long[] ids = tmx.findDeadlockedThreads();
            
            if (ids != null)
            {
                ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
                
                for (ThreadInfo info : infos)
                {
                    msg.append(info.toString());
                }
            }
            
            // kill deadlocked threads
            verifyLicenseThread.stop();
            startSysAdminThread.stop();
            
            fail(msg.toString());
        }
    }

    /**
     * Utility method to do a recursive file copy.
     * 
     * @param source
     *            the source file or directory (must exist)
     * @param dest
     *            the destination path (need not exist)
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void copyFileRecurse(File source, File dest) throws IOException
    {
        if (source.isDirectory())
        {
            dest.mkdirs();
            for (File file : source.listFiles())
            {
                copyFileRecurse(file, new File(dest, file.getName()));
            }
        }
        else
        {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(dest);
            byte[] buff = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buff)) != -1)
            {
                out.write(buff, 0, bytesRead);
            }
            in.close();
            out.close();
        }
        dest.setLastModified(source.lastModified());
    }

    /**
     * Utility method to recursively delete a file or directory.
     * 
     * @param file
     *            the file or directory
     */
    private static void deleteFileRecurse(File file)
    {
        if (file.isDirectory())
        {
            // from Thor
            File[] children = file.listFiles();
            if (children != null)
            {
                for (File child : children)
                {
                    deleteFileRecurse(child);
                }
            }
        }
        file.delete();
    }

    /**
     * A class that allows us to synthesise different server and repository versions and editions.
     */
    public static class MockDescriptorDAO extends ServerDescriptorDAOImpl
    {
        RuntimeException injectedException;
        public void setInjectedException(RuntimeException e)
        {
            this.injectedException = e;
        }

        /** The license key. */
        private byte[] licenseKey;

        /**
         * Sets the schema version.
         * 
         * @param schemaVersion
         *            the new schema version
         */
        public void setSchemaVersion(int schemaVersion)
        {
            serverProperties.setProperty("version.schema", String.valueOf(schemaVersion));
        }

        @Override
        public byte[] getLicenseKey()
        {
            return this.licenseKey;
        }

        @Override
        public Descriptor getDescriptor()
        {
            
            if(injectedException != null)
            {
                throw injectedException;
            }
            
            if (serverProperties.isEmpty())
            {
                return null;
            }
            return super.getDescriptor();
        }

        @Override
        public Descriptor updateDescriptor(Descriptor serverDescriptor, LicenseMode licenseMode)
        {
            try
            {
                setRepositoryName(serverDescriptor.getName());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            serverProperties.setProperty("version.major", serverDescriptor.getVersionMajor());
            serverProperties.setProperty("version.minor", serverDescriptor.getVersionMinor());
            serverProperties.setProperty("version.revision", serverDescriptor.getVersionRevision());
            serverProperties.setProperty("version.label", serverDescriptor.getVersionLabel());
            serverProperties.setProperty("version.build", serverDescriptor.getVersionBuild());
            serverProperties.setProperty("version.edition", serverDescriptor.getEdition());
            serverProperties.setProperty("version.schema", String.valueOf(serverDescriptor.getSchema()));
            return getDescriptor();
        }

        @Override
        public void updateLicenseKey(byte[] key)
        {
            this.licenseKey = key;
        }
    }
}