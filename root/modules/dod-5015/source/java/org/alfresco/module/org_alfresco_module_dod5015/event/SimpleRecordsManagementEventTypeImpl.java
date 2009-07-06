/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.event;

import org.springframework.beans.factory.BeanNameAware;



/**
 * Simple records management event type implementation
 * 
 * @author Roy Wetherall
 */
public class SimpleRecordsManagementEventTypeImpl implements RecordsManagementEventType, BeanNameAware
{
    private RecordsManagementEventService recordsManagementEventService;
    
    private String name;
    
    public void setRecordsManagementEventService(RecordsManagementEventService recordsManagementEventService)
    {
        this.recordsManagementEventService = recordsManagementEventService;
    }
    
    public void init()
    {
        recordsManagementEventService.registerEventType(this);
    }
    
    public boolean isAutomaticEvent()
    {
        return false;
    }

    public String getName()
    {
        return this.name;
    }

    public void setBeanName(String name)
    {
        this.name = name;
    }
}
