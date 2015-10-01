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
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Created by olga.lokhach
 */
public class DataListUtil extends AbstractUtils
{

    /**
     * Method to get dataListId
     * @param drone
     * @param siteName
     * @param listTitle
     * @return String
     */

    public static String getListId (WebDrone drone, String siteName, String listTitle)
    {
        try
        {

            SiteDashboardPage site = ShareUser.openSiteDashboard(drone, siteName).render();
            DataListPage dataListPage =  site.getSiteNav().selectDataListPage().render();
            dataListPage.selectDataList(listTitle);
            WebElement element = drone.findAndWait(By.xpath(String.format("//div[contains(@id,'default-lists')]//a[text()='%s']", listTitle)));
            String listId = element.getAttribute("href").split("[?=]+")[2];
            return listId;
        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
    }

}
