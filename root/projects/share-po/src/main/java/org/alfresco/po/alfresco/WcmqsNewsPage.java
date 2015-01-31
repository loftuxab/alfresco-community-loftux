package org.alfresco.po.alfresco;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The news page that is opened for a folder (Global Economy, Companies, Markets .... )
 * 
 * @author bogdan.bocancea
 */

public class WcmqsNewsPage extends SharePage
{

    protected static String TITLES_NEWS = "//div[@id='left']//div[@class='interior-content']//a//.././/./.././a";

    @RenderWebElement
    private final By PAGE_LOGO = By.cssSelector("#logo>a");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public WcmqsNewsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to get the headline titles from news Page
     * 
     * @return List<ShareLink>
     */
    public List<ShareLink> getHeadlineTitleNews()
    {
        List<ShareLink> titles = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = drone.findAll(By.xpath(TITLES_NEWS));
            for (WebElement div : links)
            {
                titles.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return titles;
    }

    /**
     * Method to get the date and time for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return String news Date and Time
     */
    public String getDateTimeNews(String newsName)
    {
        try
        {
            return drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]//.././/./.././span[@class='newslist-date']", newsName))).getText();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find news links. " + e.toString());
        }

    }

    /**
     * Method to get the date and time for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return String news Date and Time
     */
    public boolean isDateTimeNewsPresent(String newsName)
    {
        boolean present = false;

        try
        {
            present = drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]//.././/./.././span[@class='newslist-date']", newsName)))
                    .isDisplayed();
        }
        catch (TimeoutException e)
        {

        }

        return present;

    }

    /**
     * Method to get the description for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return String news description
     */
    public String getNewsDescrition(String newsName)
    {
        try
        {
            return drone.findAndWait(By.xpath(String.format("//h4/a[contains(@href,'%s')]//.././/./.././p", newsName))).getText();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find news links. " + e.toString());
        }

    }

    /**
     * Method title the title for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return
     */
    public String getNewsTitle(String newsName)
    {
        try
        {
            return drone.findAndWait(By.xpath(String.format("//h4/a[contains(@href,'%s')]", newsName))).getText();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find the title:" + e.toString());
        }

    }

    /**
     * Method to click a news title
     * 
     * @param newsName - the of the news declared in share!
     * @return
     */
    public void clickNewsTitle(String newsName)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]", newsName))).click();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find news links. " + e.toString());
        }

    }

}
