/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.wsi.test.monitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.wsi.WSIConstants;
import org.wsi.WSIException;
import org.wsi.WSIFileNotFoundException;
import org.wsi.test.ToolInfo;
import org.wsi.test.document.DocumentFactory;
import org.wsi.test.log.Log;
import org.wsi.test.log.LogWriter;
import org.wsi.test.monitor.config.ManInTheMiddle;
import org.wsi.test.monitor.config.MonitorConfig;
import org.wsi.test.monitor.config.Redirect;
import org.wsi.util.MessageList;

/**
 * <b>Monitor</b> replaces standard WS-I Monitor class with changes for manual Monitor controlling
 * 
 * @author Mike Shavnev
 */
public class Monitor
{
    private static final int MILLISECONDS_MULTIPLICATOR = 1000;

    private static org.apache.commons.logging.Log LOGGER = LogFactory.getLog(Monitor.class);

    public static final String TOOL_NAME = "Monitor";

    private static final String MONITOR_CONFIG_NAMESPACE_PREFIX = "wsi-monConfig";

    private static final String MONITOR_TOOL_CLASS_NAME = "org.wsi.test.monitor.Monitor";

    private static final String EXIT_MESSAGE_KEY = "exit01";
    private static final String LOG_OR_LOG_WRITER_CREATION_FAILED_MESSAGE_KEY = "error03";
    private static final String LOG_FILE_REPLACEMENT_DENIED_MESSAGE_KEY = "config08";
    private static final String INVALID_LOG_FILE_MESSAGE_KEY = "config11";
    private static final String LOG_FILE_RETRIVING_FAILED_MESSAGE_KEY = "config07";
    private static final String STOP_REQUEST_MESSAGE_KEY = "start02";
    private static final String MONITOR_IS_READY_MESSAGE_KEY = "start01";
    private static final String MONITOR_ERROR_KEY = "error01";
    private static final String MONITOR_ERROR_EXCEPTIONAL_KEY = "error02";
    private static final String STOPPING_MONITOR = "stopping01";
    private static final String MONITOR_STOPPED_KEY = "stopped01";

    private static final String DEFAULT_EXIT_VALUE = "exit";
    private static final String DEFAULT_MONITOR_ERROR_MESSAGE = "Monitor Error:";
    private static final String DEFAULT_MONITOR_STOPPED_MESSAGE = "Monitor stopped.";
    private static final String DEFAULT_STOPPING_THE_MONITOR_MESSAGE = "Stopping the monitor...";
    private static final String DEFAULT_MONITOR_STOPPED_BY_EXCEPTION_MESSAGE = "Monitor Stopped By Exception:";
    private static final String DEFAULT_LOG_FILE_REPLACEMENT_DENIED_EXCEPTION_MESSAGE = "Log file already exists:";
    private static final String DEFAULT_INVALID_LOG_FILE_EXCEPTION_MESSAGE = "The log file location value cannot contain the pass separator character:";
    private static final String DEFAULT_LOG_FILE_RETRIVING_FAILED_EXCEPTION_MESSAGE = "Could not get log file location.";
    private static final String DEFAULT_LOG_OR_LOG_WRITER_CREATION_FAILED_EXCEPTION_MESSAGE = "Could not create log or log writer.";

    private static final String DEFAULT_MONITOR_IS_READY_MESSAGE_PATTERN = "The %s  tool is ready to intercept and log web service messages.";
    private static final String DEFAULT_STOP_REQUEST_MESSAGE_PATTERN = "Type \"%s\" to stop the %s.";

    private int conversationId;

    private Log log;
    private LogWriter logWriter;

    protected ToolInfo toolInfo;
    private MonitorConfig monitorConfig;

    protected List<ServerSocketListener> listenerList;

    protected MessageList messageList;
    protected MessageEntryQueue messageEntryQueue;

    /**
     * @param monitorConfig <b>MonitorConfig</b> manually configured monitor properties
     * @throws WSIException
     */
    public Monitor(MonitorConfig monitorConfig) throws WSIException
    {
        conversationId = 1;

        this.monitorConfig = monitorConfig;
        messageList = new MessageList(MONITOR_TOOL_CLASS_NAME);
        monitorConfig.init(messageList);

        listenerList = new LinkedList<ServerSocketListener>();
        toolInfo = new ToolInfo(TOOL_NAME);
        DocumentFactory documentfactory = DocumentFactory.newInstance();

        String logLocation = monitorConfig.getLogLocation();
        if (logLocation.indexOf(WSIConstants.PATH_SEPARATOR) > -1)
        {
            throw new WSIException(messageList.getMessage(INVALID_LOG_FILE_MESSAGE_KEY, monitorConfig.getLogLocation(), DEFAULT_INVALID_LOG_FILE_EXCEPTION_MESSAGE));
        }
        File logFile = null;
        try
        {
            logFile = new File(logLocation);
        }
        catch (Exception exception)
        {
            throw new WSIException(messageList.getMessage(LOG_FILE_RETRIVING_FAILED_MESSAGE_KEY, DEFAULT_LOG_FILE_RETRIVING_FAILED_EXCEPTION_MESSAGE), exception);
        }
        if (logFile.exists() && !monitorConfig.getReplaceLog())
        {
            throw new IllegalArgumentException(messageList.getMessage(LOG_FILE_REPLACEMENT_DENIED_MESSAGE_KEY, monitorConfig.getLogLocation(),
                    DEFAULT_LOG_FILE_REPLACEMENT_DENIED_EXCEPTION_MESSAGE));
        }

        try
        {
            log = documentfactory.newLog();
            log.setStyleSheetString(monitorConfig.getAddStyleSheet().getStyleSheetString());
            logWriter = documentfactory.newLogWriter();
            logWriter.setWriter(monitorConfig.getLogLocation());
            logWriter.write(new StringReader(log.getStartXMLString("")));
            logWriter.write(new StringReader(toXMLString("")));
            messageEntryQueue = new MessageEntryQueue(this, log, logWriter);
        }
        catch (Exception exception)
        {
            throw new WSIException(messageList.getMessage(LOG_OR_LOG_WRITER_CREATION_FAILED_MESSAGE_KEY, DEFAULT_LOG_OR_LOG_WRITER_CREATION_FAILED_EXCEPTION_MESSAGE), exception);
        }

        ManInTheMiddle maninthemiddle = monitorConfig.getManInTheMiddle();

        @SuppressWarnings("unchecked")
        List<Redirect> redrectList = maninthemiddle.getRedirectList();

        for (Redirect redirect : redrectList)
        {
            listenerList.add(new ServerSocketListener(this, redirect));
        }

        logLocation = messageList.getMessage(EXIT_MESSAGE_KEY, DEFAULT_EXIT_VALUE);

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.debug(monitorConfig.toString());
        }

