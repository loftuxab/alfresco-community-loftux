package org.alfresco.po.share.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
/**
 * Created by Lucian Tuca on 11/18/2014.
 */
public class WcmqsBlogPage extends SharePage
{
        @RenderWebElement
        private final By PAGE_LOGO = By.cssSelector("#logo>a");
//        private final By BLOGS_TITLE = By.cssSelector("div.interior-header");

        public static final String ETHICAL_FUNDS = "Ethical funds";
        public static final String COMPANY_ORGANISES_WORKSHOP = "Company organises workshop";
        public static final String ANALYSTS_LATEST_THOUGHTS = "latest thoughts";

        public static final String BLOG_MENU = "Blog";
        public static final String BLOG = "blog";

        public static final String BLOG_1 = "blog1.html";
        public static final String BLOG_2 = "blog2.html";
        public static final String BLOG_3 = "blog3.html";

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

        public boolean checkIfBlogIsDeleted(String title)
        {
                boolean check;
                try
                {
                        drone.waitUntilElementDisappears(By.xpath(String.format("//a[contains(text(),'%s')]", title)),
                                SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
                        check = true;
                }
                catch (NoSuchElementException nse)
                {
                        return false;
                }

                return check;
        }

        public boolean checkIfBlogExists(String title)
        {
                boolean check;
                try
                {
                        drone.waitForElement(By.xpath(String.format("//a[contains(text(),'%s')]", title)),
                                SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
                        check = true;
                }
                catch (NoSuchElementException nse)
                {
                        return false;
                }

                return check;
        }
        
        /**
         * Method to click a blog post name from share
         * 
         * @param blogNameFromShare of the blog post declared in share!
         */
        public void clickBlogNameFromShare(String blogNameFromShare)
        {
            try
            {
                drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]", blogNameFromShare))).click();
            }
            catch (TimeoutException e)
            {
                throw new PageOperationException("Exceeded time to find news link. " + e.toString());
            }
        }
}
