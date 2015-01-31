package org.alfresco.po.alfresco;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class WcmqsHomePage extends SharePage
{
    private static Log logger = LogFactory.getLog(WcmqsHomePage.class);
    private final By PAGE_MENU = By.cssSelector("div[id='myslidemenu']");
    private final By HOME_MENU = By.xpath("//div[@id='myslidemenu']//a[text()='Home']");
    private final By NEWS_MENU = By.cssSelector("a[href$='news/']");
    private final By PUBLICATIONS_MENU = By.cssSelector("a[href$='publications/']");
    private final By BLOG_MENU = By.cssSelector("a[href$='blog/']");
    private final By CONTACT_MENU = By.cssSelector("div.link-menu");
    private final By FIRST_ARTICLE = By.cssSelector("div[id='left'] div.interior-content ul>li:nth-child(3)>h4>a");

    // private final By BLOG_ARTICLE=By.cssSelector("div[id='left'] div.interior-content div.blog-entry:nth-child(2)>h2>a");
    // private final By RIGHT_PANEL = By.cssSelector("div[id='right']");

    private final By EDIT_LINK = By.cssSelector("a.alfresco-content-edit");
    private final By CREATE_LINK = By.cssSelector("a.alfresco-content-new");
    private final By DELETE_LINK = By.cssSelector("alfresco-content-delete");

    public WcmqsHomePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(PAGE_MENU), getVisibleRenderElement(CONTACT_MENU));
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

    public WcmqsHomePage selectMenu(String menuOption)
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
            throw new TimeoutException("Exceeded time to find and click " + menuOption + " menu. " + e.toString());
        }
        return new WcmqsHomePage(drone);
    }

    public HtmlPage selectFirstArticleFromLeftPanel()
    {
        logger.info("Selecting first article from left panel.");
        List<WebElement> allArticles = drone.findAll(FIRST_ARTICLE);
        try
        {
            allArticles.get(0).click();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find and click first article. " + e.toString());
        }

        WebElement editLink = drone.find(EDIT_LINK);
        if (!editLink.isDisplayed())
        {
            return new WcmqsLoginPage(drone);
        }
        else
        {
            return this;
        }
    }

    public WcmqsEditPage clickEditButton()
    {
        try
        {
            drone.findAndWait(EDIT_LINK).click();
            return new WcmqsEditPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find edit button. " + e.toString());
        }
    }

    public void clickCreateButton()
    {
        try
        {
            drone.findAndWait(CREATE_LINK).click();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find edit button. " + e.toString());
        }
    }

    public void clickDeleteButton()
    {
        try
        {
            drone.findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find edit button. " + e.toString());
        }
    }
    
    /**
     * Method to navigate to news folders
     * @param folderName - the Name of the folder from SHARE
     * @return WcmqsNewsPage 
     */
    public WcmqsNewsPage openNewsPage(String folderName)
    {
        try
        {
            WebElement news = drone.findAndWait(NEWS_MENU);
            drone.mouseOver(news);
            
            drone.findAndWait(By.cssSelector(String.format("a[href$='/wcmqs/news/%s/']", folderName))).click();;
            
        }
        catch (TimeoutException e)
        {
            throw new TimeoutException("Exceeded time to find news links. " + e.toString());
        }   
        
        return new WcmqsNewsPage(drone);
    } 

}
