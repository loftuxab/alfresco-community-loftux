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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;
import org.alfresco.config.xml.XMLConfigService;
import org.alfresco.util.BaseTest;

/**
 * JUnit tests to exercise the forms-related capabilities in the web client
 * config service. These tests only include those that require a single config
 * xml file. Override-related tests, which use multiple config xml files, are
 * located in peer classes in this package.
 * 
 * @author Neil McErlean
 */
public class FormConfigBasicTest extends BaseTest
{
    private static final String TEST_CONFIG_FORMS_BASIC_XML = "test-config-forms-basic.xml";
	protected XMLConfigService configService;
    protected Config globalConfig;
    protected ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormsConfigElement myExampleFormsConfigElement;
    protected FormConfigElement myExampleDefaultForm;
    protected FormsConfigElement noAppearanceFormsConfigElement;
    protected FormConfigElement noAppearanceDefaultForm;
    protected FormsConfigElement noVisibilityFormsConfigElement;
    protected FormConfigElement noVisibilityDefaultForm;
    protected DefaultControlsConfigElement defltCtrlsConfElement;

    protected List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add(TEST_CONFIG_FORMS_BASIC_XML);
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
     * This method returns a List<String> containing the 3 expected templates for
     * respectively view, edit and create mode.
     * @return
     */
    protected List<String> getExpectedTemplatesForNoAppearanceDefaultForm()
    {
        return Arrays.asList(new String[]{"/view/template", "/edit/template", "/create/template"});
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
    
        ConfigElement myExampleFormsConfigObj = myExampleConfigObj.getConfigElement("forms");
        assertNotNull(myExampleFormsConfigObj);
        assertTrue("formsConfigObj should be instanceof FormsConfigElement.",
                myExampleFormsConfigObj instanceof FormsConfigElement);
        myExampleFormsConfigElement = (FormsConfigElement) myExampleFormsConfigObj;
        myExampleDefaultForm = myExampleFormsConfigElement.getDefaultForm();
        assertNotNull(myExampleDefaultForm);
        
        Config noAppearanceConfigObj = configService.getConfig("no-appearance");
        assertNotNull(noAppearanceConfigObj);
    
        ConfigElement noAppearanceFormsConfigObj = noAppearanceConfigObj.getConfigElement("forms");
        assertNotNull(noAppearanceFormsConfigObj);
        assertTrue("noAppearanceFormsConfigObj should be instanceof FormsConfigElement.",
        		noAppearanceFormsConfigObj instanceof FormsConfigElement);
        noAppearanceFormsConfigElement = (FormsConfigElement) noAppearanceFormsConfigObj;
        noAppearanceDefaultForm = noAppearanceFormsConfigElement.getDefaultForm();
        assertNotNull(noAppearanceDefaultForm);
        
        Config noVisibilityConfigObj = configService.getConfig("no-visibility");
        assertNotNull(noVisibilityConfigObj);
    
        ConfigElement noVisibilityFormsConfigObj = noVisibilityConfigObj.getConfigElement("forms");
        assertNotNull(noVisibilityFormsConfigObj);
        assertTrue("noVisibilityFormsConfigObj should be instanceof FormsConfigElement.",
        		noVisibilityFormsConfigObj instanceof FormsConfigElement);
        noVisibilityFormsConfigElement = (FormsConfigElement) noVisibilityFormsConfigObj;
        noVisibilityDefaultForm = noVisibilityFormsConfigElement.getDefaultForm();
        assertNotNull(noVisibilityDefaultForm);
        
        globalConfig = configService.getGlobalConfig();
    
        FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");
        globalDefaultControls = globalForms.getDefaultControls();
        assertNotNull("global default-controls element should not be null",
                globalDefaultControls);
        assertTrue("config element should be an instance of DefaultControlsConfigElement",
                (globalDefaultControls instanceof DefaultControlsConfigElement));
        defltCtrlsConfElement = (DefaultControlsConfigElement) globalDefaultControls;
    
        globalConstraintHandlers = globalForms.getConstraintHandlers();
        assertNotNull("global constraint-handlers element should not be null",
                globalConstraintHandlers);
        assertTrue(
                "config element should be an instance of ConstraintHandlersConfigElement",
                (globalConstraintHandlers instanceof ConstraintHandlersConfigElement));
    }
    
