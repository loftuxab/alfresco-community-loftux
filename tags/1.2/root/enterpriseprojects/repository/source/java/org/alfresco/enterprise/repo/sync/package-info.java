/**
 * This package is the uppermost package for classes and interfaces relating to the Cloud Sync feature.
 * <p/>
 * In Cloud Sync, 1..n Enterprise Alfresco server instances sync nodes to and from the (single, clustered) Cloud Alfresco server instance. These server instances are respectively
 * known as On Premise Repos and the Cloud Repo.
 * The major Java types used to achieve this are as follows:
 * <ul>
 *   <li>Before any data can be synced, a user must have an account on the Cloud Repo and must tell the On Premise Repo what their Cloud credentials are.
 *       These data are persisted by the {@link org.alfresco.service.cmr.remotecredentials.RemoteCredentialsService}.</li>
 *   <li>The {@link SyncAdminService} is responsible for the CRUD of {@link SyncSetDefinition Sync Set Definitions (SSDs)}. Using the {@link org.alfresco.enterprise.repo.sync.SyncAdminService}
 *       sync sets can be created with one or many {@link org.alfresco.enterprise.repo.sync.SyncModel#ASPECT_SYNC_SET_MEMBER_NODE Sync Set Member Nodes (SSMNs)}, which are
 *       nodes in the On Premise Repo which will be synced to the Cloud Repo.</li>
 *   <li>Any node changes which are relevant for the Cloud Sync feature will be captured by the {@link org.alfresco.enterprise.repo.sync.SyncChangeMonitor}
 *       which registers various behaviours/policies on the relevant content classes.</li>
 *   <li>The {@link org.alfresco.enterprise.repo.sync.SyncChangeMonitor} calls the {@link SyncAuditService} with the relevant information for that event and the
 *       {@link org.alfresco.enterprise.repo.sync.audit.SyncAuditService} records those data within Alfresco's normal {@link org.alfresco.service.cmr.audit.AuditService audit tables}.
 *       The {@link org.alfresco.enterprise.repo.sync.audit.SyncAuditService} also provides an API for easy querying of the recorded events.</li>
 *   <li>Having set up an SSD at each end, there needs to be some trigger event that cause the member nodes to be pushed to the Cloud.
 *       Various triggers are present, including the {@link org.alfresco.enterprise.repo.sync.OnPremiseSyncPushJob} and the {@link org.alfresco.enterprise.repo.sync.OnPremiseSyncPullJob}
 *       which respectively trigger the pushing and pulling of changes to and from the Cloud Repo.
 *   </li>
 *   <li>Having been triggered, it is the {@link org.alfresco.enterprise.repo.sync.SyncTrackerComponent} which coordinates this data transfer, e.g. ensuring that pushes and
 *       pulls are safely run without concurrency errors.
 *       It uses the {@link org.alfresco.enterprise.repo.sync.audit.SyncAuditService} to get the relevant change event data, optionally
 *       aggregating multiple changes together using the {@link org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagement} class.</li>
 *   <li>The {@link org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent SyncChangeEvents} which the {@link org.alfresco.enterprise.repo.sync.audit.SyncAuditService}
 *       provides are turned into {@link org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo} by the {@link org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagement}
 *       component and are then passed to the {@link org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport} which is responsible for the transport over the wire.
 *       It relies on the {@link org.alfresco.enterprise.repo.sync.connector.CloudConnectorService} and the {@link org.alfresco.service.cmr.remoteticket.RemoteAlfrescoTicketService}
 *       to call the necessary webscripts with the correct authentication.</li>
 *   <li>TODO {@link org.alfresco.enterprise.repo.sync.SyncService}</li>
 * </ul>
 */
package org.alfresco.enterprise.repo.sync;
