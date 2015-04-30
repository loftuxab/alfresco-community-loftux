/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.heartbeat;

import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

import javax.sql.DataSource;

import org.alfresco.encryption.EncryptingOutputStream;
import org.alfresco.enterprise.repo.cluster.core.ClusterService;
import org.alfresco.repo.descriptor.DescriptorDAO;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.usage.RepoUsageComponent;
import org.alfresco.service.cmr.admin.RepoUsage;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseException;
import org.alfresco.service.license.LicenseService;
import org.alfresco.service.license.LicenseService.LicenseChangeHandler;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.util.Base64;

import de.schlichtherle.util.ObfuscatedString;

/**
 * This class communicates some very basic repository statistics to Alfresco on a regular basis.
 * 
 * @author dward
 */
public class HeartBeat implements LicenseChangeHandler
{
    private static final String CLUSTER_SERVICE_BEAN_NAME = "ClusterService";

    /** The default URL we post encrypted data to. */
    private static final String DEFAULT_URL = "http://hbrx.alfresco.com/heartbeat/";
    
    /** The default enable state */
    private static final boolean DEFAULT_HEARTBEAT_ENABLED = true;

    /** The logger. */
    private static final Log logger = LogFactory.getLog(HeartBeat.class);

    /** The relative path to the public keystore resource. */
    static final String PUBLIC_STORE = "classpath:org/alfresco/enterprise/heartbeat/heartbeatpublic.keystore";

    /** The password protecting this store. */
    static final char[] PUBLIC_STORE_PWD = new ObfuscatedString(new long[]
    {
        0x7D47AC5E71B3B560L, 0xD6F1405DC20AE70AL
    }).toString().toCharArray();

    /** The transaction service. */
    private final TransactionService transactionService;

    /** DAO for current descriptor. */
    private final DescriptorDAO serverDescriptorDAO;

    /** DAO for current repository descriptor. */
    private final DescriptorDAO currentRepoDescriptorDAO;
    
    private final LicenseService licenseService;
    
    private final Scheduler scheduler;
    
    private RepoUsageComponent repoUsageComponent;

    /** URL to post heartbeat to. */
    private String heartBeatUrl;
    
    private boolean testMode = true;
    
    private final String JOB_NAME = "heartbeat";
    
    /** Is the heartbeat enabled */ 
    private boolean enabled = DEFAULT_HEARTBEAT_ENABLED;

    /** The authority service. */
    private final AuthorityService authorityService;

    /** The data source. */
    private final DataSource dataSource;

    /** The public key used for encryption. */
    private final PublicKey publicKey;
    /**
     * The parameters that we expect to remain static throughout the lifetime of the repository. There is no need to
     * continuously update these.
     */
    private Map<String, String> staticParameters;

    /** A secure source of random numbers used for encryption. */
    private SecureRandom random;

    /** Provides information about clustering */
    private ClusterService clusterService;
    
    /**
     * Initialises the heart beat service. Note that dependencies are intentionally 'pulled' rather than injected
     * because we don't want these to be reconfigured.
     * 
     * @param context
     *            the context
     */
    public HeartBeat(final ApplicationContext context)
    {
        this(context, false);
    }

