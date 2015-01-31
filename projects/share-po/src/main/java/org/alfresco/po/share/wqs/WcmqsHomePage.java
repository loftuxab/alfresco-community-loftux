package org.alfresco.po.share.wqs;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public class WcmqsHomePage extends WcmqsAbstractPage
{
    private static Log logger = LogFactory.getLog(WcmqsHomePage.class);
    private final By PAGE_MENU = By.cssSelector("div[id='myslidemenu']");
    private final By HOME_MENU = By.xpath("//div[@id='myslidemenu']//a[text()='Home']");
    private final By NEWS_MENU = By.cssSelector("a[href$='news/']");
    private final By BLOG_MENU = By.cssSelector("a[href$='blog/']");
    private final By CONTACT_MENU = By.cssSelector("div.link-menu");
    private final By FIRST_ARTICLE = By.cssSelector("div[id='left'] div.interior-content ul>li:nth-child(1)>h4>a");

    private final By PUBLICATIONS_MENU = By.cssSelector("a[href$='publications/']");
    private final By RESEARCH_REPORTS = By.cssSelector("a[href$='research-reports/']");
    private final By WHITE_PAPERS = By.cssSelector("a[href$='white-papers/']");

    // private final By BLOG_ARTICLE=By.cssSelector("div[id='left'] div.interior-content div.blog-entry:nth-child(2)>h2>a");
    // private final By RIGHT_PANEL = By.cssSelector("div[id='right']");

    public WcmqsHomePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(PAGE_MENU), getVisibleRenderElement(CONTACT_MENU), getVisibleRenderElement(PUBLICATIONS_MENU));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public void selectMenu(String menuOption)
    {
        WebElement webElement = null;
        switch (menuOption.toLowerCase())
        {
            case "home":
            {
                webElement = drone.findAndWait(HOME_MENU);
                break;
            }
            case "news":
            {
                webElement = drone.findAndWait(NEWS_MENU);
                break;
            }
            case "publications":
            {
                webElement = drone.findAndWait(PUBLICATIONS_MENU);
                break;
            }
            case "blog":
            {
                webElement = drone.findAndWait(BLOG_MENU);
                break;
            }

        }
        try
        {
            webElement.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click " + menuOption + " menu. " + e.toString());
        }
    }

    public WcmqsNewsArticleDetails selectFirstArticleFromLeftPanel()
    {
        logger.info("Selecting first article from left panel.");
        List<WebElement> allArticles = drone.findAll(FIRST_ARTICLE);
        try
        {
            allArticles.get(0).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click first article. " + e.toString());
        }

        return new WcmqsNewsArticleDetails(drone);
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
    
    /**
     * Method to get all the folders for a selected Primary folder (eg: News, Publications, Blog)
     * 
     * @return List<ShareLink>
     */
    public List<ShareLink> getAllFoldersFromMenu(String folderName)
    {
        List<ShareLink> folders = new ArrayList<ShareLink>();
        try
        {
            WebElement folder = drone.findAndWait(BLOG_MENU);
            drone.mouseOver(folder);
            
            List<WebElement>  firstFolders = drone.findAll(By.xpath(String.format(".//*[@id='myslidemenu']//a[contains(@href,'%s')]", folderName)));
            
            for (WebElement div : firstFolders)
            {
                folders.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return folders;
    }

    public boolean isResearchReportsDisplayed()
    {
        return drone.isElementDisplayed(RESEARCH_REPORTS);
    }

    public boolean isWhitePapersDisplayed()
    {
        return drone.isElementDisplayed(WHITE_PAPERS);
    }

    public void mouseOverMenu(String menuOption)
    {
        WebElement webElement = null;
        switch (menuOption.toLowerCase())
        {
            case "home":
            {
                webElement = drone.findAndWait(HOME_MENU);
                break;
            }
            case "news":
            {
                webElement = drone.findAndWait(NEWS_MENU);
                break;
            }
            case "publications":
            {
                webElement = drone.findAndWait(PUBLICATIONS_MENU);
                break;
            }
            case "blog":
            {
                webElement = drone.findAndWait(BLOG_MENU);
                break;
            }

        }
        try
        {
            drone.mouseOver(webElement);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click " + menuOption + " menu. " + e.toString());
        }
    }

    public WcmqsAllPublicationsPage openPublicationsPageFolder(String folderName)
    {
        try
        {

            WebElement menu = drone.findAndWait(PUBLICATIONS_MENU);

            drone.mouseOver(menu);
            switch (folderName.toLowerCase())
            {
                case "white papers":
                {

                    drone.findAndWait(WHITE_PAPERS).click();
                    break;
                }

                case "research reports":
                {
                    drone.findAndWait(RESEARCH_REPORTS).click();
                    break;
                }

            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news links. " + e.toString());
        }

        return new WcmqsAllPublicationsPage(drone);
    }

}
