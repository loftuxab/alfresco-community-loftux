package org.alfresco.po.alfresco;

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
public class WcmqsBlogPage extends SharePage
{
        @RenderWebElement
        private final By PAGE_LOGO = By.cssSelector("#logo>a");

        /**
         * Constructor.
         *
         * @param drone WebDriver to access page
         */
        public WcmqsBlogPage(WebDrone drone)
        {
                super(drone);
        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsBlogPage render(RenderTime timer)
        {
                webElementRender(timer);
                return this;

        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsBlogPage render()
        {
                return render(new RenderTime(maxPageLoadingTime));
        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsBlogPage render(final long time)
        {
                return render(new RenderTime(time));
        }

        /**
         * Method to navigate to a blog post found by its title
         * @param title - Blog post title
         */
        public void openBlogPost(String title)
        {
                try
                {
                        drone.findAndWait(By.xpath(String.format("//a[contains(text(),'%s')]", title))).click();
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find blog post. " + e.toString());
                }
        }

}
