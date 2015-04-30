package org.alfresco.enterprise.license;

import org.alfresco.service.license.LicenseDescriptor;
import org.springframework.context.ApplicationEvent;

/**
 * Used to broadcast notification of a valid license being verified and loaded into the repo
 */ 
public class ValidLicenseEvent extends ApplicationEvent
{
    LicenseDescriptor descriptor;
    public ValidLicenseEvent(Object source, LicenseDescriptor descriptor)
    {
        super(source);
        this.descriptor = descriptor;
    }
    
    public LicenseDescriptor getLicenseDescriptor()
    {
        return descriptor;
    }

}
