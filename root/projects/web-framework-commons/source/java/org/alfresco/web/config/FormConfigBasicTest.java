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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.util.BaseTest;
import org.alfresco.web.config.ConstraintHandlersConfigElement.ItemDefinition;
import org.alfresco.web.config.DefaultControlsConfigElement.DefaultControl;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service. These tests only include those that require a single config
 * xml file. Override-related tests, which use multiple config xml files, are
 * located in peer classes in this package.
 * 
 * @author Neil McErlean
 */
public class FormConfigBasicTest extends BaseTest
{
    protected XMLConfigService configService;
    protected Config globalConfig;
    protected ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormConfigElement formConfigElement;
    protected DefaultControlsConfigElement defltCtrlsConfElement;

    protected List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add("test-config-forms-basic.xml");
        return result;
    }
    
    protected String getExpectedMessageForNumericConstraint()
    {
        return "Test Message";
    }
    
    protected List<ControlParam> getExpectedControlParamsForDText()
    {
        return Arrays.asList(new ControlParam("size", "50"));
    }
    
    protected List<ControlParam> getExpectedControlParamsForDTest()
    {
        return Arrays.asList(new ControlParam("a", "Hello"), new ControlParam("b", null));
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        configService = initXMLConfigService(getConfigFiles());
        assertNotNull("configService was null.", configService);
    
        Config myExampleConfigObj = configService.getConfig("my:example");
        assertNotNull(myExampleConfigObj);
    
        ConfigElement formConfigObj = myExampleConfigObj.getConfigElement("form");
        assertNotNull(formConfigObj);
        assertTrue("formConfigObj should be instanceof FormConfigElement.",
                formConfigObj instanceof FormConfigElement);
        formConfigElement = (FormConfigElement) formConfigObj;
    
        globalConfig = configService.getGlobalConfig();
    
        globalDefaultControls = globalConfig.getConfigElement("default-controls");
        assertNotNull("global default-controls element should not be null",
                globalDefaultControls);
        assertTrue("config element should be an instance of DefaultControlsConfigElement",
                (globalDefaultControls instanceof DefaultControlsConfigElement));
        defltCtrlsConfElement = (DefaultControlsConfigElement) globalDefaultControls;
    
        globalConstraintHandlers = globalConfig
                .getConfigElement("constraint-handlers");
        assertNotNull("global constraint-handlers element should not be null",
                globalConstraintHandlers);
        assertTrue(
                "config element should be an instance of ConstraintHandlersConfigElement",
                (globalConstraintHandlers instanceof ConstraintHandlersConfigElement));
    }

    public void testFormSubmissionUrl()
    {
        assertEquals("Submission URL was incorrect.", "submission/url",
                formConfigElement.getSubmissionURL());
    }
    
    public void testGlobalConstraintHandlers()
    {
        assertEquals(ConstraintHandlersConfigElement.class, globalConstraintHandlers.getClass());
        ConstraintHandlersConfigElement constraintHandlers
                = (ConstraintHandlersConfigElement)globalConstraintHandlers;
        
        Map<String, ItemDefinition> constraintItems = constraintHandlers.getItems();
        assertEquals("Incorrect count for global constraint-handlers.",
                3, constraintItems.size());
        
        Set<String> expectedTypeNames = new HashSet<String>();
        expectedTypeNames.add("MANDATORY");
        expectedTypeNames.add("REGEX");
        expectedTypeNames.add("NUMERIC");
        assertEquals("Incorrect global constraint-handler types.", expectedTypeNames,
                constraintItems.keySet());
        
        ItemDefinition mandatoryItem = constraintItems.get("MANDATORY");
        assertNotNull(mandatoryItem);
        ItemDefinition regexItem = constraintItems.get("REGEX");
        assertNotNull(regexItem);
        ItemDefinition numericItem = constraintItems.get("NUMERIC");
        assertNotNull(numericItem);
        
        assertEquals("Alfresco.forms.validation.mandatory", mandatoryItem.getValidationHandler());
        assertEquals("Alfresco.forms.validation.regexMatch", regexItem.getValidationHandler());
        assertEquals("Alfresco.forms.validation.numericMatch", numericItem.getValidationHandler());
        
        assertEquals("blur", mandatoryItem.getEvent());
        assertEquals(null, regexItem.getEvent());
        assertEquals(null, numericItem.getEvent());

        assertEquals(null, mandatoryItem.getMessage());
        assertEquals(null, regexItem.getMessage());
        assertEquals(getExpectedMessageForNumericConstraint(), numericItem.getMessage());

        assertEquals(null, mandatoryItem.getMessageId());
        assertEquals(null, regexItem.getMessageId());
        assertEquals("regex_error", numericItem.getMessageId());
    }

    public void testGlobalDefaultControls()
    {
        assertEquals(DefaultControlsConfigElement.class, globalDefaultControls.getClass());

        DefaultControlsConfigElement defaultControls
                = (DefaultControlsConfigElement)globalDefaultControls;
        
        Map<String, DefaultControl> defCtrlItems = defaultControls.getItems();
        assertEquals("Incorrect count for global default-controls.",
                3, defCtrlItems.size());
        
        Set<String> expectedTypeNames = new HashSet<String>();
        expectedTypeNames.add("d:long");
        expectedTypeNames.add("d:text");
        expectedTypeNames.add("d:test");
        assertEquals("Incorrect global default-control types.", expectedTypeNames,
                defCtrlItems.keySet());
        
        DefaultControl longItem = defCtrlItems.get("d:long");
        assertNotNull(longItem);
        DefaultControl textItem = defCtrlItems.get("d:text");
        assertNotNull(textItem);
        DefaultControl testItem = defCtrlItems.get("d:test");
        assertNotNull(testItem);
        
        assertEquals("/form-controls/mytextfield.ftl", longItem.getTemplate());
        assertEquals("/form-controls/mytextfield.ftl", textItem.getTemplate());
        assertEquals("/form-controls/test.ftl", testItem.getTemplate());
        
        assertEquals(Collections.emptyList(), longItem.getControlParams());
        assertEquals(getExpectedControlParamsForDText(),
                textItem.getControlParams());
        assertEquals(getExpectedControlParamsForDTest(),
                testItem.getControlParams());
    }

    /*
     * The datatypes and idioms used to access control-params at the global default-control
     * level and at the individual field level should be consistent.
     */
    public void testControlParamsAreConsistentBetweenGlobalAndFieldLevel()
    {
        DefaultControlsConfigElement defaultControls
                = (DefaultControlsConfigElement)globalDefaultControls;
        
        Map<String, DefaultControl> defCtrlItems = defaultControls.getItems();
        List<ControlParam> controlParamsGlobal = defCtrlItems.get("d:test").getControlParams();
        
        List<ControlParam> controlParamsField = formConfigElement.getFields().get("my:text").getControlParams();
        
        // The simple fact that the above code compiles and runs is enough to ensure
        // that the APIs are consistent. But here's an assert to dissuade changes.
        assertEquals(controlParamsGlobal.getClass(), controlParamsField.getClass());
    }
    
    public void testFormConfigElementShouldHaveNoChildren()
    {
        try
        {
            formConfigElement.getChildren();
            fail("getChildren() did not throw an exception.");
        } catch (ConfigException expectedException)
        {
            // intentionally empty
        }
    }
}
