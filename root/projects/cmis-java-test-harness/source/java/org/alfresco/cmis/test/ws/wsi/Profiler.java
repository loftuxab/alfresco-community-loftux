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
package org.alfresco.cmis.test.ws.wsi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.cmis.test.ws.AbstractServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.wsi.test.analyzer.Analyzer;
import org.wsi.test.analyzer.BasicProfileAnalyzer;
import org.wsi.test.analyzer.config.AnalyzerConfig;
import org.wsi.test.analyzer.config.WSDLElement;
import org.wsi.test.analyzer.config.impl.WSDLElementImpl;
import org.wsi.test.analyzer.config.impl.WSDLReferenceImpl;
import org.wsi.test.monitor.Monitor;
import org.wsi.test.monitor.config.MonitorConfig;
import org.wsi.util.WSIProperties;

/**
 * @author Mike Shavnev
 */
public class Profiler
{
    private static final int SUCCELLFUL_VALIDATION_STATUS_VALUE = 1;

    private static final String FULL_SERVICE_NAME_PATTERN = "{%s}%s";
    private static final String REPORT_FILE_NAME_PATTERN = "%s%s%d-%s-%s";

    private static final String SCHEMA_LOCATION_PATH = "config/schemas/";

    private static final String WSDL_MIME_SCHEMA_LOCATION = SCHEMA_LOCATION_PATH + "wsdlMime.xsd";
    private static final String WSDL_SOAP_SCHEMA_LOCATION = SCHEMA_LOCATION_PATH + "wsdlSoap.xsd";
    private static final String WSDL11_SCHEMA_LOCATION = SCHEMA_LOCATION_PATH + "wsdl11.xsd";
    private static final String SOAP_ENVELOPE_SCHEMA_LOCATION = SCHEMA_LOCATION_PATH + "soapEnvelope.xsd";
    private static final String XMLSCHEMA_LOCATION = SCHEMA_LOCATION_PATH + "XMLSchema.xsd";

    private static final String LOG_FILE_PREFIX = "messages-log";
    private static final String LOG_FIL_EXTENSION = ".xml";

    private static final String WSI_MON_CONFIG_NAMESPACE_PREFIX = "wsi-monConfig";
    private static final String WSI_ANALYZER_CONFIG_NAMESPACE_PREFIX = "wsi-analyzerConfig";
    private static final String WSDL_ELEMENT_TYPE = "port";

    private static Log LOGGER = LogFactory.getLog(Profiler.class);

    private String reportLocation;
    private MonitorConfig monitorConfig;
    private AnalyzerConfig analyzerConfig;

    private Resource[] tadResources;

    private Resource[] xslResources;

    private List<AbstractServiceClient> clientList;

    public void setReportLocation(String reportLocation)
    {
        File reportLocationFile = new File(reportLocation);
        if (!reportLocationFile.exists())
        {
            reportLocationFile.mkdirs();
        }
        this.reportLocation = reportLocation;
    }

    public String getReportLocation()
    {
        return reportLocation;
    }

    public void setMonitorConfig(MonitorConfig monitorConfig)
    {
        this.monitorConfig = monitorConfig;
    }

    public MonitorConfig getMonitorConfig()
    {
        return monitorConfig;
    }

    public void setAnalyzerConfig(AnalyzerConfig analyzerConfig)
    {
        this.analyzerConfig = analyzerConfig;
    }

    public AnalyzerConfig getAnalyzerConfig()
    {
        return analyzerConfig;
    }

    public void setTadResources(Resource[] tadResources)
    {
        this.tadResources = tadResources;
    }

    public void setXslResources(Resource[] xslResources)
    {
        this.xslResources = xslResources;
    }

    public void setClientList(List<AbstractServiceClient> clientList)
    {
        this.clientList = clientList;
    }

    public List<AbstractServiceClient> getClientList()
    {
        return clientList;
    }

