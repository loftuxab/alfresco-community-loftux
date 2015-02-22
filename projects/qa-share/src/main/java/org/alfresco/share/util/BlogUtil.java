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
