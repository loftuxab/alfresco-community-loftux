 /*
     * Copyright (C) 2005-2015 Alfresco Software Limited.
     * This file is part of Alfresco
     * Alfresco is free software: you can redistribute it and/or modify
     * it under the terms of the GNU Lesser General Public License as published by
     * the Free Software Foundation, either version 3 of the License, or
     * (at your option) any later version.
     * Alfresco is distributed in the hope that it will be useful,
     * but WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
     * GNU Lesser General Public License for more details.
     * You should have received a copy of the GNU Lesser General Public License
     * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
  */


package org.alfreso.po.share.steps;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

public class CommonActions
{
    public static long refreshDuration = 25000;
    /**
     * Checks if the current page is share page, throws PageException if not.
     *
     * @param drone WebDrone Instance
     * @return SharePage
     * @throws PageException if the current page is not a share page
     */
    public static SharePage getSharePage(WebDrone drone)
    {
        checkIfdroneNull(drone);
        try
        {
            HtmlPage generalPage = drone.getCurrentPage().render(refreshDuration);
            return (SharePage) generalPage;
        }
        catch (PageException pe)
        {
            throw new PageException("Can not cast to SharePage: Current URL: " + drone.getCurrentUrl());
        }
    }

    /**
     * Checks if drone is null, throws UnsupportedOperationException if so.
     *
     * @param drone WebDrone Instance
     * @throws UnsupportedOperationException if drone is null
     */
    public static void checkIfdroneNull(WebDrone drone)
    {
        if (drone == null)
        {
            throw new UnsupportedOperationException("WebDrone is required");
        }
    }
}
