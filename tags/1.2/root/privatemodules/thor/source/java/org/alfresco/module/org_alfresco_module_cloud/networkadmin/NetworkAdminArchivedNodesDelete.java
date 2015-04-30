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
package org.alfresco.module.org_alfresco_module_cloud.networkadmin;

import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
import org.alfresco.repo.web.scripts.archive.ArchivedNodesDelete;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the deletednodes.get web script.
 */
public class NetworkAdminArchivedNodesDelete extends ArchivedNodesDelete
{
    private NetworkAdmin networkAdmin;
    
    public void setNetworkAdmin(NetworkAdmin service)
    {
        this.networkAdmin = service;
    }
    
    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
    {
        return networkAdmin.runAs(new NetworkAdminRunAsWork<Map<String, Object>>()
        {
            public Map<String, Object> doWork() throws Exception
            {
                return NetworkAdminArchivedNodesDelete.super.executeImpl(req, status, cache);
            }
        });
    }
}

