/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.vti.handler.alfresco.v3;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class to work with sites through REST API
 * 
 * @author PavelYur
 */
public class ShareUtils
{

    private static Log logger = LogFactory.getLog(ShareUtils.class);

    // constants
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_REFERER = "Referer";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String UTF_8 = "UTF-8";

    // next parameters are configured in vti-handler-context.xml

    // host and port of Share application
    private String shareHostWithPort;

    // context of Share application
    private String shareContext;

    // default page of Share application
    private String shareWelcomeUri;

    // login uri of Share application
    private String shareLoginUri;

    // create site uri of Share application
    private String shareCreateSiteUri;

    // site dashboard uri
    private String shareSiteDashboardUri;

    // delete site uri of Share application
    private String shareDeleteSiteUri;

    public ShareUtils()
    {
    }

    /**
     * Set share host with port
     * 
     * @param shareHostWithPort the share host with ort to set
     */
    public void setShareHostWithPort(String shareHostWithPort)
    {
        this.shareHostWithPort = shareHostWithPort;
    }

    /**
     * Get share host with port
     * 
     * @return host where share application is running
     */
    public String getShareHostWithPort()
    {
        return shareHostWithPort;
    }

    /**
     * Set share context
     * 
     * @param shareContext shareContext to set
     */
    public void setShareContext(String shareContext)
    {
        this.shareContext = shareContext;
    }

    /**
     * Set share welcome page uri
     * 
     * @param shareWelcomeUri shareWelcomeUri to set
     */
    public void setShareWelcomeUri(String shareWelcomeUri)
    {
        this.shareWelcomeUri = shareWelcomeUri;
    }

    /**
     * Set share login page uri
     * 
     * @param shareLoginUri shareLoginUri to set
     */
    public void setShareLoginUri(String shareLoginUri)
    {
        this.shareLoginUri = shareLoginUri;
    }

    /**
     * Set share create site page uri
     * 
     * @param shareCreateSiteUri shareCreateSiteUri to set
     */
    public void setShareCreateSiteUri(String shareCreateSiteUri)
    {
        this.shareCreateSiteUri = shareCreateSiteUri;
    }

    /**
     * Set share site dashboard page uri
     * 
     * @param shareSiteDashboardUri shareSiteDashboardUri to set
     */
    public void setShareSiteDashboardUri(String shareSiteDashboardUri)
    {
        this.shareSiteDashboardUri = shareSiteDashboardUri;
    }

    /**
     * Set share delete site page uri
     * 
     * @param shareDeleteSiteUri shareDeleteSiteUri to set
     */
    public void setShareDeleteSiteUri(String shareDeleteSiteUri)
    {
        this.shareDeleteSiteUri = shareDeleteSiteUri;
    }

    /**
     * Create http method that is ready to sent to Share application
     * 
     * @param login login that used to login into share
     * @param password password that used to login into share
     * @return correct http method for login into Share application
     * @throws HttpException
     * @throws IOException
     */
    public PostMethod createLoginMethod(String login, String password) throws HttpException, IOException
    {
        PostMethod loginMethod = new PostMethod(shareHostWithPort + shareContext + shareLoginUri);

        loginMethod.setRequestHeader(HEADER_REFERER, shareHostWithPort + shareContext + shareWelcomeUri);

        loginMethod.setRequestHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED);

        // we assume that user have already login into vti server through MS Office and userName and password are valid
        NameValuePair[] parametres = new NameValuePair[] { new NameValuePair("username", login), new NameValuePair("password", password),
                new NameValuePair("success", "/share/page/site-index"), new NameValuePair("failure", "/share/page?f=default&pt=login&error=true") };

