package org.alfresco.enterprise.repo.sync.connector.impl;

import org.alfresco.enterprise.license.InvalidLicenseEvent;
import org.alfresco.enterprise.license.ValidLicenseEvent;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.license.LicenseDescriptor;
import org.alfresco.service.license.LicenseService;
import org.alfresco.util.PropertyCheck;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Cloud key provided by license
 */
public class KeyProviderLicense extends KeyProvider 
    implements ApplicationListener<ApplicationEvent>
{ 
    private String key = null;
    
    private DescriptorService descriptorService;
    
    public void init()
    {
        PropertyCheck.mandatory(this, "descriptorService", descriptorService);

    }

    @Override
    public String getKey()
    {
        return key;
    }

    private void onLicenseChange(LicenseDescriptor licenseDescriptor)
    {
        if(licenseDescriptor.getCloudSyncKey() != null)
        {
            key = licenseDescriptor.getCloudSyncKey();
        }
        else
        {
            key = null;
        }
        notifyCallbacks();
    }

    private void onLicenseFail()
    {
        key = null;
        notifyCallbacks();
    }

    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }

    public DescriptorService getDescriptorService()
    {
        return descriptorService;
    }
    
    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if(event instanceof InvalidLicenseEvent)
        {
            onLicenseFail();
        }
        else if(event instanceof ValidLicenseEvent)
        {
            ValidLicenseEvent vle = (ValidLicenseEvent)event;
            onLicenseChange(vle.getLicenseDescriptor());
        }        
    } 
}
