/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.contentrule;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Maryia Zaichanka
 */
public class SetPropertyValuePage extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(SetPropertyValuePage.class);

    private final RenderElement headerElement = getVisibleRenderElement(By
            .cssSelector("div[id*='selectSetPropertyDialog-dialog_h']"));

    private final By propertyFoldersListCss = By.cssSelector("span[class$='ygtvlabel']");
    private final By setValueOkButtonCss = By
            .cssSelector("span[id$='selectSetPropertyDialog-ok-button']>span>button");

    private final By getValueXpath (String valueName)
    {
        return By.xpath("//tr/td[2]//div[contains(@class,'yui-dt-liner') and contains(text(),'" + valueName + "')]");
    }


    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public SetPropertyValuePage(WebDrone drone)
    {
        super(drone);

    }

    @SuppressWarnings("unchecked")
    @Override
    public SetPropertyValuePage render(RenderTime timer)
    {
        elementRender(timer, headerElement);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetPropertyValuePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetPropertyValuePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    /**
     * This method finds the clicks on ok button
     *
     * @return HtmlPage Create Rule Page
     */
    public HtmlPage selectOkButton()
    {
        try
        {
            drone.findAndWait(setValueOkButtonCss).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find an ok button : ", e);
            throw new PageException("Unable to find the ok button.");
        }
    }

    /**
     * This method finds and selects the type folder from the
     * displayed list
     *
     * @return SetPropertyValuePage
     */
    public SetPropertyValuePage selectPropertyTypeFolder(String folderName)
    {
        if (StringUtils.isEmpty(folderName))
        {
            throw new IllegalArgumentException("Folder name is required");
        }
        try
        {
            for (WebElement folder : drone.findAndWaitForElements(propertyFoldersListCss))
            {
                if (folder.getText() != null)
                {
                    if (folder.getText().equalsIgnoreCase(folderName))
                    {
                        folder.click();
                        drone.waitForElement(propertyFoldersListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        return new SetPropertyValuePage(drone);
                    }
                }
            }
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find values", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of values", e);
        }

        throw new PageOperationException("Unable to select " + folderName);
    }

    /**
     * This method finds and selects the value for Set Value Property from the
     * displayed list
     *
     * @return SetPropertyValuePage
     */
    public SetPropertyValuePage selectValueFromList(String valueName)
    {

        try
        {
            WebElement value = drone.findAndWait(getValueXpath(valueName));
            value.click();
            return new SetPropertyValuePage(drone);

        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find values", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of values", e);
        }

        throw new PageOperationException("Unable to select value");
    }

}
