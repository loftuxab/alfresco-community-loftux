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

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.thirdparty.wordpress.WordPressMainPage;
import org.alfresco.po.thirdparty.wordpress.WordPressSignInPage;
import org.alfresco.po.thirdparty.wordpress.WordPressUserPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Util class to manager Blog Publishing
 *
 * @author Marina.Nenadovets
 *
 */
public class BlogUtil extends AbstractUtils
{
    private static final String POST_TITLE_PREVIEW = "//div[@class='nodeTitle']/a";


    /**
     * Method to verify whether post is published to WordPress
     *
     * @param drone
     * @param postTitle
     * @param blogUrl
     * @param userName
     * @param userPassword
     * @return boolean
     */
    public static boolean isPostPublishedToExternalWordpressBlog(WebDrone drone, String postTitle, String blogUrl, String userName, String userPassword)
    {
        String currentUrl = drone.getCurrentUrl();
        try
        {
            //Navigate to Sign In page
            drone.navigateTo(String.format("https://%s.wordpress.com", blogUrl));
            WordPressUserPage wordPressUserPage = new WordPressUserPage(drone);
            if(drone.getTitle().equalsIgnoreCase("WordPress.com - Get a Free Website and Blog Here"))
            {
                WordPressMainPage wordPressMainPage = new WordPressMainPage(drone).render();
                WordPressSignInPage wordPressSignInPage = wordPressMainPage.clickLogIn().render();
                wordPressUserPage = wordPressSignInPage.login(userName, userPassword);
                drone.navigateTo(String.format("https://%s.wordpress.com", userName));
            }

            return wordPressUserPage.isPostPresent(postTitle);
        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
        finally
        {
            //Navigate to previous page
            drone.navigateTo(currentUrl);
            drone.waitForPageLoad(5);
        }
    }

    /**
     * Method to verify whether post is removed from WordPress
     * @param drone
     * @param postTitle
     * @param blogUrl
     * @param userName
     * @param userPassword
     * @return boolean
     */
    public static boolean isPostRemovedFromExternalWordpressBlog(WebDrone drone, String postTitle, String blogUrl, String userName, String userPassword)
    {
        String currentUrl = drone.getCurrentUrl();
        try
        {
            //Navigate to Sign In page
            drone.navigateTo(String.format("https://%s.wordpress.com", blogUrl));
            WordPressUserPage wordPressUserPage = new WordPressUserPage(drone);
            if(drone.getTitle().equalsIgnoreCase("WordPress.com - Get a Free Website and Blog Here"))
            {
                WordPressMainPage wordPressMainPage = new WordPressMainPage(drone).render();
                WordPressSignInPage wordPressSignInPage = wordPressMainPage.clickLogIn().render();
                wordPressUserPage = wordPressSignInPage.login(userName, userPassword);
            }
            return wordPressUserPage.isPostRemoved(postTitle);

        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
        finally
        {
            //Navigate to previous page.
            drone.navigateTo(currentUrl);
        }
    }

    /**
     * Method to get postId
     * @param drone
     * @param siteName
     * @param postName
     * @return String
     */

    public static String getPostId (WebDrone drone, String siteName, String postName)
    {
        try
        {

            SiteDashboardPage site = ShareUser.openSiteDashboard(drone, siteName).render();
            BlogPage blogPage = site.getSiteNav().selectBlogPage().render();
            blogPage.openBlogPost(postName);
            WebElement element = drone.findAndWait(By.xpath(POST_TITLE_PREVIEW));
            String postId = element.getAttribute("href").split("\\&")[0];
            postId = postId.split("[?=]+")[2];
            return postId;
        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException("Element not found", ex);
        }
    }

}
