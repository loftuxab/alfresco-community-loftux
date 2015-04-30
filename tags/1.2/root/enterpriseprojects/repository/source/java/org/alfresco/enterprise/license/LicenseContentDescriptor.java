/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.license;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.admin.RepoUsage.LicenseMode;
import org.alfresco.service.license.LicenseDescriptor;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import de.schlichtherle.license.LicenseContent;


/**
 * License Descriptor
 * 
 * @author davidc
 */
public class LicenseContentDescriptor implements LicenseDescriptor
{
    private LicenseContent licenseContent = null;

    /**
     * Construct
     * 
     * @param licenseContent
     */
    public LicenseContentDescriptor(LicenseContent licenseContent)
    {
        this.licenseContent = licenseContent;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getIssued()
     */
    public Date getIssued()
    {
        return licenseContent.getIssued();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getValidUntil()
     */
    public Date getValidUntil()
    {
        return licenseContent.getNotAfter();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getSubject()
     */
    public String getSubject()
    {
        return licenseContent.getSubject();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getHolder()
     */
    public Principal getHolder()
    {
        return licenseContent.getHolder();
    }

    /**
     * Get Organisation from holder
     *
     * @return  organisation
     * @since 4.2
     */
    public String getHolderOrganisation()
    {
        Principal holderPrincipal = getHolder();

        String holder = null;
        if (holderPrincipal != null)
        {
            holder = holderPrincipal.getName();
            String[] properties = holder.split(",");
            for (String property : properties)
            {
                String[] parts = property.split("=");
                if (parts[0].equals("O"))
                {
                    holder = parts[1];
                }
            }
        }

        return holder;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getIssuer()
     */
    public Principal getIssuer()
    {
        return licenseContent.getIssuer();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getDays()
     */
    public Integer getDays()
    {
        Integer days = null;
        Date validUntil = getValidUntil();
        if (validUntil != null)
        {
            Date issued = getIssued();
            days = new Integer(calcDays(issued, validUntil));
        }
        return days;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.license.LicenseDescriptor#getRemainingDays()
     */
    public Integer getRemainingDays()
    {
        Integer days = null;
        Date validUntil = getValidUntil();
        if (validUntil != null)
        {
            Date now = new Date();
            days = new Integer(calcDays(now, validUntil));
        }
        return days;
    }

    /**
     * Calculate number of days between start and end date
     * 
     * @param start  start date
     * @param end  end date
     * @return  number days between
     */
    private int calcDays(Date start, Date end)
    {
        DateMidnight startMidnight = new DateMidnight(start);
        DateMidnight endMidnight = new DateMidnight(end);
        
        int days;
        if (endMidnight.isBefore(startMidnight))
        {
            Interval interval = new Interval(endMidnight, startMidnight);
            Period period = interval.toPeriod(PeriodType.days());
            days = 0 - period.getDays();
        }
        else
        {
            Interval interval = new Interval(startMidnight, endMidnight);
            Period period = interval.toPeriod(PeriodType.days());
            days = period.getDays();
        }
        return days;
    }

    @SuppressWarnings("unchecked")
    public boolean isHeartBeatDisabled()
    {
        Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra == null)
        {
            return false;
        }
        Boolean b = (Boolean) extra.get("disableHeartBeat");
        return b != null && b.booleanValue();
    }

    @SuppressWarnings("unchecked")
    public String getHeartBeatUrl()
    {
        Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra == null)
        {
            return null;
        }
        return (String) extra.get("heartBeatUrl");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getMaxDocs()
    {
        Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra != null)
        {
            Object o = extra.get("maxDocs");
        
            if (o != null)
            {
                if(o instanceof Long)
                {
                    return (Long)o;
                }
                if(o instanceof Integer)
                {
                    return new Long((Integer)o);
                }
            }
        }
        return null;

    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getMaxUsers()
    {
        Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra != null)
        {
            Object o = extra.get("maxUsers");
        
            if (o != null)
            {
                if(o instanceof Long)
                {
                    return (Long)o;
                }
                if(o instanceof Integer)
                {
                    return new Long((Integer)o);
                }
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LicenseMode getLicenseMode()
    {
        Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra != null)
        {
            String s = (String)extra.get("licenseMode");
        
            if (s != null)
            {
                return LicenseMode.valueOf(s);
            }
        }
        
        return LicenseMode.UNKNOWN;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String getCloudSyncKey()
    {
        Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra != null)
        {
            String s = (String)extra.get("sync.cloudKey");
        
            if (s != null)
            {
                return s;
            }
            
        }
        
        return null;
    }

	@Override
    @SuppressWarnings("unchecked")
	public boolean isClusterEnabled() 
	{
		Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra != null)
        {
            Boolean b = (Boolean) extra.get("clusterEnabled");
            return b != null && b.booleanValue(); 
        }

        return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getTransformationServerExpiryDate() {
		  Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
	      if (extra != null)
	      {
	          String s = (String)extra.get("transformationServer.ExpiryDate");
	          return s;
	      }
	      return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isCryptodocEnabled() 
	{
		Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
        if (extra != null)
        {
            Boolean b = (Boolean) extra.get("cryptodocEnabled");
            return b != null && b.booleanValue(); 
        }

        return false;
	}

	@Override
	public Map<String, Object> getExtras() 
	{
		Map<String, Object> extra = (Map<String, Object>) licenseContent.getExtra();
		
		Map<String, Object> ret = new HashMap<String, Object>(extra);
		ret.remove("sync.cloudKey");   // private value
		
		return ret;
	}
	
	
}
