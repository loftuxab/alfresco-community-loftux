/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.share.util;

import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * @author Sergey Kardash
 */
public class TelnetUtil extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(FtpUtil.class);

    /**
     * Method to check connection via telnet
     * 
     * @param shareUrl
     * @param port
     * @return true if connection was established, else will be returned false
     */

    public static boolean connectServer(String shareUrl, String port)
    {

        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");
        int portInt = Integer.parseInt(port);
        boolean status;

        final TelnetClient telnetClient = new TelnetClient();
        try
        {
            if (server == null || portInt < 0 || portInt > 65535)
            {
                logger.info("Check server ip or port number: server '" + server + "' port " + port);
                return false;
            }
            telnetClient.connect(server, portInt);
            telnetClient.disconnect();
            status = true;
            logger.info("Connected to server '" + server + "' port " + port + " via telnet");

        }
        catch (ConnectException ce)
        {
            logger.info("Could not connect to server '" + server + "' port " + port);
            status = false;
        }
        catch (UnknownHostException e)
        {
            logger.error("Unknown host: " + server);
            status = false;
        }
        catch (IOException e)
        {
            logger.error("Error connecting to server: " + server + " - " + e.getMessage(), e);
            status = false;
        }

        return status;

    }

}
