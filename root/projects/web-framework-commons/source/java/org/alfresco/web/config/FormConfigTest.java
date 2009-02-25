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

import java.util.ArrayList;
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
import org.alfresco.web.config.FormConfigElement.Mode;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service. These tests only include those that require a single config
 * xml file. Override-related tests, which use multiple config xml files, are
 * located in peer classes in this package.
 * 
 * @author Neil McErlean
 */
public class FormConfigTest extends BaseTest
{
    protected XMLConfigService configService;
    protected Config globalConfig;
    protected ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormConfigElement formConfigElement;
    protected DefaultControlsConfigElement defltCtrlsConfElement;

    protected String getConfigXmlFile()
    {
        return "test-config-forms.xml";
    }
    
    public void testFormSubmissionUrl()
    {
        assertEquals("Submission URL was incorrect.", "submission/url",
                formConfigElement.getSubmissionURL());
    }
    
    @SuppressWarnings("unchecked")
    public void testGetFormTemplatesWithoutRoles()
    {
        assertEquals("Incorrect template.","/path/create/template/norole",
        		formConfigElement.getFormTemplate(Mode.CREATE, null));
        assertEquals("Incorrect template.","/path/create/template/norole",
        		formConfigElement.getFormTemplate(Mode.CREATE, Collections.EMPTY_LIST));

        assertNull("Incorrect template.", formConfigElement.getFormTemplate(
                Mode.EDIT, null));
        assertNull("Incorrect template.", formConfigElement.getFormTemplate(
                Mode.VIEW, null));
        assertNull("Incorrect template.", formConfigElement.getFormTemplate(
                Mode.EDIT, Collections.EMPTY_LIST));
        assertNull("Incorrect template.", formConfigElement.getFormTemplate(
                Mode.VIEW, Collections.EMPTY_LIST));
    }

    public void testGetFormTemplatesWithRoles()
    {
        List<String> roles = new ArrayList<String>();
        roles.add("Consumer");
        roles.add("Manager");
        assertEquals("Incorrect template.", "/path/create/template",
                formConfigElement.getFormTemplate(Mode.CREATE, roles));
        assertEquals("Incorrect template.", "/path/edit/template/manager",
                formConfigElement.getFormTemplate(Mode.EDIT, roles));
        assertEquals("Incorrect template.", "/path/view/template",
                formConfigElement.getFormTemplate(Mode.VIEW, roles));
    }

    public void testGetFormTemplatesWithIrrelevantRoles()
    {
        List<String> roles = new ArrayList<String>();
        roles.add("Bread");
        roles.add("Jam"); // hoho
        assertEquals("Incorrect template.", "/path/create/template/norole",
                formConfigElement.getFormTemplate(Mode.CREATE, roles));
        assertEquals("Incorrect template.", null,
                formConfigElement.getFormTemplate(Mode.EDIT, roles));
        assertEquals("Incorrect template.", null,
                formConfigElement.getFormTemplate(Mode.VIEW, roles));
    }

    public void testGetFormFieldVisibilitiesForModes()
    {
        assertTrue("Field 'name' should be visible.", formConfigElement
                .isFieldVisible("name", Mode.CREATE));
        assertTrue("Field 'title' should be visible.", formConfigElement
                .isFieldVisible("title", Mode.CREATE));
        assertFalse("Field 'rubbish' should be invisible.", formConfigElement
                .isFieldVisible("rubbish", Mode.CREATE));

        assertTrue("Field 'name' should be visible.", formConfigElement
                .isFieldVisible("name", Mode.EDIT));
        assertFalse("Field 'title' should be invisible.", formConfigElement
                .isFieldVisible("title", Mode.EDIT));
        assertFalse("Field 'rubbish' should be invisible.", formConfigElement
                .isFieldVisible("rubbish", Mode.EDIT));

        assertTrue("Field 'name' should be visible.", formConfigElement
                .isFieldVisible("name", Mode.VIEW));
        assertTrue("Field 'title' should be visible.", formConfigElement
                .isFieldVisible("title", Mode.VIEW));
        assertFalse("Field 'rubbish' should be invisible.", formConfigElement
                .isFieldVisible("rubbish", Mode.VIEW));
    }
    
    public void testVisibleFieldsMustBeCorrectlyOrdered()
    {
    	List<String> fieldNames = formConfigElement.getVisibleViewFieldNames();
    	
    	List<String> expectedFieldNames = new ArrayList<String>();
    	expectedFieldNames.add("name");
    	expectedFieldNames.add("title");
    	assertEquals("Visible fields wrong.", expectedFieldNames, fieldNames);
    }

