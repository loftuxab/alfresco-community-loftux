/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.util.BaseTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service.
 * These tests are aimed at the XML config file that has no &lt;appearance&gt; tag.
 * 
 * @author Neil McErlean
 */
public class FormConfigNegativeTest extends BaseTest
{
    private static Log logger = LogFactory.getLog(FormConfigNoAppearanceTest.class);
    private XMLConfigService configService;
    private Config globalConfig;
    private ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormConfigElement formConfigElement;
    protected DefaultControlsConfigElement defltCtrlsConfElement;
    
    public void testInvalidConfigXmlShouldProduceNullConfigElements()
    {
        configService = initXMLConfigService("test-config-forms-negative.xml");
        assertNotNull("configService was null.", configService);

        Config contentConfig = configService.getConfig("content");
        assertNotNull("contentConfig was null.", contentConfig);

        ConfigElement confElement = contentConfig.getConfigElement("form");
        assertNull("confElement should be null.", confElement);

        globalConfig = configService.getGlobalConfig();

        globalDefaultControls = globalConfig
                .getConfigElement("default-controls");
        assertNull("global default-controls element should be null",
                globalDefaultControls);

        globalConstraintHandlers = globalConfig
                .getConfigElement("constraint-handlers");
        assertNull("global constraint-handlers element should be null",
                globalConstraintHandlers);
     }
}