    /**
     * Initialises the heart beat service, potentially in test mode. Note that dependencies are intentionally 'pulled'
     * rather than injected because we don't want these to be reconfigured.
     * 
     * @param context
     *            the context
     * @param testMode
     *            are we running in test mode? If so we send data to local port 9999 rather than an alfresco server. We
     *            also use a special test encryption certificate and ping on a more frequent basis.
     */
    public HeartBeat(final ApplicationContext context, final boolean testMode)
    {
        logger.debug("Initialising HeartBeat");
        this.transactionService = (TransactionService) context.getBean("transactionService");
        this.serverDescriptorDAO = (DescriptorDAO) context.getBean("serverDescriptorDAO");
        this.currentRepoDescriptorDAO = (DescriptorDAO) context.getBean("currentRepoDescriptorDAO");
        this.authorityService = (AuthorityService) context.getBean("authorityService");
        this.dataSource = (DataSource) context.getBean("dataSource");
        this.scheduler = (Scheduler) context.getBean("schedulerFactory");
        this.repoUsageComponent = (RepoUsageComponent) context.getBean("repoUsageComponent");
        
        // ALF-19744: build failing as ClusterService is enterprise only.
        if (context.containsBean(CLUSTER_SERVICE_BEAN_NAME))
        {
            // Check type - due to IndexRecoveryJob minimal-context.xml hack.
            if (context.getBean(CLUSTER_SERVICE_BEAN_NAME) instanceof ClusterService)
            {
                this.clusterService = (ClusterService) context.getBean(CLUSTER_SERVICE_BEAN_NAME);
            }
        }
        
        this.testMode = testMode;

        try
        {
            // Load the public key from the key store (use the trial one if this is a unit test)
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            final InputStream in = context.getResource(HeartBeat.PUBLIC_STORE).getInputStream();
            keyStore.load(in, HeartBeat.PUBLIC_STORE_PWD);
            in.close();
            final Certificate cert = keyStore.getCertificate(JOB_NAME);
            this.publicKey = cert.getPublicKey();

            LicenseService licenseService = null;
            try
            {
                licenseService = (LicenseService) context.getBean("licenseService");
                
                licenseService.registerOnLicenseChange(this);
            }
            catch (NoSuchBeanDefinitionException e)
            {
                logger.error("licenseService not found", e);
            }
            this.licenseService = licenseService;
            
            // We force the job to be scheduled regardless of the potential state of the licenses
            scheduleJob();
        }
        catch (final RuntimeException e)
        {
            throw e;
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private synchronized void setHeartBeatUrl(String heartBeatUrl)
    {
        this.heartBeatUrl = heartBeatUrl;
    }

    // Determine the URL to send the heartbeat to from the license if not set
    private synchronized String getHeartBeatUrl()
    {
        if (heartBeatUrl == null)
        {
            LicenseDescriptor licenseDescriptor = licenseService.getLicense();
            String url = (licenseDescriptor == null) ? null : licenseDescriptor.getHeartBeatUrl();
            setHeartBeatUrl(url == null ? HeartBeat.DEFAULT_URL : url);
        }
        logger.debug("heartBeatUrl="+heartBeatUrl);
        return heartBeatUrl;
    }

    /**
     * @return          <tt>true</tt> if the heartbeat is currently enabled
     */
    public synchronized boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Initializes static parameters on first invocation. Avoid doing it on construction due to bootstrap dependencies
     * (e.g. patch service must have run)
     * 
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private synchronized void lazyInit() throws GeneralSecurityException, IOException
    {
        if (this.staticParameters == null)
        {
            this.staticParameters = new TreeMap<String, String>();

            // Load up the static parameters
            final String ip = getLocalIps();
            this.staticParameters.put("ip", ip);
            final String uid;
            final Descriptor currentRepoDescriptor = this.currentRepoDescriptorDAO.getDescriptor();
            if (currentRepoDescriptor != null)
            {
                uid = currentRepoDescriptor.getId();
                this.staticParameters.put("uid", uid);
            }
            else
            {
                uid = "Unknown";
            }
                        
            final Descriptor serverDescriptor = this.serverDescriptorDAO.getDescriptor();
            this.staticParameters.put("repoName", serverDescriptor.getName());
            this.staticParameters.put("edition", serverDescriptor.getEdition());
            this.staticParameters.put("versionMajor", serverDescriptor.getVersionMajor());
            this.staticParameters.put("versionMinor", serverDescriptor.getVersionMinor());
            this.staticParameters.put("schema", String.valueOf(serverDescriptor.getSchema()));
            this.staticParameters.put("numUsers", String.valueOf(this.authorityService.getAllAuthoritiesInZone(
                    AuthorityService.ZONE_APP_DEFAULT, AuthorityType.USER).size()));
            this.staticParameters.put("numGroups", String.valueOf(this.authorityService.getAllAuthoritiesInZone(
                    AuthorityService.ZONE_APP_DEFAULT, AuthorityType.GROUP).size()));
            
            if(repoUsageComponent != null)
            {
            	RepoUsage usage = repoUsageComponent.getUsage();
            	
            	if (usage.getUsers() != null)
            	{
                    this.staticParameters.put("licenseUsers", String.valueOf(usage.getUsers()));
            	}
            }


            // Use some of the unique parameters to seed the random number generator used for encryption
            this.random = SecureRandom.getInstance("SHA1PRNG");
            this.random.setSeed((uid + ip + System.currentTimeMillis()).getBytes("UTF-8"));

        }
    }

    /**
     * Sends encrypted data over HTTP.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws GeneralSecurityException
     *             an encryption related exception
     */
    public void sendData() throws IOException, GeneralSecurityException
    {
        RetryingTransactionCallback<Void> initCallback = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                lazyInit();
                return null;
            }
        };
        transactionService.getRetryingTransactionHelper().doInTransaction(initCallback, true);

        String heartBeatUrl = getHeartBeatUrl();
        final HttpURLConnection req = (HttpURLConnection) new URL(heartBeatUrl).openConnection();
        try
        {
            req.setRequestMethod("POST");
            req.setRequestProperty("Content-Type", "application/octet-stream");
            // Let's not use chunked encoding because there appear to be some proxies that can't cope with it!
            // req.setChunkedStreamingMode(1024);
            req.setConnectTimeout(2000);
            req.setDoOutput(true);
            sendData(req.getOutputStream());
            if (req.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException(req.getResponseMessage());
            }
        }
        finally
        {
            try
            {
                req.disconnect();
            }
            catch (final Exception e)
            {
            }
        }
    }

