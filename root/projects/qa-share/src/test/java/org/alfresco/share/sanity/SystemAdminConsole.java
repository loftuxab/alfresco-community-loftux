package org.alfresco.share.sanity;

import org.alfresco.po.share.*;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;

import org.alfresco.share.site.document.DocumentDetailsActionsTest;
import org.alfresco.share.util.*;

import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.util.List;
import static org.testng.Assert.*;

/**
 * 
 * @author Maryia Zaichanka
 */
@Listeners(FailedTestListener.class)
public class SystemAdminConsole extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(DocumentDetailsActionsTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "Sanity", "EnterpriseOnly" })
    public void AONE_8097()
    {

        // Open to Admin Console System Summary page
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        // Verify that Admin Console Summary Page displayed with next information on it
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.AlfreacoHome.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.AlfreacoEdition.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.AlfreacoVersion.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.JavaHome.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.JavaVersion.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.JavaVmVendor.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.OperatingSystem.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.Version.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.Architecture.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.FreeMemory.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.MaximumMemory.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.systemInformation.TotalMemory.get()), "Info isn't present");

        // Verify File Systems column
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.fileSystems.CIFS.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.fileSystems.FTP.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.fileSystems.NFS.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.fileSystems.WebDAV.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.fileSystems.CIFS.get()), "Radio button isn't present");

        if (alfrescoVersion.getVersion() < 5.0)
        {
            assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.fileSystems.SPP.get()), "Radio button isn't present");
        }
        else
        {
            assertEquals(sysSummaryPage.getValue(SystemSummaryPage.fileSystems.SPP.get()), "Not Installed", "");
        }

        // Verify Email column
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.email.IMAP.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.email.Inbound.get()), "Radio button isn't present");

        // Transformation Services section
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.transformationServices.OpenOfficeDirect.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.transformationServices.JODConverter.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.transformationServices.SWFTools.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.transformationServices.ImageMagic.get()), "Radio button isn't present");
        String ffValue = sysSummaryPage.getValue(SystemSummaryPage.transformationServices.FFMpeg.get());
        assertTrue(ffValue.contains("nstalled"), "");

        // Auditing services section
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.auditingServices.Audit.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.auditingServices.CMISChangeLog.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.auditingServices.AlfrescoAccess.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.auditingServices.Tagging.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.auditingServices.Sync.get()), "Radio button isn't present");

        // Indexing Subsystems section
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.indexingSubsystem.Solr.get()), "Radio button isn't present");
        assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.indexingSubsystem.NoIndex.get()), "Radio button isn't present");


        if (alfrescoVersion.getVersion() < 5.0)
        {
            assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.indexingSubsystem.Lucene.get()), "Radio button isn't present");
        }
        else
        {
            assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.indexingSubsystem.Solr4.get()), "Radio button isn't present");
        }

        // Content Stores section
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.contentStores.StorePath.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.contentStores.SpaceAvailable.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.contentStores.SpaceUsed.get()), "Info isn't present");

        // Repository Clustering
        Assert.assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.repositoryClustering.Clustering.get()), "");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.repositoryClustering.ClusterMembers.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.repositoryClustering.ClusterName.get()), "Info isn't present");

        // Activities Feed section
        Assert.assertTrue(sysSummaryPage.isRadioButtonPresent(SystemSummaryPage.activitiesFeed.Feed.get()), "Radio button isn't present");

        // Alfresco Modules Packages (AMPs)
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.modulePackages.CurrentlyInstalled.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.modulePackages.PreviouslyInstalled.get()), "Info isn't present");

        // Authentication section
        List<String> autColumnNames = sysSummaryPage.getAutHeadDirectories();
        Assert.assertTrue(autColumnNames.contains("Name"), "Authentication Directories table names aren't displayed");
        Assert.assertTrue(autColumnNames.contains("Type"), "Authentication Directories table names aren't displayed");

        List<String> autNames = sysSummaryPage.getAutDirectoriesNames();
        assertNotEquals(autNames, "", "Authentication Directories aren't displayed");

        // Users and Groups
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.usersAndGroups.Users.get()), "Info isn't present");
        Assert.assertTrue(sysSummaryPage.isDataPresent(SystemSummaryPage.usersAndGroups.Groups.get()), "Info isn't present");

    }

    @Test(groups = { "Sanity", "EnterpriseOnly", "Cluster" })
    public void AONE_8104()
    {
        // Admin user is logged into the Share
        SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();

        // Verify Repository Server Clustering tab is present in the left-hand side of the Console
        assertTrue(sysSummaryPage.isConsoleLinkPresent(AdminConsoleLink.RepositoryServerClustering), "");

        // Click on the Repository Server Clustering tab
        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();

        Assert.assertTrue(clusteringPage.isClusterEnabled(), "Cluster isn't enabled");

        List<String> clusterMembers = clusteringPage.getClusterMembers();
        assertTrue(clusterMembers.size()>=2, "Number of cluster members is less than two");

    }





}
