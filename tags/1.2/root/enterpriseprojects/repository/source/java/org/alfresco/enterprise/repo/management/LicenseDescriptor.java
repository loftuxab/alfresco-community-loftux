/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.Calendar;
import java.util.Date;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.admin.RepoUsage;
import org.alfresco.service.cmr.admin.RepoUsageStatus;
import org.alfresco.service.cmr.admin.RepoUsage.LicenseMode;

/**
 * An implementation of the {@link LicenseDescriptorMBean} interface that exposes information from a repository license
 * descriptor.
 * 
 * @author dward
 */
public class LicenseDescriptor implements LicenseDescriptorMBean
{
    /** A constant used in place of null dates to work around a bug in Hyperic. */
    private static final Date NULL_DATE;
    
    static
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        NULL_DATE = calendar.getTime();
    }

    /** The descriptor. */
    private final org.alfresco.service.descriptor.DescriptorService descriptorService;
    
    /** RepoAdminService to get current user and document counts */
    private org.alfresco.service.cmr.admin.RepoAdminService repoAdminService;

    /**
     * The Constructor.
     * 
     * @param descriptor
     *            the descriptor
     */
    public LicenseDescriptor(org.alfresco.service.descriptor.DescriptorService descriptorService, org.alfresco.service.cmr.admin.RepoAdminService repoAdminService)
    {
        this.descriptorService = descriptorService;
        this.repoAdminService = repoAdminService;
    }

    public int getDays()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return 0;
        }
        Integer retVal = descriptorService.getLicenseDescriptor().getDays();
        return retVal == null ? -1 : retVal.intValue();
    }

    public String getHolder()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return "";
        }
        return descriptorService.getLicenseDescriptor().getHolder().toString();
    }

    public Date getIssued()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return null;
        }
        return descriptorService.getLicenseDescriptor().getIssued();
    }

    public String getIssuer()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return "";
        }
        return descriptorService.getLicenseDescriptor().getIssuer().toString();
    }

    public int getRemainingDays()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return -1;
        }
        Integer retVal = descriptorService.getLicenseDescriptor().getRemainingDays();
        return retVal == null ? -1 : retVal.intValue();
    }

    public String getSubject()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return "";
        }
        return descriptorService.getLicenseDescriptor().getSubject();
    }

    public Date getValidUntil()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return NULL_DATE;
        }
        Date retVal = descriptorService.getLicenseDescriptor().getValidUntil();
        return retVal == null ? NULL_DATE : retVal;
    }

    public boolean isHeartBeatDisabled()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return false;
        }
        return descriptorService.getLicenseDescriptor().isHeartBeatDisabled();
    }

    @Override
    public Long getMaxDocs()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return 0L;
        }
        //return this.descriptor.getMaxDocs();
        return descriptorService.getLicenseDescriptor().getMaxDocs();
    }

    @Override
    public Long getMaxUsers()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return 0L;
        }
        // return this.descriptor.getMaxUsers();
        return descriptorService.getLicenseDescriptor().getMaxUsers();
    }

    @Override
    public String getLicenseMode()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return LicenseMode.UNKNOWN.toString();
        }
        return descriptorService.getLicenseDescriptor().getLicenseMode().toString();
    }
    
    @Override
    public boolean isCloudSyncKeyAvailable()
    {
        if(descriptorService.getLicenseDescriptor() == null)
        {
            return false;
        }
        // Of course we don't want to expose the real key to JMX!
        if(descriptorService.getLicenseDescriptor().getCloudSyncKey() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String loadLicense()
    {
        try
        {
            return descriptorService.loadLicense();
        }
        catch(Exception e)
        {
            throw new RuntimeException("Unable to load licence:" +  e.getMessage());
        }
    }

    @Override
    public Long getCurrentDocs()
    {
        // Runas system to obtain the info
        RunAsWork<Long> runAs = new RunAsWork<Long>()
        {
            @Override
            public Long doWork() throws Exception
            {
                RepoUsageStatus usageStatus = repoAdminService.getUsageStatus();
                RepoUsage usage = usageStatus.getUsage();
                
                Long documents = usage.getDocuments();
                return documents;
            }
        };
        return AuthenticationUtil.runAs(runAs, AuthenticationUtil.getSystemUserName());
    }

    @Override
    public Long getCurrentUsers()
    {
        // Runas system to obtain the info
        RunAsWork<Long> runAs = new RunAsWork<Long>()
        {
            @Override
            public Long doWork() throws Exception
            {
                RepoUsageStatus usageStatus = repoAdminService.getUsageStatus();
                RepoUsage usage = usageStatus.getUsage();
                
                Long users = usage.getUsers();
                return users;
            }
        };
        return AuthenticationUtil.runAs(runAs, AuthenticationUtil.getSystemUserName());
    }

	@Override
	public boolean isClusterEnabled() 
	{
	    if(descriptorService.getLicenseDescriptor() == null)
        {
            return false;
        }
        return descriptorService.getLicenseDescriptor().isClusterEnabled();
	}
	
	@Override
	public boolean isCryptodocEnabled() 
	{
	    if(descriptorService.getLicenseDescriptor() == null)
        {
            return false;
        }
        return descriptorService.getLicenseDescriptor().isCryptodocEnabled();
	}
}
