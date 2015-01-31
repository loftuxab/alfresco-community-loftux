package org.alfresco.po.alfresco;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

public class WcmqsNewsArticleDetails extends SharePage
{
    private final By ARTICLE_BODY = By.cssSelector("div.article-body");
    private final By EDIT_LINK = By.cssSelector("a.alfresco-content-edit");
    private final By CREATE_LINK = By.cssSelector("a.alfresco-content-new");
    private final By DELETE_LINK = By.cssSelector("alfresco-content-delete");
    private final By TITLE_LINK = By.cssSelector("div.interior-content>h2");
    private final By DETAILS_LINK = By.cssSelector("div.interior-content span.ih-date");

    public WcmqsNewsArticleDetails(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsArticleDetails render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(ARTICLE_BODY));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsArticleDetails render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsArticleDetails render(final long time)
    {
        return render(new RenderTime(time));
    }

    public static WcmqsNewsArticleDetails getCurrentNewsArticlePage(WebDrone drone)
    {
        WcmqsNewsArticleDetails currentPage = new WcmqsNewsArticleDetails(drone);
        currentPage.render();
        return currentPage;
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
            throw new PageOperationException("Exceeded time to find edit button. " + e.toString());
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
            throw new PageOperationException("Exceeded time to find edit button. " + e.toString());
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
            throw new PageOperationException("Exceeded time to find edit button. " + e.toString());
        }
    }

    public String getTitleOfNewsArticle()
    {
        try
        {
            return drone.findAndWait(TITLE_LINK).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find title of the article. " + e.toString());
        }

    }

    public String getBodyOfNewsArticle()
    {
        try
        {
            return drone.findAndWait(ARTICLE_BODY).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find body of the article. " + e.toString());
        }

    }

    public String getDetailsOfNewsArticle()
    {
        try
        {
            return drone.findAndWait(DETAILS_LINK).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find deatils of the article. " + e.toString());
        }

    }
}
