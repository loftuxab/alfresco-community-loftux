/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.analytics.scripts;

import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.site.SiteVisibility;

public class ScriptAnalyticsService extends BaseScopableProcessorExtension
{
    /**
     * Is the analytics service currently enabled or not
     *
     * @return  True - enabled, False - disabled
     */
    public boolean isEnabled() {
    	return Analytics.isEnabled();
    }
    
    /**
     * Record an uploaded document event
     * 
     * @param mimetype
     *            The type of document
     * @param size
     *            The size of the document in Bytes
     * @param isEdit
     *            True - this is a file update, False - This is a file creation
     */
    public static void record_UploadDocument(String mimeType, long size,
            boolean isEdit)
    {
        Analytics.record_UploadDocument(mimeType, size, isEdit);
    }
    
    /**
     * Record a site creation event
     * 
     * @param siteTemplate The template used for the site.
     * @param visibility the site visibility. This must exactly match one of the enum values in {@link SiteVisibility}.
     * @throws IllegalArgumentException if the visibility parameter does not match one of the {@link SiteVisibility} values.
     */
    public static void record_CreateSite(String siteTemplate, String visibility)
    {
        SiteVisibility enumValue = SiteVisibility.valueOf(visibility);
        Analytics.record_createSite(siteTemplate, enumValue);
    }
}
