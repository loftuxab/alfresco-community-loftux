/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync;

import org.alfresco.enterprise.repo.sync.SyncAdminService;
import org.alfresco.enterprise.repo.sync.SyncAdminServiceImplTest;
import org.alfresco.enterprise.repo.sync.SyncChangeMonitorTest;
import org.alfresco.enterprise.repo.sync.SyncServiceImplTest;
import org.alfresco.enterprise.repo.sync.SyncTrackerComponentTest;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceImplTest;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditServiceIntegrationTest;
import org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagementTest;
import org.alfresco.enterprise.repo.web.scripts.sync.connector.CloudConnectorWebScriptsTest;
import org.alfresco.enterprise.repo.web.scripts.sync.transport.CloudTransportWebScriptsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class is a holder for the various test classes associated with the {@link SyncAdminService}.
 * It is not (at the time of writing) intended to be incorporated into the automatic build
 * which will find the various test classes and run them individually.
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // These are all based on BaseSyncServiceImplTest
    // Audit needs to go first
    SyncAuditServiceImplTest.class,
    SyncAuditServiceIntegrationTest.class,
    SyncAdminServiceImplTest.class,
    SyncChangeMonitorTest.class,
    SyncTrackerComponentTest.class,

    // Service level tests that aren't in the BaseSyncServiceImplTest Family
    SsmnChangeManagementTest.class,
    SyncServiceImplTest.class,
    
    // All REST-based tests at the end as they trigger a spring context reload.
    
    // The Cloud*WebScriptsTest tests will pass if they are the first REST API-based tests to run in this suite.
    // If they are not the first, they will fail as they assume the user "remote.user" has never logged in, initially.
    CloudConnectorWebScriptsTest.class,
    CloudTransportWebScriptsTest.class,
    
    RemoteSyncedNodeRestApiTest.class,
    SyncAdminServiceRestApiTest.class,
    SyncAuditServiceRestApiTest.class,
    SyncConfigGetTest.class
})
public class AllSyncServiceTests
{
    // Intentionally empty
}
