package org.alfresco.po.share.steps;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.search.SearchKeys;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

public class AbstractSteps
{
    // Constants
    protected static final String SITE_VISIBILITY_PUBLIC = "public";
    protected static final String SITE_VISIBILITY_PRIVATE = "private";
    protected static final String SITE_VISIBILITY_MODERATED = "moderated";
    protected static final String DOCLIB = "DocumentLibrary";
    protected static final String REPO = "Repository";
    protected static final String SITES = "Sites";
    protected static final String DOCLIB_CONTAINER = "documentLibrary";
    protected static final String MY_DASHBOARD = " Dashboard";
    protected final static String SERACH_ZERO_CONTENT = "ZERO_RESULTS";
    protected final static String NONE = "(None)";
    protected final static String adminGroup = "ALFRESCO_ADMINISTRATORS";
    protected static final String BASIC_SEARCH = SearchKeys.BASIC_SEARCH.getSearchKeys();
    protected static final String ADV_FOLDER_SEARCH = SearchKeys.FOLDERS.getSearchKeys();
    protected static final String ADV_CONTENT_SEARCH = SearchKeys.CONTENT.getSearchKeys();
    protected static final String ADV_CRM_SEARCH = SearchKeys.CRM_SEARCH.getSearchKeys();
    
    /**
     * Checks if driver is null, throws UnsupportedOperationException if so.
     * 
     * @param driver WebDrone Instance
     * @throws UnsupportedOperationException if driver is null
     */
    protected static void checkIfDriverNull(WebDrone driver)
    {
        if (driver == null)
        {
            throw new UnsupportedOperationException("WebDrone is required");
        }
    }
    
    /**
     * Checks if the current page is share page, throws PageException if not.
     * 
     * @param driver WebDrone Instance
     * @return SharePage
     * @throws PageException if the current page is not a share page
     */
    public static SharePage getSharePage(WebDrone driver)
    {
        checkIfDriverNull(driver);
        try
        {
            HtmlPage generalPage = driver.getCurrentPage().render();
            return (SharePage) generalPage;
        }
        catch (PageException pe)
        {
            throw new PageException("Can not cast to SharePage: Current URL: " + driver.getCurrentUrl());
        }
    }
}
