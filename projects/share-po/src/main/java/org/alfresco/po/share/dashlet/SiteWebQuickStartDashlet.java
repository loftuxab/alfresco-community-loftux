package org.alfresco.po.share.dashlet;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.Select;

public class SiteWebQuickStartDashlet extends AbstractDashlet implements Dashlet
{
    private Log logger = LogFactory.getLog(SiteActivitiesDashlet.class);
    
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div[class='dashlet']");
    private static final By LOAD_DATA_SELECTOR= By.cssSelector("select[id$='load-data-options']");
    private static final By IMPORT_BUTTON=By.cssSelector("button[id$='default-load-data-link']");
    private static final By WQS_HELP_LINK=By.cssSelector("div.detail-list-item.last-item>a");

    /**
     * Constructor.
     */
    protected SiteWebQuickStartDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
    }

    @SuppressWarnings("unchecked")
    public SiteWebQuickStartDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteWebQuickStartDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteWebQuickStartDashlet render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(DASHLET_CONTAINER_PLACEHOLDER),
                getVisibleRenderElement(DASHLET_TITLE));
        return this;
    }
    
    /**
     * 
     */
    public void selectWebsiteDataOption(WebQuickStartOptions option)
    {
        if (option == null)
        {
            throw new UnsupportedOperationException("An option value is required");
        }
        try
        {
            Select dataLoadDropDown=new Select(drone.findAndWait(LOAD_DATA_SELECTOR));
            dataLoadDropDown.selectByVisibleText(option.getDescription());
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and select the Website data dropdown.", e);
            }
        }
    }
    
    /**
     * 
     */
    public void clickImportButtton()
    {
        try
        {
            drone.findAndWait(IMPORT_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Import Button.", e);
            }
        }
    }
    
    /**
     * Get the selected option applied for site wqs dashlet.
     * 
     * @return 
     */
    public String getSelectedWebsiteData()
    {
        try
        {
            Select websiteDataDropdown=new Select(drone.find(LOAD_DATA_SELECTOR));
            return websiteDataDropdown.getFirstSelectedOption().getText();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate filter elements from the dropdown", e);
        }
    }
    
    public boolean isWQSHelpLinkDisplayed()
    {
        try
        {
            return drone.find(WQS_HELP_LINK).isDisplayed();
        }
        catch (Exception e)
        {
            return false;
        }
    }

}
