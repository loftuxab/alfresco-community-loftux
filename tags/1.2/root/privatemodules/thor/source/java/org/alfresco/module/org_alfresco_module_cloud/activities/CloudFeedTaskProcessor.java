/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.activities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.activities.feed.local.LocalFeedTaskProcessor;

/**
 * Extend/override Activities Feed Task Processor (within Activities subsystem - see custom-activities-feed-context.xml)
 * 
 * @author janv
 * @since Alfresco Cloud Module (Thor)
 */
public class CloudFeedTaskProcessor extends LocalFeedTaskProcessor
{
    // THOR-1060 - override here for now - pending a more 'generic' (no pun intended) fix and/or config option
    @Override
    protected Map<String, List<String>> getActivityTypeTemplates(String repoEndPoint, String ticket, String subPath) throws Exception
    {
        List<String> allTemplateNames = Arrays.asList(new String[]{"alfresco/templates/activities/org/alfresco/generic.json.ftl"});
        return getActivityTemplates(allTemplateNames);
    }
}
