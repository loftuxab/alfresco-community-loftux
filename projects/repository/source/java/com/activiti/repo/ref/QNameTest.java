package com.activiti.repo.ref;

import junit.framework.TestCase;

/**
 * @see com.activiti.repo.ref.QName
 * 
 * @author David Caruana
 */
public class QNameTest extends TestCase
{

   public QNameTest(String name)
   {
      super(name);
   }

   public void testInvalidQName() throws Exception
   {
      try
      {
         QName qname = QName.createQName("");
         fail("Missing local name was not caught");
      }
      catch (InvalidQNameException e)
      {
      }

      try
      {
         QName qname = QName.createQName("invalid{}name");
         fail("Namespace not at start was not caught");
      }
      catch (InvalidQNameException e)
      {
      }

      try
      {
         QName qname = QName.createQName("{name");
         fail("Missing closing namespace token was not caught");
      }
      catch (InvalidQNameException e)
      {
      }

      try
      {
         QName qname = QName.createQName("{}");
         fail("Missing local name after namespace was not caught");
      }
      catch (InvalidQNameException e)
      {
      }

      try
      {
         QName qname = QName.createQName("{}name");
      }
      catch (InvalidQNameException e)
      {
         fail("Empty namespace is valid");
      }

      try
      {
         QName qname = QName.createQName("{namespace}name");
         assertEquals("namespace", qname.getNamespaceURI());
         assertEquals("name", qname.getLocalName());
      }
      catch (InvalidQNameException e)
      {
         fail("Valid namespace has been thrown out");
      }

      try
      {
         QName qname = QName.createQName((String)null, (String)null);
         fail("Null name was not caught");
      }
      catch (InvalidQNameException e)
      {
      }

      try
      {
         QName qname = QName.createQName((String)null, "");
         fail("Empty name was not caught");
      }
      catch (InvalidQNameException e)
      {
      }
   }

   
   public void testConstruction()
   {
      QName qname1 = QName.createQName("namespace1", "name1");
      assertEquals("namespace1", qname1.getNamespaceURI());
      assertEquals("name1", qname1.getLocalName());
      
      QName qname2 = QName.createQName("{namespace2}name2");
      assertEquals("namespace2", qname2.getNamespaceURI());
      assertEquals("name2", qname2.getLocalName());
      
      QName qname3 = QName.createQName(null, "name3");
      assertEquals("", qname3.getNamespaceURI());
      
      QName qname4 = QName.createQName("", "name4");
      assertEquals("", qname4.getNamespaceURI());

      QName qname5 = QName.createQName("{}name5");
      assertEquals("", qname5.getNamespaceURI());

      QName qname6 = QName.createQName("name6");
      assertEquals("", qname6.getNamespaceURI());
   }

   
   public void testStringRepresentation()
   {
      QName qname1 = QName.createQName("namespace", "name1");
      assertEquals("{namespace}name1", qname1.toString());
      
      QName qname2 = QName.createQName("", "name2");
      assertEquals("{}name2", qname2.toString());
      
      QName qname3 = QName.createQName("{namespace}name3");
      assertEquals("{namespace}name3", qname3.toString());

      QName qname4 = QName.createQName("{}name4");
      assertEquals("{}name4", qname4.toString());

      QName qname5 = QName.createQName("name5");
      assertEquals("{}name5", qname5.toString());
   }
   
   
   public void testEquality()
   {
      QName qname1 = QName.createQName("namespace", "name");
      QName qname2 = QName.createQName("namespace", "name");
      QName qname3 = QName.createQName("{namespace}name");
      assertEquals(qname1, qname2);
      assertEquals(qname1, qname3);
      assertEquals(qname1.hashCode(), qname2.hashCode());
      assertEquals(qname1.hashCode(), qname3.hashCode());

      QName qname4 = QName.createQName("", "name");
      QName qname5 = QName.createQName("", "name");
      QName qname6 = QName.createQName(null, "name");
      assertEquals(qname4, qname5);
      assertEquals(qname4, qname6);
      assertEquals(qname4.hashCode(), qname5.hashCode());
      assertEquals(qname4.hashCode(), qname6.hashCode());
    
      QName qname7 = QName.createQName("namespace", "name");
      QName qname8 = QName.createQName("namespace", "differentname");
      assertFalse(qname7.equals(qname8));
      assertFalse(qname7.hashCode() == qname8.hashCode());
      
      QName qname9 = QName.createQName("namespace", "name");
      QName qname10 = QName.createQName("differentnamespace", "name");
      assertFalse(qname9.equals(qname10));
      assertFalse(qname9.hashCode() == qname10.hashCode());
   }
    
    
}
