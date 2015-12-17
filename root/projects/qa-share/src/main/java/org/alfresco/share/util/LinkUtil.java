/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.util;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Created by olga.lokhach
 */
public class LinkUtil extends AbstractUtils
{

    private static final String LINK_TITLE_PREVIEW = "//div[@class='nodeTitle']/a";

    /**
     * Method to get LinkId
     * @param drone
     * @param siteName
     * @param linkTitle
     * @return String
     */

    public static String getLinkId (WebDrone drone, String siteName, String linkTitle)
    {
        try
        {

            SiteDashboardPage site = ShareUser.openSiteDashboard(drone, siteName).render();
            LinksPage linksPage = site.getSiteNav().selectLinksPage();
            linksPage.clickLink(linkTitle);
            WebElement element = drone.findAndWait(By.xpath(LINK_TITLE_PREVIEW));
            String linkId = element.getAttribute("href").split("[?=]+")[2];
            return linkId;
        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
    }
}
