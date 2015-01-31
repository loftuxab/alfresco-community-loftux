package org.alfresco.po.share.wqs;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

public abstract class WcmqsAbstractPage extends SharePage
{
    private static Log logger = LogFactory.getLog(WcmqsAbstractPage.class);
    protected final By PAGE_MENU = By.cssSelector("div[id='myslidemenu']");
    protected final By CONTACT_MENU = By.cssSelector("div.link-menu");
    protected final By ALFRESCO_LOGO = By.cssSelector("div[id='logo']");
    protected final By SEARCH_FIELD = By.cssSelector("input[id='search-phrase']");
    protected final By SEARCH_BUTTON = By.cssSelector("input.input-arrow");

    public WcmqsAbstractPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractPage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(PAGE_MENU), getVisibleRenderElement(CONTACT_MENU));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to verify the contact link exists
     * 
     * @return
     */
    public boolean isContactLinkDisplay()
    {
        try
        {
            return drone.findAndWait(CONTACT_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the page menu exists
     * 
     * @return
     */
    public boolean isPageMenuDisplay()
    {
        try
        {
            return drone.findAndWait(PAGE_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the Alfresco logo exists
     * 
     * @return
     */
    public boolean isAlfrescoLogoDisplay()
    {
        try
        {
            return drone.findAndWait(ALFRESCO_LOGO).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the search field with search button exists
     * 
     * @return
     */
    public boolean isSearchFieldWithButtonDisplay()
    {
        try
        {
            return drone.findAndWait(SEARCH_FIELD).isDisplayed() && drone.findAndWait(SEARCH_BUTTON).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

}