    public void testGetDefaultFormElement() throws Exception
    {
    	FormConfigElement defaultFormCE = myExampleFormsConfigElement.getDefaultForm();
    	assertNotNull(defaultFormCE);
    	
    	assertEquals("submit/default/form", defaultFormCE.getSubmissionURL());
    	assertNull(defaultFormCE.getId());
    }

    public void testGetNonexistentDefaultFormElement() throws Exception
    {
        Config noDefaultConfigObj = configService.getConfig("no-default-form");
        assertNotNull(noDefaultConfigObj);
    
        ConfigElement noDefaultFormsConfigObj = noDefaultConfigObj.getConfigElement("forms");
        assertNotNull(noDefaultFormsConfigObj);
        assertTrue("noDefaultFormsConfigObj should be instanceof FormsConfigElement.",
                noDefaultFormsConfigObj instanceof FormsConfigElement);
        FormsConfigElement noDefaultFormsConfigElement = (FormsConfigElement) noDefaultFormsConfigObj;
        FormConfigElement noDefaultForm = noDefaultFormsConfigElement.getDefaultForm();
        assertNull(noDefaultForm);
    }

    public void testGetFormElementById() throws Exception
    {
    	FormConfigElement formCE = myExampleFormsConfigElement.getForm("id");
    	assertNotNull(formCE);

    	assertEquals("submit/id/form", formCE.getSubmissionURL());
    	assertEquals("id", formCE.getId());
    }

    public void testGetNonexistentFormElementById() throws Exception
    {
    	FormConfigElement noSuchFormCE = myExampleFormsConfigElement.getForm("rubbish");
    	assertNull(noSuchFormCE);
    }

    public void testFormSubmissionUrl()
    {
        assertEquals("Submission URL was incorrect.", "submit/default/form",
                myExampleDefaultForm.getSubmissionURL());
    }
    
    public void testGetFormTemplatesForViewEditCreate() throws Exception
    {
        FormConfigElement testForm = noAppearanceFormsConfigElement.getDefaultForm();
        assertEquals(getExpectedTemplatesForNoAppearanceDefaultForm().get(0), testForm.getViewTemplate());
        assertEquals(getExpectedTemplatesForNoAppearanceDefaultForm().get(1), testForm.getEditTemplate());
        assertEquals(getExpectedTemplatesForNoAppearanceDefaultForm().get(2), testForm.getCreateTemplate());
    }
    
    public void testGlobalConstraintHandlers()
    {
        assertEquals(ConstraintHandlersConfigElement.class, globalConstraintHandlers.getClass());
        ConstraintHandlersConfigElement constraintHandlers
                = (ConstraintHandlersConfigElement)globalConstraintHandlers;
        
        Map<String, ConstraintHandlerDefinition> constraintItems = constraintHandlers.getItems();
        assertEquals("Incorrect count for global constraint-handlers.",
                3, constraintItems.size());
        
        Set<String> expectedTypeNames = new HashSet<String>();
        expectedTypeNames.add("MANDATORY");
        expectedTypeNames.add("REGEX");
        expectedTypeNames.add("NUMERIC");
        assertEquals("Incorrect global constraint-handler types.", expectedTypeNames,
                constraintItems.keySet());
        
        ConstraintHandlerDefinition mandatoryItem = constraintItems.get("MANDATORY");
        assertNotNull(mandatoryItem);
        ConstraintHandlerDefinition regexItem = constraintItems.get("REGEX");
        assertNotNull(regexItem);
        ConstraintHandlerDefinition numericItem = constraintItems.get("NUMERIC");
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
        
        Map<String, Control> defCtrlItems = defaultControls.getItems();
        assertEquals("Incorrect count for global default-controls.",
                6, defCtrlItems.size());
        
        Set<String> expectedTypeNames = new HashSet<String>();
        expectedTypeNames.add("d:long");
        expectedTypeNames.add("d:text");
        expectedTypeNames.add("d:test");
        expectedTypeNames.add("d:boolean");
        expectedTypeNames.add("association");
        expectedTypeNames.add("abc");
        assertEquals("Incorrect global default-control types.", expectedTypeNames,
                defCtrlItems.keySet());
        
        Control longItem = defCtrlItems.get("d:long");
        assertNotNull(longItem);
        Control textItem = defCtrlItems.get("d:text");
        assertNotNull(textItem);
        Control testItem = defCtrlItems.get("d:test");
        assertNotNull(testItem);
        
        assertEquals("/form-controls/mytextfield.ftl", longItem.getTemplate());
        assertEquals("/form-controls/mytextfield.ftl", textItem.getTemplate());
        assertEquals("/form-controls/test.ftl", testItem.getTemplate());
        
        assertEquals(Collections.emptyList(), longItem.getParams());
        assertEquals(getExpectedControlParamsForDText(),
                textItem.getParams());
        assertEquals(getExpectedControlParamsForDTest(),
                testItem.getParams());
    }

