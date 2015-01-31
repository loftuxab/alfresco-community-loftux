package org.alfresco.po.share.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.datalist.items.VisitorFeedbackRow;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Representation of comment from wcmqs site that can be used while leaving a comment to an article.
 * 
 * @author Cristina Axinte
 */

public class WcmqsComment extends SharePage
{
    private final By NAME_FROM_COMMENT=By.cssSelector("ul.comments-wrapper h4");
    private final By DATE_OF_COMMENT=By.cssSelector("span.newslist-date");
    private final By TEXT_OF_COMMENT=By.cssSelector("span.comments-text");
    private final By REPORT_COMMENT=By.cssSelector("span.comments-report");
    
    public WcmqsComment(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsComment render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsComment render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsComment render(long time)
    {
        return render(new RenderTime(time));
    }
    
    /**
     * Method that returns the post content
     * @return String
     */
    public String getNameFromContent()
    {
            try
            {
                    return drone.findAndWait(NAME_FROM_COMMENT).getText();
            }
            catch (TimeoutException e)
            {
                    throw new PageOperationException("Exceeded time to find name from comment. " + e.toString());
            }
    }
    
    /**
     * Method that returns the post content
     * @return String
     */
    public WcmqsBlogPostPage clickReportComment()
    {
            try
            {
                    drone.findAndWait(REPORT_COMMENT).click();
                    return new WcmqsBlogPostPage(drone);
            }
            catch (TimeoutException e)
            {
                    throw new PageOperationException("Exceeded time to find report this post link. " + e.toString());
            }
    }
    
    
}