    /**
     * Writes the heartbeat data to a given output stream. Parameters are serialized in XML format for maximum forward
     * compatibility.
     * 
     * @param dest
     *            the stream to write to
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws GeneralSecurityException
     *             an encryption related exception
     */
    public synchronized void sendData(final OutputStream dest) throws IOException, GeneralSecurityException
    {
        // Complement the static parameters with some dynamic ones
        final Map<String, String> params = new TreeMap<String, String>(this.staticParameters);
        params.put("maxNodeId", String.valueOf(getMaxNodeId()));
        
        String licenseKey = transactionService.getRetryingTransactionHelper().doInTransaction(
                new RetryingTransactionCallback<String>()
                {
                    public String execute() 
                    {
                        try
                        {   
                            byte[] licenseKey = currentRepoDescriptorDAO.getLicenseKey();
                            return licenseKey == null ? null : Base64.encodeBytes(licenseKey, Base64.DONT_BREAK_LINES);
                        }
                        catch (LicenseException e)
                        {
                            // Swallow Licence Exception Here
                            // Don't log error: It'll be reported later and the logging fails
                            return null;
                        }
                    }
                }, true);
   
        if (licenseKey != null)
        {
            params.put("licenseKey", licenseKey);
        }
        Runtime runtime = Runtime.getRuntime();
        params.put("memFree", String.valueOf(runtime.freeMemory()));
        params.put("memMax", String.valueOf(runtime.maxMemory()));
        params.put("memTotal", String.valueOf(runtime.totalMemory()));
        if (clusterService != null)
        {
            params.put("numClusterMembers", String.valueOf(clusterService.getNumActiveClusterMembers()));
        }
        // Compress and encrypt the output stream
        OutputStream out = new GZIPOutputStream(new EncryptingOutputStream(dest, this.publicKey, this.random), 1024);

        // Encode the parameters to XML
        XMLEncoder encoder = null;
        try
        {
            encoder = new XMLEncoder(out);
            encoder.writeObject(params);
        }
        finally
        {
            if (encoder != null)
            {
                try
                {
                    encoder.close();
                    out = null;
                }
                catch (final Exception e)
                {
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (final Exception e)
                {
                }
            }
        }
    }

    /**
     * The scheduler job responsible for triggering a heartbeat on a regular basis.
     */
    public static class HeartBeatJob implements Job
    {
        public void execute(final JobExecutionContext jobexecutioncontext) throws JobExecutionException
        {
            final JobDataMap dataMap = jobexecutioncontext.getJobDetail().getJobDataMap();
            final HeartBeat heartBeat = (HeartBeat) dataMap.get("heartBeat");
            try
            {
                heartBeat.sendData();
            }
            catch (final Exception e)
            {
                if (logger.isDebugEnabled())
                {
                    // Verbose logging
                    HeartBeat.logger.debug("Heartbeat job failure", e);
                }
                else
                {
                    // Heartbeat errors are non-fatal and will show as single line warnings
                    HeartBeat.logger.warn(e.toString());
                    throw new JobExecutionException(e);
                }
            }
        }
    }

    /**
     * Attempts to get all the local IP addresses of this machine in order to distinguish it from other nodes in the
     * same network. The machine may use a static IP address in conjunction with a loopback adapter (e.g. to support
     * Oracle on Windows), so the IP of the default network interface may not be enough to uniquely identify this
     * machine.
     * 
     * @return the local IP addresses, separated by the '/' character
     */
    private String getLocalIps()
    {
        final StringBuilder ip = new StringBuilder(1024);
        boolean first = true;
        try
        {
            final Enumeration<NetworkInterface> i = NetworkInterface.getNetworkInterfaces();
            while (i.hasMoreElements())
            {
                final NetworkInterface n = i.nextElement();
                final Enumeration<InetAddress> j = n.getInetAddresses();
                while (j.hasMoreElements())
                {
                    InetAddress a = j.nextElement();
                    if (a.isLoopbackAddress())
                    {
                        continue;
                    }
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        ip.append('/');
                    }
                    ip.append(a.getHostAddress());
                }
            }
        }
        catch (final Exception e)
        {
            // Ignore
        }
        return first ? "127.0.0.1" : ip.toString();
    }

