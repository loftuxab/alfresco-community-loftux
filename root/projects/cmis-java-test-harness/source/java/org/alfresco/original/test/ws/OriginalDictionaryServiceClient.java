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
package org.alfresco.original.test.ws;

import javax.xml.rpc.ServiceException;

import org.alfresco.cmis.test.ws.AbstractService;
import org.alfresco.cmis.test.ws.AbstractServiceClient;
import org.alfresco.repo.webservice.dictionary.DictionaryServiceLocator;
import org.alfresco.repo.webservice.dictionary.DictionaryServiceSoapBindingStub;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Client for Dictionary Service
 * 
 * @author Mike Shavnev
 */
public class OriginalDictionaryServiceClient extends AbstractServiceClient
{
    private static Log LOGGER = LogFactory.getLog(OriginalDictionaryServiceClient.class);

    private static final String PROP_MODIFIED = "cm:modified";
    private static final String PROP_CREATOR = "cm:creator";
    private static final String ASSOC_CHILDREN = "sys:children";
    private static final String ASSOC_CONTAINS = "cm:contains";
    private static final String CLASS_CONTENT = "cm:content";
    private static final String CLASS_BASE = "sys:base";

    public OriginalDictionaryServiceClient(AbstractService abstractService)
    {
        super(abstractService);
    }

    /**
     * Gets stub for Dictionary Service
     * 
     * @param address address where service resides
     * @return DictionaryServiceSoapBindingStub
     * @throws ServiceException
     */
    private DictionaryServiceSoapBindingStub getDictionaryService(String address) throws ServiceException
    {
        DictionaryServiceSoapBindingStub dictionaryService = null;
        DictionaryServiceLocator locator = new DictionaryServiceLocator(getEngineConfiguration());
        locator.setDictionaryServiceEndpointAddress(address);
        dictionaryService = (DictionaryServiceSoapBindingStub) locator.getDictionaryService();
        dictionaryService.setMaintainSession(true);
        dictionaryService.setTimeout(TIMEOUT);
        return dictionaryService;
    }

    /**
     * Starts session for Dictionary Service client
     */
    public void initialize() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Initializing client...");
        }
        startSession();
    }

    /**
     * Invokes all methods in Dictionary Service
     */
    public void invoke() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Invoking client...");
        }
        DictionaryServiceSoapBindingStub dictionaryService = getDictionaryService(getProxyUrl() + getService().getPath());
        dictionaryService.getClasses(null, null);
        dictionaryService.getProperties(new String[] { PROP_MODIFIED, PROP_CREATOR });
        dictionaryService.getAssociations(new String[] { ASSOC_CHILDREN, ASSOC_CONTAINS });
        dictionaryService.isSubClass(CLASS_CONTENT, CLASS_BASE);
    }

    /**
     * Ends session for Dictionary Service client
     */
    public void release() throws Exception
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Releasing client...");
        }
        endSession();
    }

    /**
     * Main method to start client
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:wsi-tools-client-context.xml");
        AbstractServiceClient client = (OriginalDictionaryServiceClient) applicationContext.getBean("originalDictionaryServiceClient");
        try
        {
            client.initialize();
            client.invoke();
            client.release();
        }
        catch (Exception e)
        {
            LOGGER.error("Some error occured during client running. Exception message: " + e.getMessage());
        }
    }
}
