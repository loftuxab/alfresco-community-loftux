package org.alfresco.po.share.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Lucian Tuca on 11/18/2014.
 */
public class WcmqsBlogPostPage extends SharePage
{
        public static final By CREATE_ARTICLE = By.cssSelector("a[id='yui-gen5']");
        public static final By TITLE = By.xpath(".//div/h2");
        public static final By CONTENT = By.xpath(".//div/div[2]/p");
        private final By DELETE_LINK = By.cssSelector("a[class=alfresco-content-delete]");
        private final By DELETE_CONFIRM_OK = By.xpath("//button[contains(text(),'Ok')]");
        private final By DELETE_CONFIRM_CANCEL = By.xpath("//button[contains(text(),'Cancel')]");
        private final By DELETE_CONFIRM_WINDOW = By.id("prompt_c");
        @RenderWebElement
        private final By PAGE_LOGO = By.cssSelector("#logo>a");

        /**
         * Constructor.
         *
         * @param drone WebDriver to access page
         */
        public WcmqsBlogPostPage(WebDrone drone)
        {
                super(drone);
        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsBlogPostPage render(RenderTime timer)
        {
                webElementRender(timer);
                return this;

        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsBlogPostPage render()
        {
                return render(new RenderTime(maxPageLoadingTime));
        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsBlogPostPage render(final long time)
        {
                return render(new RenderTime(time));
        }

        /**
         * Method to click on Create article
         *
         * @return WcmqsEditPage
         */
        public WcmqsEditPage createArticle()
        {
                try
                {
                        drone.findAndWait(CREATE_ARTICLE).click();
                        return new WcmqsEditPage(drone);
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to create article. " + e.toString());
                }
        }

        /**
         * Method that retuns the post title
         * @return String
         */
        public String getTitle()
        {
                try
                {
                        return drone.findAndWait(TITLE).getText();
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find the post title. " + e.toString());
                }
        }

        /**
         * Method that retuns the post content
         * @return String
         */
        public String getContent()
        {
                try
                {
                        return drone.findAndWait(CONTENT).getText();
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find post content. " + e.toString());
                }
        }

        /**
         * Presses the delete button while you are in blog editing
         */
        public void deleteArticle()
        {

                try
                {
                        drone.findAndWait(DELETE_LINK).click();
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
                }
        }

        /**
         * Verifies if delete confirmation window is displayed
         *
         * @return boolean
         */
        public boolean isDeleteConfirmationWindowDisplayed()
        {
                boolean check = false;
                try
                {

                        drone.waitForElement(DELETE_CONFIRM_WINDOW, SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
                        WebElement importMessage = drone.find(By.id("prompt_c"));
                        check = true;
                }
                catch (NoSuchElementException nse)
                {
                }

                return check;
        }

        public void confirmArticleDelete()
        {
                try
                {
                        drone.findAndWait(DELETE_CONFIRM_OK).click();
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
                }
        }

        public void cancelArticleDelete()
        {
                try
                {
                        drone.findAndWait(DELETE_CONFIRM_CANCEL).click();
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
                }
        }

}
