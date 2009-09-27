/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.module.org_alfresco_module_dod5015.notification.RecordsManagementNotificationService;
import org.alfresco.module.org_alfresco_module_dod5015.security.RecordsManagementSecurityService;
import org.alfresco.repo.service.ServiceDescriptorRegistry;

/**
 * Records management service registry implementation
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceRegistryImpl extends ServiceDescriptorRegistry 
                                                  implements RecordsManagementServiceRegistry
{
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementActionService()
     */
    public RecordsManagementActionService getRecordsManagementActionService()
    {
        return (RecordsManagementActionService)getService(RECORDS_MANAGEMENT_ACTION_SERVICE);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementAdminService()
     */
    public RecordsManagementAdminService getRecordsManagementAdminService()
    {
        return (RecordsManagementAdminService)getService(RECORDS_MANAGEMENT_ADMIN_SERVICE);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementEventService()
     */
    public RecordsManagementEventService getRecordsManagementEventService()
    {
        return (RecordsManagementEventService)getService(RECORDS_MANAGEMENT_EVENT_SERVICE);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementService()
     */
    public RecordsManagementService getRecordsManagementService()
    {
        return (RecordsManagementService)getService(RECORDS_MANAGEMENT_SERVICE);
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementSecurityService()
     */
    public RecordsManagementSecurityService getRecordsManagementSecurityService()
    {
        return (RecordsManagementSecurityService)getService(RECORDS_MANAGEMENT_SECURITY_SERVICE);
    }

    /*
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementAuditService()
     */
    public RecordsManagementAuditService getRecordsManagementAuditService()
    {
        return (RecordsManagementAuditService)getService(RECORDS_MANAGEMENT_AUDIT_SERVICE);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementServiceRegistry#getRecordsManagementNotificationService()
     */
    public RecordsManagementNotificationService getRecordsManagementNotificationService()
    {
        return (RecordsManagementNotificationService)getService(RECORDS_MANAGEMENT_NOTIFICATION_SERVICE);
    }
}
