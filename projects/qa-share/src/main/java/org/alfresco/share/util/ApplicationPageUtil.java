/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains the utils for Application page of Admin Console
 *
 * @author Antonik Olga
 */
public class ApplicationPageUtil {

    private static final Log logger = LogFactory.getLog(ApplicationPageUtil.class);

    /**
     * Open Application page from any share page. Admin user must be logged in
     *
     *
     * @param drone
     * @return
     */
    public static AdminConsolePage openApplicationPage(WebDrone drone)
    {
        try
        {
            SharePage page = drone.getCurrentPage().render();
            return page.getNav().getAdminConsolePage();
        }
        catch (PageRenderTimeException e)
        {
            throw new PageOperationException("Application page does not render in time", e);
        }
    }


}
