/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
    package org.alfresco.share.repository;

import org.alfresco.po.share.util.PageUtils;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.FtpUtil;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.assertTrue;

@Listeners(FailedTestListener.class)
@Test(groups = { "NonGrid", "EnterpriseOnly" }, timeOut = 400000)

/**
 * Created by olga.lokhach
 */

public class RepositoryFtpTest2 extends AbstractUtils
{
    private static final Logger logger = Logger.getLogger(RepositoryFtpTest2.class);

    @Override
    @BeforeClass
    public void setup() throws Exception
    {
        super.setup();
        logger.info("Starting Tests: " + testName);
        testName = this.getClass().getSimpleName();

        FtpUtil.setCustomFtpPort(drone, ftpPort);

    }

    /**
     * AONE-6448: Connect to FTP via Firefox
     */

    @Test
    public void AONE_6448() throws Exception
    {
        String ftpUrl = "ftp://%s:%s@%s";
        String serverIP = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "") + ":" + ftpPort;
        ftpUrl = String.format(ftpUrl, ADMIN_USERNAME, ADMIN_PASSWORD, serverIP);

        // Navigate to ftp://login:pass@server_ip
        drone.navigateTo(ftpUrl);
        String handle1 = drone.getWindowHandle();
        assertTrue(drone.findAndWait(By.cssSelector(".up")).getAttribute("href").contains(ftpUrl));
        assertTrue(getDrone().findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));

        //  Navigate to ftp://server_ip
        ftpUrl = "ftp://%s";
        ftpUrl = String.format(ftpUrl, serverIP);
        drone.executeJavaScript("myWindow = window.open(\"" + ftpUrl + "\"" + "," + "\"myWindow\"," + "\"" + "[top=500, left=500, width=800, height=600]" + "\""
            + "); myWindow.focus(); myWindow.moveTo(0, 0);");
        sleep();

        //  Fill Login and Pass fields and click 'Ok' button;
        try
        {
            Robot robot = new Robot();
            type(ADMIN_USERNAME);
            robot.keyPress(KeyEvent.VK_TAB);
            type(ADMIN_PASSWORD);
            robot.keyPress(KeyEvent.VK_ENTER);
        }
        catch (AWTException ex)
        {
            logger.error(ex);
        }

        Set<String> windowIds = drone.getWindowHandles();
        Iterator<String> iter = windowIds.iterator();
        String windowId1 = iter.next();
        String windowId2 = iter.next();
        if (windowId1.equals(handle1))
        {
            drone.switchToWindow(windowId2);
        }
        assertTrue(drone.findAndWait(By.cssSelector(".up")).getAttribute("href").contains(ftpUrl), String.format("displayed: %s, Expected: %s",
            drone.findAndWait(By.cssSelector(".up")).getAttribute("href"), ftpUrl));
        assertTrue(getDrone().findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));
        String handle2 = drone.getWindowHandle();

        // Navigate to ftp://login@server_ip
        ftpUrl = "ftp://%s@%s";
        ftpUrl = String.format(ftpUrl, ADMIN_USERNAME, serverIP);
        drone.executeJavaScript(
            "myWindow1 = window.open(\"" + ftpUrl + "\"" + "," + "\"myWindow1\"," + "\"" + "[top=500, left=500, width=800, height=600]" + "\""
                + "); myWindow1.focus(); myWindow1.moveTo(0, 0);");
        sleep();

        // Fill Password field and click 'Ok' button;
        try
        {
            Robot robot = new Robot();
            robot.setAutoWaitForIdle(true);
            type(ADMIN_PASSWORD);
            robot.keyPress(KeyEvent.VK_ENTER);
        }
        catch (AWTException ex)
        {
            logger.error(ex);
        }

        windowIds = drone.getWindowHandles();
        iter = windowIds.iterator();
        windowId1 = iter.next();
        windowId2 = iter.next();
        String windowId3 = iter.next();
        if (windowId1.equals(handle1) && windowId2.equals(handle2))
        {
            drone.switchToWindow(windowId3);
        }
        assertTrue(drone.findAndWait(By.cssSelector(".up")).getAttribute("href").contains(ftpUrl), String.format("displayed: %s, Expected: %s",
            drone.findAndWait(By.cssSelector(".up")).getAttribute("href"), ftpUrl));
        assertTrue(getDrone().findAndWait(By.cssSelector(".dir")).getText().contains("Alfresco"));
    }

    private void writeToClipboard(String s)
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new StringSelection(s);
        clipboard.setContents(transferable, null);
    }

    private void type(String text)
    {
        writeToClipboard(text);
        pasteClipboard();
    }

    private void pasteClipboard()
    {
        try
        {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.delay(50);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }
        catch (AWTException ex)
        {
            logger.error(ex);
        }
    }

    private static void sleep()
    {
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}