    public void execute()
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("WsiProfiler initializing...");
            LOGGER.info(tadResources.length + " TAD profile(s) and " + clientList.size() + " service client(s) were configured. Expected reports amount: " + tadResources.length
                    * clientList.size());
        }

        try
        {
            File logFile = File.createTempFile(LOG_FILE_PREFIX, LOG_FIL_EXTENSION);
            logFile.deleteOnExit();

            String logFileLocation = logFile.getAbsolutePath();
            monitorConfig.setLogLocation(logFileLocation);
            analyzerConfig.setLogLocation(logFileLocation);

            if (LOGGER.isInfoEnabled())
            {
                LOGGER.info("Log file location: " + "\"" + logFileLocation + "\"");
            }
        }
        catch (Exception e)
        {
            if (LOGGER.isInfoEnabled())
            {
                LOGGER.fatal("Log file create was failed! Cause message: " + e);
            }

            System.exit(0);
        }

        int reportNumber = 0;
        Monitor monitor = null;

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Monitor configuration:\n" + monitorConfig.toXMLString(WSI_MON_CONFIG_NAMESPACE_PREFIX));
        }

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("WsiProfiler conformance analyzing started...");
        }

        for (AbstractServiceClient profilerClient : clientList)
        {
            String serviceName = String.format(FULL_SERVICE_NAME_PATTERN, profilerClient.getService().getNamespace(), profilerClient.getService().getParentElementName());

            try
            {
                profilerClient.initialize();

                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("Starting Monitor tool...");
                }

                monitor = new Monitor(monitorConfig);

                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("Monitor tool was started successfully!");
                }

                profilerClient.invoke();
                profilerClient.release();
            }
            catch (Exception e)
            {
                LOGGER.warn("Monitor or " + profilerClient.getClass().getName() + " client execution failed. Exception message: " + e);

                continue;
            }
            finally
            {
                if (monitor != null)
                {
                    if (LOGGER.isInfoEnabled())
                    {
                        LOGGER.info("Stopping the monitor...");
                    }

                    try
                    {
                        monitor.shutdown();

                        if (LOGGER.isInfoEnabled())
                        {
                            LOGGER.info("Monitor stopped");
                        }
                    }
                    catch (Throwable th)
                    {
                        if (LOGGER.isInfoEnabled())
                        {
                            LOGGER.info("Some error occured during monitor stopping. Exception message: " + th);
                        }
                    }
                }
            }

            for (Resource tadResource : tadResources)
            {
                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("Analyzer initializing...");
                }

                File tadFile;
                try
                {
                    if (LOGGER.isInfoEnabled())
                    {
                        LOGGER.info("\"" + tadResource.getFilename() + "\" TAD file was received normally");
                    }

                    File tempTadFile = new File("./" + tadResource.getFilename());
                    copySingleFileEntry(Channels.newChannel(tadResource.getInputStream()), Channels.newChannel(new FileOutputStream(tempTadFile.getAbsolutePath())));
                    tempTadFile.deleteOnExit();
                    tadFile = tempTadFile;
                }
                catch (Exception e)
                {
                    LOGGER.warn("\"" + tadResource.getFilename()
                            + "\" TAD file resource receiving failed. Conformance analyzing for this TAD file was skipped. Exception message: " + e);

                    continue;
                }

                WSDLElement wsdlElement = new WSDLElementImpl();
                wsdlElement.setType(WSDL_ELEMENT_TYPE);
                wsdlElement.setName(profilerClient.getService().getName());
                wsdlElement.setNamespace(profilerClient.getService().getNamespace());
                wsdlElement.setParentElementName(profilerClient.getService().getParentElementName());

                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("Current client configured against \"" + serviceName + "\" service. Target port name: " + profilerClient.getService().getName());
                }

                String wsdlUri = profilerClient.getServerUrl() + profilerClient.getService().getWsdlUri();
                String wsdlLocation = profilerClient.getServerUrl() + profilerClient.getService().getPath();
                analyzerConfig.setWSDLReference(new WSDLReferenceImpl(wsdlElement, wsdlUri, wsdlLocation));

                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("Current WSDL URI: \"" + wsdlUri + "\", current service: \"" + wsdlLocation + "\"");
                }

                analyzerConfig.setTestAssertionsDocumentLocation(tadFile.getAbsolutePath());
                analyzerConfig.setReportLocation(String.format(REPORT_FILE_NAME_PATTERN, reportLocation, File.separator, reportNumber++, profilerClient.getService().getName(),
                        tadFile.getName()));

                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info("Report file name that will be generated: \"" + analyzerConfig.getReportLocation() + "\"");
                }

                List<AnalyzerConfig> analyzerConfigList = new ArrayList<AnalyzerConfig>();
                analyzerConfigList.add(analyzerConfig);
                try
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Analyzer configuration:\n" + analyzerConfig.toXMLString(WSI_ANALYZER_CONFIG_NAMESPACE_PREFIX));
                    }

                    Analyzer analyzer = new BasicProfileAnalyzer(analyzerConfigList);

                    if (LOGGER.isInfoEnabled())
                    {
                        LOGGER.info("Conformance Analyzer tool was started...");
                    }

                    if (analyzer.validateConformance() == SUCCELLFUL_VALIDATION_STATUS_VALUE)
                    {
                        if (LOGGER.isInfoEnabled())
                        {
                            LOGGER.info("\"" + analyzerConfig.getReportLocation() + "\" report was successfully generated!");
                        }
                    }
                    else
                    {
                        LOGGER.warn("Report generation was finished with some troubles. See log messages or generated report");
                    }
                }
                catch (Exception e)
                {
                    if (!(new File(analyzerConfig.getReportLocation()).exists()))
                    {
                        reportNumber--;
                    }

                    LOGGER.warn("Conrformance analyzing for \"" + tadResource.getFilename() + "\" was failed. Exception message: " + e);

                    continue;
                }
            }
        }
        if (!clientList.isEmpty() && (tadResources.length > 0) && (reportNumber > 0))
        {
            try
            {
                copyStylesheet();
            }
            catch (Exception e)
            {
                LOGGER.error("Report related style sheets files copying was failed. Exception message: " + e);
            }

            if (LOGGER.isInfoEnabled())
            {
                LOGGER.info(reportNumber + " report(s) was/were actually generated");
            }
        }
        else
        {
            LOGGER
                    .warn("No one report file was generated! Check WsiProfiler configuration, TAD files in <WsiProfiler_root_folder>/config/profiles, services clients configuration and messages log");
        }
    }

    private void copyStylesheet() throws IOException
    {
        for (int i = 0; i < xslResources.length; i++)
        {
            File file = new File(reportLocation + File.separator + xslResources[i].getFilename());
            if (!file.exists())
            {
                final ReadableByteChannel inputChannel = Channels.newChannel(xslResources[i].getInputStream());
                final WritableByteChannel outputChannel = Channels.newChannel(new FileOutputStream(file));
                copySingleFileEntry(inputChannel, outputChannel);
            }
        }
    }

    private void copySingleFileEntry(ReadableByteChannel inputChannel, WritableByteChannel outputChannel) throws IOException
    {
        try
        {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
            while (inputChannel.read(buffer) != -1)
            {
                buffer.flip();
                outputChannel.write(buffer);
                buffer.compact();
            }
            buffer.flip();
            while (buffer.hasRemaining())
            {
                outputChannel.write(buffer);
            }
        }
        finally
        {
            if (inputChannel != null)
                inputChannel.close();
            if (outputChannel != null)
                outputChannel.close();
        }
    }

    public static void main(String[] args)
    {
        System.setProperty(WSIProperties.PROP_WSDL_MIME_SCHEMA, XMLSCHEMA_LOCATION);
        System.setProperty(WSIProperties.PROP_SOAP_SCHEMA, SOAP_ENVELOPE_SCHEMA_LOCATION);
        System.setProperty(WSIProperties.PROP_WSDL_SCHEMA, WSDL11_SCHEMA_LOCATION);
        System.setProperty(WSIProperties.PROP_WSDL_SOAP_SCHEMA, WSDL_SOAP_SCHEMA_LOCATION);
        System.setProperty(WSIProperties.PROP_WSDL_MIME_SCHEMA, WSDL_MIME_SCHEMA_LOCATION);

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-context.xml");
        Profiler profiler = (Profiler) applicationContext.getBean("profiler");
        profiler.execute();
    }
}
