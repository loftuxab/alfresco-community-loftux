package org.alfresco.po.share.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucian Tuca on 11/18/2014.
 */
public class WcmqsSearchPage extends SharePage
{
        @RenderWebElement
        private final By TAG_SEARCH_RESULT_TITLES = By.cssSelector(".newslist-wrapper>li>h4>a");

        /**
         * Constructor.
         *
         * @param drone WebDriver to access page
         */
        public WcmqsSearchPage(WebDrone drone)
        {
                super(drone);
        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsSearchPage render(RenderTime timer)
        {
                webElementRender(timer);
                return this;

        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsSearchPage render()
        {
                return render(new RenderTime(maxPageLoadingTime));
        }

        @SuppressWarnings("unchecked")
        @Override
        public WcmqsSearchPage render(final long time)
        {
                return render(new RenderTime(time));
        }

        /**
         * Method to get tag search result titles
         */
        public ArrayList<String> getTagSearchResults()

        {
                ArrayList<String> results = new ArrayList<String>();
                try
                {
                        List<WebElement> links = drone.findAndWaitForElements(TAG_SEARCH_RESULT_TITLES);
                        for (WebElement div : links)
                        {
                                results.add(div.getText());
                        }
                }
                catch (NoSuchElementException nse)
                {
                        throw new PageException("Unable to access search results site data", nse);
                }

                return results;
        }

}
