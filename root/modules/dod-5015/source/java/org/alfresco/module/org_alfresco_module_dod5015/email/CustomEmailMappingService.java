package org.alfresco.module.org_alfresco_module_dod5015.email;

import java.util.Set;

public interface CustomEmailMappingService
{
    public Set<CustomMapping> getCustomMappings();

    public void addCustomMapping(String from, String to);
    
    public void deleteCustomMapping(String from, String to);
    
    public void init();
}
