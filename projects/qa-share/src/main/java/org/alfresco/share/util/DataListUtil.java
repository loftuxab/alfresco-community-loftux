
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
