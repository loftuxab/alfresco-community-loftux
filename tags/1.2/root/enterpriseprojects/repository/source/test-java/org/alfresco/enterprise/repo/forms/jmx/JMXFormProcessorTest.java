/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.enterprise.repo.forms.jmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.alfresco.repo.forms.FieldDefinition;
import org.alfresco.repo.forms.Form;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.FormService;
import org.alfresco.repo.forms.Item;
import org.alfresco.repo.forms.PropertyFieldDefinition;
import org.alfresco.repo.forms.FormData.FieldData;
import org.alfresco.util.BaseAlfrescoSpringTest;

public class JMXFormProcessorTest extends BaseAlfrescoSpringTest
{
    private FormService formService;
    private MBeanServerConnection mbeanServer; 
    
    private static final String MBEAN_FORM_ITEM_KIND = "mbean";
    private static final String ITEM_OUTBOUND_EMAIL = "Alfresco:Type=Configuration,Category=email,id1=outbound";
    private static final String ITEM_RUNTIME = "Alfresco:Name=Runtime";
    private static final String ITEM_PDF2WSF = "Alfresco:Name=ContentTransformer,Type=pdf2swf";
    
    /**
     * Called during the transaction setup
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Get the required services
        this.formService = (FormService)this.applicationContext.getBean("FormService");
        this.mbeanServer = (MBeanServerConnection)this.applicationContext.getBean("alfrescoMBeanServer");
        
        // set admin as the current user
        authenticationComponent.setCurrentUser("admin");
    }
    
    public void testDefaultFormGeneration() throws Exception
    {
        Form form = this.formService.getForm(new Item(MBEAN_FORM_ITEM_KIND, ITEM_OUTBOUND_EMAIL));
        
        // check a form got returned
        assertNotNull("Expecting form to be present", form);
        
        // get the fields into a Map
        Collection<FieldDefinition> fieldDefs = form.getFieldDefinitions();
        Map<String, FieldDefinition> fieldDefMap = new HashMap<String, FieldDefinition>(fieldDefs.size());
        for (FieldDefinition fieldDef : fieldDefs)
        {
            fieldDefMap.put(fieldDef.getName(), fieldDef);
        }
        
        // get the current mail.host value
        String currentHostName = (String)this.mbeanServer.getAttribute(
                    new ObjectName(ITEM_OUTBOUND_EMAIL), "mail.host");
        
        // check the mail.host field
        PropertyFieldDefinition hostField = (PropertyFieldDefinition)fieldDefMap.get("mail.host");
        assertNotNull(hostField);
        assertEquals("mail.host", hostField.getName());
        assertEquals("mail.host", hostField.getLabel());
        assertNull(hostField.getDescription());
        assertEquals("text", hostField.getDataType());
        FieldData hostFieldData = form.getFormData().getFieldData(hostField.getDataKeyName());
        assertNotNull(hostFieldData);
        assertEquals(currentHostName, hostFieldData.getValue());
        
        // test a boolean field
        PropertyFieldDefinition authField = (PropertyFieldDefinition)fieldDefMap.get("mail.smtp.auth");
        assertNotNull(authField);
        assertEquals("mail.smtp.auth", authField.getName());
        assertEquals("mail.smtp.auth", authField.getLabel());
        assertNull(authField.getDescription());
        assertEquals("text", authField.getDataType());
        FieldData authFieldData = form.getFormData().getFieldData(authField.getDataKeyName());
        assertNotNull(authFieldData);
        assertEquals("false", authFieldData.getValue());
        
        // test the operations field
        PropertyFieldDefinition opsField = (PropertyFieldDefinition)fieldDefMap.get("mbean_operations");
        assertNotNull(opsField);
        assertEquals("mbean_operations", opsField.getName());
        assertEquals("mbean_operations", opsField.getLabel());
        assertNull(opsField.getDescription());
        assertEquals("mbean_operations", opsField.getDataType());
        FieldData opsFieldData = form.getFormData().getFieldData(opsField.getDataKeyName());
        assertNotNull(opsFieldData);
        
        assertTrue("operations does not contain Start operation", ((String)opsFieldData.getValue()).contains("start|Start"));
        assertTrue("operations does not contain sendTestMessage operation", ((String)opsFieldData.getValue()).contains("sendTestMessage|sendTestMessage"));
    }
    
    public void testNullAttributeValues() throws Exception
    {
        Form form = this.formService.getForm(new Item(MBEAN_FORM_ITEM_KIND, ITEM_PDF2WSF));
        assertNotNull(form);
    }
    
    public void testSelectedFieldsFormGeneration() throws Exception
    {
        // request a certain set of fields inc. ops
        List<String> fields = new ArrayList<String>(8);
        fields.add("FreeMemory");
        fields.add("TotalMemory");
        
        Form form = this.formService.getForm(new Item(MBEAN_FORM_ITEM_KIND, ITEM_RUNTIME), fields);
        
        // check a form got returned
        assertNotNull("Expecting form to be present", form);
        
        // get the fields into a Map
        Collection<FieldDefinition> fieldDefs = form.getFieldDefinitions();
        Map<String, FieldDefinition> fieldDefMap = new HashMap<String, FieldDefinition>(fieldDefs.size());
        for (FieldDefinition fieldDef : fieldDefs)
        {
            fieldDefMap.put(fieldDef.getName(), fieldDef);
        }
        
        // check there are 2 field
        assertEquals(2, fieldDefMap.size());
        
        // check the 2 fields are the correct ones!
        PropertyFieldDefinition freeMemField = (PropertyFieldDefinition)fieldDefMap.get("FreeMemory");
        assertNotNull(freeMemField);
        PropertyFieldDefinition totalMemField = (PropertyFieldDefinition)fieldDefMap.get("TotalMemory");
        assertNotNull(totalMemField);
    }
    
    public void testFormPersistence() throws Exception
    {
        // create FormData object containing the values to update
        FormData data = new FormData();
        
        String newFromDefault = "test@alfresco.com";
        data.addFieldData("prop_mail.from.default", newFromDefault);
        
        String newPort = "26";
        data.addFieldData("prop_mail#dot#port", newPort);
        
        data.addFieldData("prop_mail#dot#smtp#dot#auth", "true");
        
        // try invalid property names
        data.addFieldData("-", "");
        data.addFieldData("mail.wrong", "wrong");
        
        // persist the data
        this.formService.saveForm(new Item(MBEAN_FORM_ITEM_KIND, ITEM_OUTBOUND_EMAIL), data);
        
        // now go direct to the mbean and check values
        ObjectName objectName = new ObjectName(ITEM_OUTBOUND_EMAIL);
        MBeanInfo mbean = this.mbeanServer.getMBeanInfo(objectName);
        assertNotNull(mbean);
        
        String from = (String)this.mbeanServer.getAttribute(objectName, "mail.from.default");
        assertNotNull(from);
        assertEquals(newFromDefault, from);
        
        String port = (String)this.mbeanServer.getAttribute(objectName, "mail.port");
        assertNotNull(port);
        assertEquals(newPort, port);
        
        String auth = (String)this.mbeanServer.getAttribute(objectName, "mail.smtp.auth");
        assertNotNull(auth);
        assertEquals("true", auth);
        
        String wrong = (String)this.mbeanServer.getAttribute(objectName, "mail.wrong");
        assertNull(wrong);
    }
}