        loginMethod.setRequestBody(parametres);
        return loginMethod;
    }

    /**
     * Create site dashboard
     * 
     * @param siteShortName site name
     * @return http method to site dashboard
     */

    private GetMethod createSiteDashboardMethod(String siteShortName)
    {
        GetMethod siteDashboardMethod = new GetMethod(shareHostWithPort + shareContext + shareSiteDashboardUri.replace("...", siteShortName));

        return siteDashboardMethod;
    }

    /**
     * Creates new site using REST API, http method is sent to appropriate web script
     * 
     * @param login login that used to login into share
     * @param password password that used to login into share
     * @param sitePreset sitePreset for new site
     * @param shortName shortName for new site
     * @param title title for new site
     * @param description description for new site
     * @param isPublic is new site public?
     * @throws HttpException
     * @throws IOException
     */
    public void createSite(String login, String password, String sitePreset, String shortName, String title, String description, boolean isPublic) throws HttpException,
            IOException
    {
        HttpClient httpClient = new HttpClient();
        PostMethod createSiteMethod = new PostMethod(shareHostWithPort + shareContext + shareCreateSiteUri);

        createSiteMethod.setRequestHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        // generate valid request body
        String createSiteBody = "{\"isPublic\":\"" + isPublic + "\",\"title\":\"" + title + "\",\"shortName\":\"" + shortName + "\"," + "\"description\":\"" + description
                + "\",\"sitePreset\":\"" + sitePreset + "\"" + (isPublic ? ",\"alfresco-createSite-instance-isPublic-checkbox\":\"on\"}" : "}");
        createSiteMethod.setRequestEntity(new StringRequestEntity(createSiteBody, CONTENT_TYPE_TEXT_PLAIN, UTF_8));
        PostMethod loginMethod = createLoginMethod(login, password);
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to login into Share. URL: " + loginMethod.getURI());

            int loginStatus = httpClient.executeMethod(loginMethod);
            loginMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Login method returned status: " + loginStatus);
        }
        catch (Exception e)
        {
            loginMethod.releaseConnection();
            if (logger.isDebugEnabled())
                logger.debug("Login into share failed. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to create Site with name: " + shortName + ". URL: " + createSiteMethod.getURI());

            int createSiteStatus = httpClient.executeMethod(createSiteMethod);
            createSiteMethod.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Create method returned status: " + createSiteStatus);
        }
        catch (Exception e)
        {
            createSiteMethod.releaseConnection();
            if (logger.isDebugEnabled())
                logger.debug("Fail to create the Site with name: " + shortName + ". Message: " + e.getMessage());
            throw new RuntimeException(e);
        }

        GetMethod dashboard = createSiteDashboardMethod(shortName);
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to initialize dashboard for Site with name: " + shortName + ". URL: " + dashboard.getURI());

            int dashboardStatus = httpClient.executeMethod(dashboard);
            dashboard.getResponseBody();

            if (logger.isDebugEnabled())
                logger.debug("Dashboard initialyzing finished with status: " + dashboardStatus);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Dashboard initialyzing failed. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            dashboard.releaseConnection();
        }
    }

    /**
     * Deletes site using REST API, http method is sent to appropriate web script
     * 
     * @param login login that used to login into share
     * @param password password that used to login into share
     * @param shortName shortName of site we are going to delete
     * @throws HttpException
     * @throws IOException
     */
    public void deleteSite(String login, String password, String shortName) throws HttpException, IOException
    {
        HttpClient httpClient = new HttpClient();
        PostMethod deleteSiteMethod = new PostMethod(shareHostWithPort + shareContext + shareDeleteSiteUri);

        deleteSiteMethod.setRequestHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
        // generate valid request body
        String deleteSiteBody = "{\"shortName\":\"" + shortName + "\"}";
        deleteSiteMethod.setRequestEntity(new StringRequestEntity(deleteSiteBody, CONTENT_TYPE_TEXT_PLAIN, UTF_8));
        PostMethod loginMethod = createLoginMethod(login, password);
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to login into Share. URL: " + loginMethod.getURI());

            int loginStatus = httpClient.executeMethod(loginMethod);

            if (logger.isDebugEnabled())
                logger.debug("Login method returned status: " + loginStatus);
        }
        catch (Exception e)
        {
            loginMethod.releaseConnection();
            if (logger.isDebugEnabled())
                logger.debug("Login into share failed. Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Trying to delete Site with name: " + shortName + ". URL: " + deleteSiteMethod.getURI());

            int deleteSiteStatus = httpClient.executeMethod(deleteSiteMethod);

            if (logger.isDebugEnabled())
                logger.debug("Delete method returned status: " + deleteSiteStatus);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.debug("Fail to delete the Site with name: " + shortName + ". Message: " + e.getMessage());
            throw new RuntimeException(e);
        }
        finally
        {
            deleteSiteMethod.releaseConnection();
        }
    }

    /**
     * Get share context name
     * 
     * @return String share context name
     */
    public String getShareContext()
    {
        return shareContext;
    }

}
