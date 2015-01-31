package org.alfresco.po.alfresco;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class WcmqsEditPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By NAME_INPUT = By.cssSelector("input[id='wef-panel-wefPanel_prop_cm_name']");
    private static final By TITLE_INPUT = By.cssSelector("input[id='wef-panel-wefPanel_prop_cm_title']");
    private static final By DESCRIPTION_INPUT = By.cssSelector("textarea[id='wef-panel-wefPanel_prop_cm_description']");
    private static final String CONTENT_IFRAME = "wef-panel-wefPanel_prop_cm_content_ifr";
    private static final By TEMPLATENAME_INPUT = By.cssSelector("input[id='wef-panel-wefPanel_prop_ws_templateName']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id='wef-panel-wefPanel-form-submit-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id='wef-panel-wefPanel-form-cancel-button']");
    
    private static final By NOTIFICATION_BALLOON = By.cssSelector("div.balloon>div>div");

    private final TinyMceEditor contentTinyMceEditor;

    /**
     * Constructor.
     */
    public WcmqsEditPage(WebDrone drone)
    {
        super(drone);
        contentTinyMceEditor = new TinyMceEditor(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsEditPage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(NAME_INPUT), getVisibleRenderElement(SUBMIT_BUTTON), getVisibleRenderElement(CANCEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsEditPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsEditPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for edit name
     * 
     * @param userName
     * @return
     */
    public void editName(String newName)
    {
        drone.findAndWait(NAME_INPUT).clear();
        drone.findAndWait(NAME_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(newName);
    }

    /**
     * Method for edit title
     * 
     * @param password
     * @return
     */
    public void editTitle(String newTitle)
    {
        drone.findAndWait(TITLE_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(newTitle);
    }

    /**
     * Method for edit Description
     * 
     * @param password
     * @return
     */
    public void editDescription(String newDescription)
    {
        drone.findAndWait(DESCRIPTION_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(newDescription);
    }

    /**
     * Method for edit Template Name
     * 
     * @param password
     * @return
     */
    public void editTemplateName(String newTemplateName)
    {
        drone.findAndWait(TEMPLATENAME_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(newTemplateName);
    }

    public void clickSubmitButton()
    {
        drone.findAndWait(SUBMIT_BUTTON).click();
    }

    public void clickCancelButton()
    {
        drone.findAndWait(CANCEL_BUTTON).click();
    }

    public void editArticle(WcmqsArticleDetails articleDetails)
    {
        logger.info("Edit and save the asticle");
        if (articleDetails == null || StringUtils.isEmpty(articleDetails.getName()))
        {
            throw new UnsupportedOperationException("Article name cannot be blank");
        }

        editName(articleDetails.getName());
        editDescription(articleDetails.getDescription());
        editTitle(articleDetails.getTitle());
        editTemplateName(articleDetails.getTemplateName());
        insertTextInContent(articleDetails.getContent());
        clickSubmitButton();
    }

    public WcmqsArticleDetails getArticleDetails()
    {
        String name = drone.findAndWait(NAME_INPUT).getAttribute("value");
        String title = drone.findAndWait(TITLE_INPUT).getAttribute("value");
        String description = drone.findAndWait(DESCRIPTION_INPUT).getText();
        String content = getContentTinyMCEEditor().getText();
        String templateName = drone.findAndWait(TEMPLATENAME_INPUT).getAttribute("value");
        return new WcmqsArticleDetails(name, title, description, content, templateName);
    }

    /**
     * Get TinyMCEEditor object to navigate TinyMCE functions.
     * 
     * @return
     */
    public TinyMceEditor getContentTinyMCEEditor()
    {
        contentTinyMceEditor.setTinyMce(CONTENT_IFRAME);
        return contentTinyMceEditor;
    }

    /**
     * Method for inserting text into the Reply form
     * 
     * @param txtLines
     */
    public void insertTextInContent(String txtLines)
    {
        try
        {
            contentTinyMceEditor.addContent(txtLines);
        }
        catch (TimeoutException toe)
        {
            throw new ShareException("Time out finding #tinymce content", toe);
        }
    }

    public boolean isSubmitButtonDisplayed()
    {
        return drone.isElementDisplayed(SUBMIT_BUTTON);
    }
    
    public String getNotificationMessage()
    {
        try 
        {
            WebElement notifBalloon=drone.findAndWait(NOTIFICATION_BALLOON);
            return notifBalloon.getText();            
        }
        catch (TimeoutException toe)
        {
            throw new ShareException("Time out finding notification balloon.", toe);
        }
        
    }
}