    /*
     * The datatypes and idioms used to access control-params at the global default-control
     * level and at the individual field level should be consistent.
     */
    public void testControlParamsAreConsistentBetweenGlobalAndFieldLevel()
    {
        DefaultControlsConfigElement defaultControls
                = (DefaultControlsConfigElement)globalDefaultControls;
        
        Map<String, Control> defCtrlItems = defaultControls.getItems();
        List<ControlParam> controlParamsGlobal = defCtrlItems.get("d:test").getParams();
        
        List<ControlParam> controlParamsField = myExampleDefaultForm.getFields().get("my:text").getControl().getParams();
        
        // The simple fact that the above code compiles and runs is enough to ensure
        // that the APIs are consistent. But here's an assert to dissuade changes.
        assertEquals(controlParamsGlobal.getClass(), controlParamsField.getClass());
    }
    
    public void testFormConfigElementShouldHaveNoChildren()
    {
        try
        {
            myExampleDefaultForm.getChildren();
            fail("getChildren() did not throw an exception.");
        } catch (ConfigException expectedException)
        {
            // intentionally empty
        }
    }
    
    public void testEmptyConstraintsMsgs()
    {
        // check the messages on the cm:name field
        FormField field = myExampleDefaultForm.getFields().get("cm:name");
        assertNotNull("Expecting cm:name to be present", field);
        Map<String, ConstraintHandlerDefinition> constraints = field.getConstraintDefinitionMap();
        assertNotNull(constraints);
        ConstraintHandlerDefinition constraint = constraints.get("REGEX");
        assertNotNull(constraint);
        assertNull(constraint.getMessageId());
        assertEquals("You can't have these characters in a name: /*", constraint.getMessage());
        
        // check the messages on the cm:text field
        field = myExampleDefaultForm.getFields().get("my:text");
        assertNotNull("Expecting cm:text to be present", field);
        constraints = field.getConstraintDefinitionMap();
        assertNotNull(constraints);
        constraint = constraints.get("REGEX");
        assertNotNull(constraint);
        assertNull(constraint.getMessage());
        assertEquals("custom_msg", constraint.getMessageId());
    }
    
    public void testFieldsVisibleInViewModeShouldStillBeVisibleWithNoAppearanceTag()
    {
        List<String> fieldNames = noAppearanceDefaultForm.getVisibleViewFieldNames();
        
        // The order specified in the config XML should also be preserved.
        List<String> expectedFieldNames = new ArrayList<String>();
        expectedFieldNames.add("cm:name");
        expectedFieldNames.add("cm:title");
        expectedFieldNames.add("cm:description");
        expectedFieldNames.add("cm:content");
        expectedFieldNames.add("my:text");
        expectedFieldNames.add("my:mltext");
        expectedFieldNames.add("my:date");
        
        assertEquals("Visible fields wrong.", expectedFieldNames, fieldNames);
    }

    public void testGetFormFieldVisibilitiesForModes()
    {
        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:name", Mode.CREATE));
        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:title", Mode.CREATE));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("rubbish", Mode.CREATE));

        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:name", Mode.EDIT));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("cm:title", Mode.EDIT));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("rubbish", Mode.EDIT));

        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:name", Mode.VIEW));
        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:title", Mode.VIEW));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("rubbish", Mode.VIEW));
    }