    /**
     * Gets the maximum repository node id. Note that this isn't the best indication of size, because on oracle, all
     * unique IDs are generated from the same sequence. A count(*) would result in an index scan.
     * 
     * @return the max node id
     */
    private int getMaxNodeId()
    {
        Connection connection = null;
        Statement stmt = null;
        try
        {
            connection = this.dataSource.getConnection();
            connection.setAutoCommit(true);
            stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery("select max(id) from alf_node");
            if (!rs.next())
            {
                return 0;
            }
            return rs.getInt(1);
        }
        catch (final SQLException e)
        {
            return 0;
        }
        finally
        {
            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (final Exception e)
                {
                }
            }
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (final Exception e)
                {
                }
            }
        }
    }
    
    /**
     * Listens for license changes.  If a license is change or removed, the heartbeat job is resheduled.
     */
    public synchronized void onLicenseChange(LicenseDescriptor licenseDescriptor)
    {
        logger.debug("Update license called");

        setHeartBeatUrl(licenseDescriptor.getHeartBeatUrl());
        boolean newEnabled = !licenseDescriptor.isHeartBeatDisabled();
        
        if (newEnabled != enabled)
        {
            logger.debug("State change of heartbeat");
            this.enabled = newEnabled;
            try
            {
                scheduleJob();
            }
            catch (Exception e)
            {
                logger.error("Unable to schedule heart beat", e);
            }
        }
    }

    /**
     * License load failure resets the heartbeat back to the default state
     */
    @Override
    public synchronized void onLicenseFail()
    {
        boolean newEnabled = DEFAULT_HEARTBEAT_ENABLED;
        
        if (newEnabled != enabled)
        {
            logger.debug("State change of heartbeat");
            this.enabled = newEnabled;
            try
            {
                scheduleJob();
            }
            catch (Exception e)
            {
                logger.error("Unable to schedule heart beat", e);
            }
        }
    }

    /**
     * Start or stop the hertbeat job depending on whether the heartbeat is enabled or not
     * @throws SchedulerException
     */
    private synchronized void scheduleJob() throws SchedulerException
    {
        // Schedule the heart beat to run regularly
        if(enabled)
        {
            logger.debug("heartbeat job scheduled");
            final JobDetail jobDetail = new JobDetail(JOB_NAME, Scheduler.DEFAULT_GROUP, HeartBeatJob.class);
            jobDetail.getJobDataMap().put("heartBeat", this);
            // Ensure the job wasn't already scheduled in an earlier retry of this transaction
            final String triggerName = JOB_NAME + "Trigger";
            scheduler.unscheduleJob(triggerName, Scheduler.DEFAULT_GROUP);
            final Trigger trigger = new SimpleTrigger(triggerName, Scheduler.DEFAULT_GROUP, new Date(), null,
            SimpleTrigger.REPEAT_INDEFINITELY, testMode ? 1000 : 4 * 60 * 60 * 1000);
            scheduler.scheduleJob(jobDetail, trigger);
        }
        else
        {
            logger.debug("heartbeat job unscheduled");
            final String triggerName = JOB_NAME + "Trigger";
            scheduler.unscheduleJob(triggerName, Scheduler.DEFAULT_GROUP);
        }
    }
}
