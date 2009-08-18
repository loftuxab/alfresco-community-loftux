/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.service.NotAuditable;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Records management service registry
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementServiceRegistry extends ServiceRegistry
{
    static final QName RECORDS_MANAGEMENT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementService");
    static final QName RECORDS_MANAGEMENT_ADMIN_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementAdminService");
    static final QName RECORDS_MANAGEMENT_ACTION_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementActionService");
    static final QName RECORDS_MANAGEMENT_EVENT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementEventService");
    static final QName RECORDS_MANAGEMENT_AUDIT_SERVICE = QName.createQName(NamespaceService.ALFRESCO_URI, "RecordsManagementAuditService");
     
    @NotAuditable
    RecordsManagementService getRecordsManagementService();
    
    @NotAuditable
    RecordsManagementAdminService getRecordsManagementAdminService();
    
    @NotAuditable
    RecordsManagementActionService getRecordsManagementActionService();
    
    @NotAuditable
    RecordsManagementEventService getRecordsManagementEventService();
    
    @NotAuditable
    RecordsManagementAuditService getRecordsManagementAuditService();
}
