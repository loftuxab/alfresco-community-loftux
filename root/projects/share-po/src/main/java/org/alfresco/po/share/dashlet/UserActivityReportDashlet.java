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

package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


/**
 * 
 * Represents User Activity Report Dashlet
 * 
 * @author jcule
 *
 */
public class UserActivityReportDashlet extends AdhocAnalyzerDashlet
{

    private static Log logger = LogFactory.getLog(UserActivityReportDashlet.class);
    
    public UserActivityReportDashlet(WebDrone drone)
    {
        super(drone);
    }
    
    /**
     * Checks if Title is displayed in a dashlet header
     * 
     * @return
     */
    public String getTitle()
    {
        try
        {
            WebElement dashletTitle = drone.find(By.xpath("//div[contains(text(),'User Activity Report')]"));
            return dashletTitle.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Title in User Activity Report Dashlet header " + nse);
            throw new PageException("Unable to find title in User Activity Report Dashlet header.", nse);
        }
    }     

}
