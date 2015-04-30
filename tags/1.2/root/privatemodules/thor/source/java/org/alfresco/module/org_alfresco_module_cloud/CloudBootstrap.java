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
package org.alfresco.module.org_alfresco_module_cloud;

import org.alfresco.repo.domain.qname.QNameDAO;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

public class CloudBootstrap extends AbstractLifecycleBean
{
    private QNameDAO qnameDAO;
    
    public void setQNameDAO(QNameDAO qnameDAO)
    {
        this.qnameDAO = qnameDAO;
    }
    
    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        qnameDAO.getOrCreateNamespace(CloudModel.CLOUD_MODEL_1_0_URI);

        qnameDAO.getOrCreateQName(CloudModel.ASPECT_ACCOUNTS);
        qnameDAO.getOrCreateQName(CloudModel.ASPECT_EXTERNAL_PERSON);
        qnameDAO.getOrCreateQName(CloudModel.ASPECT_NETWORK_ADMIN);

        qnameDAO.getOrCreateQName(CloudModel.PROP_HOME_ACCOUNT);
        qnameDAO.getOrCreateQName(CloudModel.PROP_SECONDARY_ACCOUNTS);
        qnameDAO.getOrCreateQName(CloudModel.PROP_DEFAULT_ACCOUNT);
        qnameDAO.getOrCreateQName(CloudModel.PROP_EXTERNAL_PERSON);
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
    }

}
