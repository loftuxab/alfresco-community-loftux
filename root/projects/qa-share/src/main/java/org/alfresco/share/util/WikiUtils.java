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
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.share.util.AbstractUtils.webDriverWait;

/**
 * @author Olga Antonik
 */
public class WikiUtils
{

    private static Log logger = LogFactory.getLog(ShareUserSharedFilesPage.class);

    /**
     * navigate to the Wiki Page. User must be logged in to the Share
     * 
     * @param driver -
     *            WebDrone Instance
     * @param siteName
     * @return WikiPage
     */
    public static WikiPage openWikiPage(WebDrone driver, String siteName)
    {
        WikiPage wikiPage = ShareUser.openSiteDashboard(driver, siteName).render().getSiteNav().selectWikiPage().render();
        logger.info("Opened Wiki page");

        return wikiPage;

    }

    /**
     * Method to create new Wiki Page. User must be logged in to the Share.
     * 
     * @param driver -
     *            WebDrone Instance
     * @param siteName
     * @param wikiTitle
     * @param text
     * @param tag
     * @return WikiPage
     */
    public static WikiPage createWikiPage(WebDrone driver, String siteName, String wikiTitle, String text, String tag)
    {
        SiteDashboardPage siteDashboardPage = ShareUser.openSiteDashboard(driver, siteName).render();
        WikiPage wikiPage = siteDashboardPage.getSiteNav().selectWikiPage().render();
        List<String> wikiText = new ArrayList<>();
        wikiText.add(text);
        List<String> wikiTag = new ArrayList<>();
        wikiTag.add(tag);
        return wikiPage.createWikiPage(wikiTitle, wikiText, wikiTag).render();
    }

    /**
     * Method to return tag name from Details Page of wiki. User must be logged in to the Share.
     * 
     * @param driver -
     *            WebDrone Instance
     * @param siteName
     * @param wikiTitle
     * @return String - tag name
     */
    public static String getWikiTag(WebDrone driver, String siteName, String wikiTitle)
    {
        WikiPage wikiPage = WikiUtils.openWikiPage(driver, siteName);
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPage = ShareUser.getCurrentPage(driver).render();
        wikiPage = wikiPageList.getWikiPageDirectoryInfo(wikiTitle.replace("_", " ")).clickDetails().render();
        webDriverWait(driver, 3000);
        return wikiPage.getTagName();
    }

}
