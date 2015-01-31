package org.alfresco.po.share.site.datalist.items;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to reflect Visitor Feedback list item
 * 
 * @author Cristins.Axinte
 */

public class VisitorFeedbackRow extends AbstractItem
{
    private static Log logger = LogFactory.getLog(VisitorFeedbackRow.class);

  private static final By VISITOR_EMAIL_FIELD = By.cssSelector("td[class*='ws_visitorEmail']");
  private static final By FLAGGED_FIELD = By.cssSelector("td[class*='ws_commentFlagged']");
  private static final By FEEDBACK_TYPE_FIELD = By.cssSelector("td[class*='ws_feedbackType']");
  private static final By FEEDBACK_SUBJECT_FIELD = By.cssSelector("td[class*='ws_feedbackSubject']");
  private static final By COMMENT_FIELD = By.cssSelector("td[class*='ws_feedbackComment']");
  private static final By RATING_FIELD = By.cssSelector("td[class*='ws_rating']");
  private static final By RELEVANT_ASSET_FIELD = By.cssSelector("td[class*='ws_relevantAssetAssoc']");
  private static final By VISITOR_NAME_FIELD = By.cssSelector("td[class*='ws_visitorName']");
  private static final By VISITOR_WEBSITE_FIELD = By.cssSelector("td[class*='ws_visitorWebsite']");
  
//  private static final By VISITOR_EMAIL_FIELD = By.cssSelector("tr>td[class*='ws_visitorEmail']");
//  private static final By FLAGGED_FIELD = By.cssSelector("tr>td[class*='ws_commentFlagged']");
//  private static final By FEEDBACK_TYPE_FIELD = By.cssSelector("tr>td[class*='ws_feedbackType']");
//  private static final By FEEDBACK_SUBJECT_FIELD = By.cssSelector("tr>td[class*='ws_feedbackSubject']");
//  private static final By COMMENT_FIELD = By.cssSelector("tr>td[class*='ws_feedbackComment']");
//  private static final By RATING_FIELD = By.cssSelector("tr>td[class*='ws_rating']");
//  private static final By RELEVANT_ASSET_FIELD = By.cssSelector("tr>td[class*='ws_relevantAssetAssoc']");
//  private static final By VISITOR_NAME_FIELD = By.cssSelector("tr>td[class*='ws_visitorName']");
//  private static final By VISITOR_WEBSITE_FIELD = By.cssSelector("tr>td[class*='ws_visitorWebsite']");
  
  private WebElement webElement;
    
    public VisitorFeedbackRow(WebDrone drone)
    {
        super(drone);
    }
    
    /**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param drone
     */
    public VisitorFeedbackRow(WebElement element, WebDrone drone)
    {
        super(drone);
        this.webElement = element;
        this.drone = drone;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VisitorFeedbackRow render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VisitorFeedbackRow render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public VisitorFeedbackRow render(long time)
    {
        return render(new RenderTime(time));
    }

//    /**
//     * Check visitor feedback list table that all elements located
//     * 
//     * @return true if all elements were found, throw exception if any element not found
//     */
//    public boolean isAllTableFieldsPresented()
//    {
//        boolean isDisplayed;
//        try
//        {
//            logger.info("Check that all elements presented");
//
//            WebElement emaiField = drone.find(VISITOR_EMAIL_FIELD);
//            WebElement flaggedField = drone.find(FLAGGED_FIELD);
//            WebElement feedbackTypeField = drone.find(FEEDBACK_TYPE_FIELD);
//            WebElement feedbackSubjectField = drone.find(FEEDBACK_SUBJECT_FIELD);
//            WebElement commentField = drone.find(COMMENT_FIELD);
//            WebElement ratingField = drone.find(RATING_FIELD);
//            WebElement relevantAssetField = drone.find(RELEVANT_ASSET_FIELD);
//            WebElement nameField = drone.find(VISITOR_NAME_FIELD);
//            WebElement websiteField = drone.find(VISITOR_WEBSITE_FIELD);
//            isDisplayed = emaiField.isDisplayed() && flaggedField.isDisplayed() && feedbackTypeField.isDisplayed() && feedbackSubjectField.isDisplayed()
//                    && commentField.isDisplayed() && ratingField.isDisplayed() && relevantAssetField.isDisplayed() && nameField.isDisplayed()
//                    && websiteField.isDisplayed();
//        }
//        catch (NoSuchElementException te)
//        {
//            logger.debug("Unable to locate any element for visitor feedback list form");
//            throw new ShareException("Unable to locate any element for visitor feedback list form");
//        }
//        if (!isDisplayed)
//        {
//            throw new ShareException("The operation has timed out");
//        }
//        return isDisplayed;
//
//    }
    
    /**
     * Method to get visitor email
     *
     * @return String
     */
    public String getVisitorEmail()
    {
        try
        {
            return webElement.findElement(VISITOR_EMAIL_FIELD).getText();
        }
        catch (TimeoutException ex)
        {
            throw new ShareException("Exceeded time to find the visitor email field", ex);
        }
    }
    
    
    /**
     * Method to get flag of the comment
     *
     * @return String
     */
    public String getCommnetFlag()
    {
        try
        {
            return webElement.findElement(COMMENT_FIELD).getText();
        }
        catch (TimeoutException ex)
        {
            throw new ShareException("Exceeded time to find the comment flag field", ex);
        }
    }
    
    /**
     * Method to get visitor's comment
     *
     * @return String
     */
    public String getVisitorComment()
    {
        try
        {
            return webElement.findElement(COMMENT_FIELD).getText();
        }
        catch (TimeoutException ex)
        {
            throw new ShareException("Exceeded time to find the visitor's comment", ex);
        }
    }
    
    /**
     * Method to get visitor's name
     *
     * @return String
     */
    public String getVisitorName()
    {
        try
        {
            return webElement.findElement(VISITOR_NAME_FIELD).getText();
        }
        catch (TimeoutException ex)
        {
            throw new ShareException("Exceeded time to find the visitor's name", ex);
        }
    }
    
    /**
     * Method to get visitor's website
     *
     * @return String
     */
    public String getVisitorWebsite()
    {
        try
        {
            return webElement.findElement(VISITOR_WEBSITE_FIELD).getText();
        }
        catch (TimeoutException ex)
        {
            throw new ShareException("Exceeded time to find the visitor's website", ex);
        }
    }

}
