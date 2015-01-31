package org.alfresco.po.share.wqs;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * The news page that is opened for a folder (Global Economy, Companies, Markets .... )
 * 
 * @author bogdan.bocancea
 */

public class WcmqsNewsPage extends SharePage
{

    public static final String FTSE_1000 = "FTSE 100 rallies from seven-week low";
    public static final String GLOBAL_CAR_INDUSTRY = "Global car industry";
    public static final String FRESH_FLIGHT_TO_SWISS = "Fresh flight to Swiss franc as Europe's bond strains return";
    public static final String HOUSE_PRICES = "House prices face rollercoaster ride";
    public static final String EUROPE_DEPT_CONCERNS = "Europe dept concerns ease but bank fears remain";
    public static final String MEDIA_CONSULT = "Media Consult new site coming out in September";
    public static final String CHINA_EYES = "China eyes shake-up of bank holding";
    public static final String MINICARDS_AVAILABLE = "Minicards are now available";
    public static final String INVESTORS_FEAR = "Investors fear rising risk of US regional defaults";
    public static final String OUR_NEW_BROCHURE = "Our new brochure is now available";

    public static final String NEWS = "news";
    public static final String GLOBAL = "global";
    public static final String COMPANIES = "companies";
    public static final String MARKETS = "markets";

    private final String ARTICLE_4 = "article4.html";
    private final String ARTICLE_3 = "article3.html";
    private final String ARTICLE_2 = "article2.html";
    private final String ARTICLE_1 = "article1.html";
    private final String ARTICLE_6 = "article6.html";
    private final String ARTICLE_5 = "article5.html";

    protected static String TITLES_NEWS = "//div[@id='left']//div[@class='interior-content']//a//.././/./.././a";

    @RenderWebElement
    private final By PAGE_LOGO = By.cssSelector("#logo>a");
    private final By NEWS_MENU = By.cssSelector("a[href$='news/']");

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
            throw new PageOperationException("Exceeded time to find news links. " + e.toString());
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
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
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
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
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
            throw new PageOperationException("Exceeded time to find the title:" + e.toString());
        }

    }

    /**
     * Method to click a news title
     * 
     * @param newsName - the of the news declared in share!
     * @return
     */
    public void clickNewsByName(String newsName)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]", newsName))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

    }

    /**
     * Method to click a news title
     *
     * @param newsTitle - the of the news declared in share!
     * @return
     */
    public void clickNewsByTitle(String newsTitle)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[text()='%s']", newsTitle))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

    }

    /**
     * Method to navigate to news folders
     *
     * @param folderName - the Name of the folder from SHARE
     * @return WcmqsNewsPage
     */
    public WcmqsNewsPage openNewsPageFolder(String folderName)
    {
        try
        {
            WebElement news = drone.findAndWait(NEWS_MENU);
            drone.mouseOver(news);

            drone.findAndWait(By.cssSelector(String.format("a[href$='/wcmqs/news/%s/']", folderName))).click();

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news links. " + e.toString());
        }

        return new WcmqsNewsPage(drone);
    }



}