    public void testGetSetsFromForm()
    {
        Set<String> expectedSetIds = new HashSet<String>();
        expectedSetIds.add("details");
        expectedSetIds.add("user");
        assertEquals("Set IDs were wrong.", expectedSetIds, formConfigElement.getSets().keySet());

        Map<String, FormSet> sets = formConfigElement.getSets();
        assertEquals("Set parent was wrong.", "details", sets.get("user")
                .getParentId());
        assertEquals("Set parent was wrong.", null, sets.get("details")
                .getParentId());

        assertEquals("Set parent was wrong.", "fieldset", sets.get("details")
                .getAppearance());
        assertEquals("Set parent was wrong.", "panel", sets.get("user")
                .getAppearance());
    }
    
    public void testAccessAllFieldRelatedData()
    {
        // Field checks
        Map<String, FormField> fields = formConfigElement.getFields();
        assertEquals("Wrong number of Fields.", 5, fields.size());

        FormField usernameField = fields.get("username");
        assertNotNull("usernameField was null.", usernameField);
        assertTrue("Missing attribute.", usernameField.getAttributes()
                .containsKey("set"));
        assertEquals("Incorrect attribute.", "user", usernameField
                .getAttributes().get("set"));
        assertNull("username field's template should be null.", usernameField
                .getTemplate());

        FormField nameField = fields.get("name");
        String nameTemplate = nameField.getTemplate();
        assertNotNull("name field had null template", nameTemplate);
        assertEquals("name field had incorrect template.",
                "alfresco/extension/formcontrols/my-name.ftl", nameTemplate);

        List<ControlParam> controlParams = nameField.getControlParams();
        assertNotNull("name field should have control params.", controlParams);
        assertEquals("name field has incorrect number of control params.", 1,
                controlParams.size());

        ControlParam firstCP = controlParams.iterator().next();
        assertEquals("Control param has wrong name.", "foo", firstCP.getName());
        assertEquals("Control param has wrong value.", "bar", firstCP.getValue());

        assertEquals("name field had incorrect type.", "REGEX",
        		nameField.getConstraintMessages().get(0).getType());
        assertEquals("name field had incorrect message.",
                "The name can not contain the character '{0}'",
        		nameField.getConstraintMessages().get(0).getMessage());
        assertEquals("name field had incorrect message-id.",
                "field_error_name",
        		nameField.getConstraintMessages().get(0).getMessageId());
    }

    public void testControlParamSequenceThatIncludesValuelessParamsParsesCorrectly()
    {
        // Field checks
        Map<String, FormField> fields = formConfigElement.getFields();

        FormField testField = fields.get("fieldWithMixedCtrlParams");

        List<ControlParam> controlParams = testField.getControlParams();
        assertNotNull("field should have control params.", controlParams);
        assertEquals("field has incorrect number of control params.", 4,
                controlParams.size());

        List<ControlParam> expectedCPs = new ArrayList<ControlParam>();
        expectedCPs.add(new ControlParam("one", "un"));
        expectedCPs.add(new ControlParam("two", "deux"));
        expectedCPs.add(new ControlParam("three", ""));
        expectedCPs.add(new ControlParam("four", "quatre"));
        
        assertEquals(expectedCPs, controlParams);
    }
    
    public void testFormsShouldSupportMultipleConstraintMessageTags()
    {
    	FormField nameField = formConfigElement.getFields().get("name");
    	assertEquals(3, nameField.getConstraintMessages().size());
    	
    	ConstraintMessage regexField = nameField.getConstraintMessages().get(0);
    	ConstraintMessage applesField = nameField.getConstraintMessages().get(1);
    	ConstraintMessage orangesField = nameField.getConstraintMessages().get(2);

    	assertEquals("REGEX", regexField.getType());
    	assertEquals("apples", applesField.getType());
    	assertEquals("oranges", orangesField.getType());
    	
    	assertEquals("Pink Lady", applesField.getMessage());
    	assertEquals("", applesField.getMessageId());
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

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        configService = initXMLConfigService(getConfigXmlFile());
        assertNotNull("configService was null.", configService);
    
        Config contentConfig = configService.getConfig("content");
        assertNotNull("contentConfig was null.", contentConfig);
    
        ConfigElement confElement = contentConfig.getConfigElement("form");
        assertNotNull("confElement was null.", confElement);
        assertTrue("confElement should be instanceof FormConfigElement.",
                confElement instanceof FormConfigElement);
        formConfigElement = (FormConfigElement) confElement;
    
        globalConfig = configService.getGlobalConfig();
    
        globalDefaultControls = globalConfig
                .getConfigElement("default-controls");
        assertNotNull("global default-controls element should not be null",
                globalDefaultControls);
        assertTrue(
                "config element should be an instance of DefaultControlsConfigElement",
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
}
