package org.alfresco.repo.dictionary.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import junit.framework.TestCase;


public class M2ModelTest extends TestCase 
{

    public void testCreate()
        throws Exception
    {
       M2Model model = M2Model.createModel(new FileInputStream("model.xml"));
       assertNotNull(model);
       assertEquals("alf:model", model.getName());
    }
    
    public void testToXML()
        throws Exception
    {
       M2Model model = M2Model.createModel(new FileInputStream("model.xml"));
       assertNotNull(model);
       assertEquals("alf:model", model.getName());
       model.setName("alf:modifiedmodel");
       ByteArrayOutputStream os = new ByteArrayOutputStream();
       model.toXML(os);
       String strModel = os.toString();
       System.out.println(strModel);
    }
    
    public void testManualCreate()
    {
        M2Model model = M2Model.createModel("alf:manualmodel");
        model.setAuthor("TestCase");
        M2Type type = model.createType("alf:manualtype");
        M2Property prop = type.createProperty("alf:manualprop");
        prop.setType("alf:manualproptype");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.toXML(os);
        String strModel = os.toString();
        System.out.println(strModel);
    }
}