    public void testGetForcedFields()
    {
        List<String> forcedFields = noAppearanceDefaultForm.getForcedFields();
        assertEquals("Expecting one forced field", 1, forcedFields.size());

        assertTrue("Expected cm:name to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:name"));
        assertFalse("Expected cm:title not to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:title"));
    }

    public void testGetVisibleFieldsForFormWithoutFieldVisibilityReturnsNull()
    {
        assertEquals(null, noVisibilityDefaultForm.getVisibleCreateFieldNames());
        assertEquals(null, noVisibilityDefaultForm.getVisibleEditFieldNames());
        assertEquals(null, noVisibilityDefaultForm.getVisibleViewFieldNames());
    }
    
    public void testFieldVisibilityForTwoCombinedFormTags()
    {
        FormConfigElement combinedConfig = (FormConfigElement)myExampleDefaultForm.combine(noVisibilityFormsConfigElement.getDefaultForm());
        
        Set<String> expectedFields = new LinkedHashSet<String>();
        expectedFields.add("cm:name");
        expectedFields.add("my:text");
        expectedFields.add("my:mltext");
        expectedFields.add("my:date");
        expectedFields.add("my:duplicate");
        expectedFields.add("my:int");
        expectedFields.add("my:broken");
        
        assertEquals(new ArrayList<String>(expectedFields), myExampleDefaultForm.getVisibleCreateFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), myExampleDefaultForm.getVisibleEditFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), myExampleDefaultForm.getVisibleViewFieldNames());

        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleCreateFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleEditFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleViewFieldNames());
    }
    
    /**
     * This test case should test the overriding of a constraint handler from the default
     * (or form) level, to a single field and on to an overridden field.
     * 
     * @throws Exception
     */
    public void testConstraintHandlerOnField() throws Exception
    {
    	// The default or form level constraint handler
    	ConstraintHandlersConfigElement defaultConstraintHandlers
    	    = (ConstraintHandlersConfigElement)globalConstraintHandlers;
    	Map<String, ConstraintHandlerDefinition> handlers = defaultConstraintHandlers.getItems();
    	
    	ConstraintHandlerDefinition regexConstraintHandler = handlers.get("REGEX");
    	assertNotNull(regexConstraintHandler);
    	
    	assertEquals("REGEX", regexConstraintHandler.getType());
    	assertEquals("Alfresco.forms.validation.regexMatch", regexConstraintHandler.getValidationHandler());
    	assertNull(regexConstraintHandler.getMessage());
    	assertNull(regexConstraintHandler.getMessageId());
    	assertNull(regexConstraintHandler.getEvent());
    	
    	//TODO Currently if we define a constraint-handler on a field which overrides 
    	// the default (form-level) constraint, the properties of the constraint-handler
    	// are only those of the field-level constraint. The form-level default one is
    	// not inherited.

    	ConstraintHandlerDefinition regexFieldConstr
    	    = myExampleDefaultForm.getFields().get("cm:name").getConstraintDefinitionMap().get("REGEX");
    	assertNotNull(regexFieldConstr);
    	assertEquals("REGEX", regexFieldConstr.getType());
    	assertEquals("Alfresco.forms.validation.regexMatch", regexFieldConstr.getValidationHandler());
    	assertEquals("You can't have these characters in a name: /*", regexFieldConstr.getMessage());
    	assertNull(regexFieldConstr.getMessageId());
    	assertNull(regexFieldConstr.getEvent());
    	
    	// We also need to support multiple constraint-handlers on a single field.
    	ConstraintHandlerDefinition numericFieldConstr
    	    = myExampleDefaultForm.getFields().get("cm:name").getConstraintDefinitionMap().get("NUMERIC");
    	assertNotNull(numericFieldConstr);
    }

    /**
     * This test checks that the expected JS and CSS resources are available.
     */
    public void testGetDependencies() throws Exception
    {
        FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");
        
        DependenciesConfigElement depsCE = globalForms.getDependencies();
        assertNotNull(depsCE);

        // We want the dependencies as arrays as these are more JS-friendly than
        // Lists, but I'll compare the expected values as Lists.
        String[] expectedCssDependencies = new String[]{"/css/path/1", "/css/path/2"};
        String[] expectedJsDependencies = new String[]{"/js/path/1", "/js/path/2"};

        assertEquals(Arrays.asList(expectedCssDependencies), Arrays.asList(depsCE.getCss()));
        assertEquals(Arrays.asList(expectedJsDependencies), Arrays.asList(depsCE.getJs()));
    }
}
