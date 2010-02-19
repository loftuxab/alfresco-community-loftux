/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.wef;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * JUnit class for testing the WEF Spring context and plugin mechanism.
 *
 * @author Gavin Cornwell
 */
public class WEFTest extends TestCase
{
    private ApplicationContext appContext;
    private WEFPluginRegistry pluginRegistry;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        this.appContext = new ClassPathXmlApplicationContext("classpath:alfresco/wef-context.xml");
        this.pluginRegistry = (WEFPluginRegistry)this.appContext.getBean(WEFPluginRegistry.BEAN_NAME);
        assertNotNull(this.pluginRegistry);
    }
    
    /**
     * Tests setup and registration.
     */
    public void testRegistration()
    { 
        // get all the registered plugins
        List<WEFPlugin> plugins = this.pluginRegistry.getPlugins();
        assertNotNull(plugins);
        assertEquals(3, plugins.size());
        
        // get and check the application plugin
        WEFPlugin app = plugins.get(0);
        assertEquals("application.wef", app.getName());
        assertEquals("The application", app.getDescription());
        assertEquals("/service/wef/application-resource", app.getPath());
        assertEquals("application", app.getType());
        assertNull(app.getContainer());
        List<WEFResource> appDependencies = app.getDependencies();
        assertEquals(2, appDependencies.size());
        
        // get and check the forms plugin and it's dependencies
        WEFPlugin formsPlugin = plugins.get(1);
        assertEquals("com.alfresco.forms", formsPlugin.getName());
        assertEquals("Plugin providing forms capabilities", formsPlugin.getDescription());
        assertEquals("/components/form/form-min.js", formsPlugin.getPath());
        assertEquals("plugin", formsPlugin.getType());
        assertNull(formsPlugin.getContainer());
        List<WEFResource> formsDependencies = formsPlugin.getDependencies();
        assertEquals(8, formsDependencies.size());
        
        // get and check the AWE plugin and it's dependencies
        WEFPlugin awePlugin = plugins.get(2);
        assertEquals("com.alfresco.awe", awePlugin.getName());
        assertEquals("Plugin providing in-context editing capabilities", awePlugin.getDescription());
        assertEquals("/js/awe.js", awePlugin.getPath());
        assertEquals("plugin", awePlugin.getType());
        assertNull(awePlugin.getContainer());
        List<WEFResource> aweDependencies = awePlugin.getDependencies();
        assertEquals(2, aweDependencies.size());
        
        // test retrieval of individual plugins
        WEFPlugin plugin = pluginRegistry.getPlugin("com.alfresco.forms");
        assertNotNull(plugin);
        assertTrue(plugin instanceof WEFPlugin);
        assertEquals("com.alfresco.forms", plugin.getName());
        
        plugin = pluginRegistry.getPlugin("com.alfresco.awe");
        assertNotNull(plugin);
        assertTrue(plugin instanceof WEFPlugin);
        assertEquals("com.alfresco.awe", plugin.getName());
        
        plugin = pluginRegistry.getPlugin("application.wef");
        assertNotNull(plugin);
        assertTrue(plugin instanceof WEFApplication);
        assertEquals("application.wef", plugin.getName());
        
        // get and check the WEF application plugin
        List<WEFApplication> apps = this.pluginRegistry.getApplications();
        assertNotNull(apps);
        assertEquals(1, apps.size());
        app = apps.get(0);
        assertEquals("application.wef", app.getName());
        assertEquals("The application", app.getDescription());
        assertEquals("/service/wef/application-resource", app.getPath());
        assertEquals("application", app.getType());
        assertNull(app.getContainer());
        appDependencies = app.getDependencies();
        assertEquals(2, appDependencies.size());
    }
    
    /**
     * Tests the retrieval of all resources for all applications.
     */
    public void testResources()
    {
        // get list of all resources
        List<WEFResource> resources = this.pluginRegistry.getPluginResources();
        assertNotNull(resources);
        assertEquals(14, resources.size());
        assertEquals("com.alfresco.forms.runtime", resources.get(0).getName());
        assertEquals("com.alfresco.forms.css", resources.get(1).getName());
        assertEquals("com.alfresco.forms.control.date-picker", resources.get(2).getName());
        assertEquals("com.alfresco.forms.control.period", resources.get(3).getName());
        assertEquals("com.alfresco.forms.control.object-finder", resources.get(4).getName());
        assertEquals("com.alfresco.forms.control.object-finder-css", resources.get(5).getName());
        assertEquals("com.moxiecode.tinymce", resources.get(6).getName());
        assertEquals("com.alfresco.editors.tinymce", resources.get(7).getName());
        assertEquals("com.alfresco.forms.control.rich-text", resources.get(8).getName());
        assertEquals("com.alfresco.forms.control.content", resources.get(9).getName());
        assertEquals("com.alfresco.forms", resources.get(10).getName());
        assertEquals("com.alfresco.awe.reset-css", resources.get(11).getName());
        assertEquals("com.alfresco.awe", resources.get(12).getName());
        assertEquals("application.wef", resources.get(13).getName());
        
        // get the application itself and ensure the dependencies are the plugins
        WEFApplication app = this.pluginRegistry.getApplication("application.wef");
        assertNotNull(app);
        List<WEFResource> plugins = app.getDependencies();
        assertNotNull(plugins);
        assertEquals(2, plugins.size());
        assertEquals("com.alfresco.forms", plugins.get(0).getName());
        assertEquals("com.alfresco.awe", plugins.get(1).getName());
    }
}
