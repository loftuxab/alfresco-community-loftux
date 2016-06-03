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

import org.alfresco.po.thirdparty.flickr.FlickrUserPage;
import org.alfresco.po.thirdparty.flickr.YahooSignInPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.NoSuchElementException;

/**
 * @author Aliaksei.Boole
 */
public class PublishUtil
{

    /**
     * Method checks the published picture in Flickr profile exist. Login in Yahoo if necessary.
     *
     * @param drone
     * @param fileName
     * @param userName
     * @param userPassword
     * @return
     * @throws InterruptedException
     */
    public static boolean isContentUploadedToFlickrChannel(WebDrone drone, String fileName, String userName, String userPassword) throws InterruptedException
    {
        String detailsPageUrl = drone.getCurrentUrl();
        try
        {
            // navigate to sign in page
            drone.navigateTo("http://www.flickr.com/signin/");
            if (drone.getTitle().endsWith("Yahoo"))
            {
                YahooSignInPage yahooSignInPage = new YahooSignInPage(drone).render();
                yahooSignInPage.login(userName, userPassword);
            }
            FlickrUserPage flickrUserPage = new FlickrUserPage(drone).render();
            return flickrUserPage.isFileUpload(fileName);

        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
        finally
        {
            //Navigate to previous page.
            drone.navigateTo(detailsPageUrl);
        }
    }

}
