package org.alfresco.po.share.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * Created by Lucian Tuca on 11/18/2014.
 */
public class WcmqsBlogPostPage extends SharePage
{
        public static final By CREATE_ARTICLE = By.cssSelector("a[id='yui-gen5']");
        public static final By TITLE = By.xpath(".//div/h2");
        public static final By CONTENT = By.xpath(".//div/div[2]/p");
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
}
