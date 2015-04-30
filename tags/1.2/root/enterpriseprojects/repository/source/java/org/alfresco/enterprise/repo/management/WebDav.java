package org.alfresco.enterprise.repo.management;

import org.alfresco.service.cmr.webdav.WebDavService;
import org.alfresco.util.PropertyCheck;

public class WebDav extends MBeanSupport implements WebDavMBean
{

    private WebDavService service;

    public void init()
    {
        PropertyCheck.mandatory(this, "service", service);
    }
    
    @Override
    public boolean isEnabled()
    {
        return service.getEnabled();
    }

    public void setWebDavService(WebDavService service)
    {
        this.service = service;
    }

    public WebDavService getWebDavService()
    {
        return service;
    }


}
