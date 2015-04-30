/*
 * Copyright 2014-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.cryptodoc.impl;

import org.alfresco.enterprise.license.InvalidLicenseEvent;
import org.alfresco.enterprise.license.ValidLicenseEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.extensions.surf.util.I18NUtil;

public class CryptodocLicenseImpl implements CryptodocLicense,
    ApplicationListener<ApplicationEvent>
{
    private static Log logger = LogFactory.getLog(CryptodocLicenseImpl.class);

    private enum CryptodocLicenseStatus
    {
        UNKNOWN, ENABLED, DISABLED
    }

    CryptodocLicenseStatus licenseStatus = CryptodocLicenseStatus.UNKNOWN;

    @Override
    public boolean isCryptodocLicenseValid()
    {
        if (licenseStatus == CryptodocLicenseStatus.ENABLED)
        {
            return true;
        }
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("event:" + event);
        }

        if (event instanceof InvalidLicenseEvent)
        {
            licenseStatus = CryptodocLicenseStatus.DISABLED;
        }
        else if (event instanceof ValidLicenseEvent)
        {
            ValidLicenseEvent vle = (ValidLicenseEvent) event;
            boolean isCryptodocLicenseEnabled = vle.getLicenseDescriptor()
                    .isCryptodocEnabled();

            licenseStatus = isCryptodocLicenseEnabled ? CryptodocLicenseStatus.ENABLED
                    : CryptodocLicenseStatus.DISABLED;

            if (logger.isDebugEnabled())
            {
                logger.debug("valid license event" + isCryptodocLicenseEnabled);
            }
        }
    }
}
