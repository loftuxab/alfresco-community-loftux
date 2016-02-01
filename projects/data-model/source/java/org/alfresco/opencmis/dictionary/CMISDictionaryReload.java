/*
 * Copyright (C) 2005-2016 Alfresco Software Limited.
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
package org.alfresco.opencmis.dictionary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Temporary workaround for:
 * <ul>
 *     <li>ACE-5041: CLONE - google docs content cannot be accessed via cmis</li>
 * </ul>
 * 
 * TODO: Remove this bean when rework for MNT-14819 is complete.
 * 
 * @author Matt Ward
 */
public final class CMISDictionaryReload extends AbstractLifecycleBean
{
    private static final Log log = LogFactory.getLog(CMISDictionaryReload.class);
    private final CMISAbstractDictionaryService cmisDictService;
    
    public CMISDictionaryReload(CMISAbstractDictionaryService cmisDictService)
    {
        this.cmisDictService = cmisDictService;
    }
    
    public void reload()
    {
        log.debug("Reloading CMIS dictionary.");
        cmisDictService.afterDictionaryInit();
    }

    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        reload();
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // Do nothing.
    }
}
