/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.event;

import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Simple records management event type implementation
 * 
 * @author Roy Wetherall
 */
public class SimpleRecordsManagementEventTypeImpl implements RecordsManagementEventType, BeanNameAware
{
    /** Display label lookup prefix */
    protected static final String LOOKUP_PREFIX = "rmeventservice.";
    
    /** Records management event service */
    protected RecordsManagementEventService recordsManagementEventService;
    
    /** Name */
    protected String name;
    
    /**
     * Set the records management event service
     * 
     * @param recordsManagementEventService     records management service
     */
    public void setRecordsManagementEventService(RecordsManagementEventService recordsManagementEventService)
    {
        this.recordsManagementEventService = recordsManagementEventService;
    }
    
    /**
     * Initialisation method
     */
    public void init()
    {
        recordsManagementEventService.registerEventType(this);
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventType#isAutomaticEvent()
     */
    public boolean isAutomaticEvent()
    {
        return false;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventType#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name)
    {
        this.name = name;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventType#getDisplayLabel()
     */
    public String getDisplayLabel()
    {
        return I18NUtil.getMessage(LOOKUP_PREFIX + getName());
    }
}