        if (LOGGER.isDebugEnabled())
        {
            printMessage(MONITOR_IS_READY_MESSAGE_KEY, String.format(DEFAULT_MONITOR_IS_READY_MESSAGE_PATTERN, toolInfo.getName()));
            printMessage(STOP_REQUEST_MESSAGE_KEY, String.format(DEFAULT_STOP_REQUEST_MESSAGE_PATTERN, logLocation, toolInfo.getName()));

            LOGGER.debug("");
        }
    }

    /**
     * Public extension for manual monitor stopping
     */
    public void shutdown()
    {
        stopMonitor();
    }

    public MonitorConfig getMonitorConfig()
    {
        return monitorConfig;
    }

    public Log getLog()
    {
        return log;
    }

    public MessageEntryQueue getMessageEntryQueue()
    {
        return messageEntryQueue;
    }

    public void printMessage(String messageKey, String defaultMessage)
    {
        printMessage(messageKey, null, defaultMessage);
    }

    public void printMessage(String messageKey, String errorMessage, String defaultMessage)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(messageList.getMessage(messageKey, errorMessage, defaultMessage));
        }
    }

    public static void staticPrintMessage(String messageKey, String defaultMessage)
    {
        staticPrintMessage(messageKey, null, defaultMessage);
    }

    public static void staticPrintMessage(String messageKey, String errorMessage, String defaultMessage)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(MessageList.getMessage(MONITOR_TOOL_CLASS_NAME, messageKey, errorMessage, defaultMessage));
        }
    }

    synchronized int getNextConversationId()
    {
        return conversationId++;
    }

    public String toXMLString(String namespacePrefix)
    {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        printwriter.print(toolInfo.getStartXMLString(namespacePrefix));
        printwriter.print(monitorConfig.toXMLString(MONITOR_CONFIG_NAMESPACE_PREFIX));
        printwriter.println(toolInfo.getEndXMLString(namespacePrefix));
        return stringwriter.toString();
    }

    public void processException(Exception exception)
    {
        String messageKey;
        String defaultMessage;
        String exceptionMessage;
        if ((exception instanceof WSIFileNotFoundException) || (exception instanceof IllegalArgumentException))
        {
            messageKey = MONITOR_ERROR_KEY;
            defaultMessage = DEFAULT_MONITOR_ERROR_MESSAGE;
        }
        else
        {
            messageKey = MONITOR_ERROR_EXCEPTIONAL_KEY;
            defaultMessage = DEFAULT_MONITOR_STOPPED_BY_EXCEPTION_MESSAGE;
        }
        exceptionMessage = exception.toString();
        printMessage(messageKey, exceptionMessage, defaultMessage);
    }

    void exitMonitor()
    {
    	printMessage(STOPPING_MONITOR, DEFAULT_STOPPING_THE_MONITOR_MESSAGE);
        //System.exit(0);
    }

    void exitMonitor(Exception exception)
    {
        //LOGGER.error(messageList.getMessage(STOPPING_MONITOR_WITH_EXCEPTION_KEY, DEFAULT_STOPPING_MONITOR_EXCEPTION_MESSAGE), exception);
        exitMonitor();
    }

    void stopMonitor()
    {
        try
        {
            for (ServerSocketListener serverSocketListener : listenerList)
            {
            	try 
            	{
            		serverSocketListener.shutdown();
            	}
            	catch (Throwable e) 
            	{
					// ignore
				}
            }
            Thread.sleep(monitorConfig.getTimeout() * MILLISECONDS_MULTIPLICATOR);
            if (logWriter != null)
            {
                logWriter.write(new StringReader(log.getEndXMLString("")));
                logWriter.close();
            }
        }
        catch (Exception exception)
        {
        }
        printMessage(MONITOR_STOPPED_KEY, DEFAULT_MONITOR_STOPPED_MESSAGE);
    }
}
