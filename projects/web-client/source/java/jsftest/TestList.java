/*
 * Created on Mar 18, 2005
 */
package jsftest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import org.alfresco.web.jsf.component.UIActionLink;
import org.alfresco.web.jsf.component.UIBreadcrumb;

/**
 * @author kevinr
 */
public class TestList
{
   /**
    * Constructor
    */
   public TestList()
   {
      // Create test data rows
      rows.add(new TestRow("monkey", 5, true, 0.1f, new Date(1999-1900, 1, 5)));
      rows.add(new TestRow("biscuit", 15, true, 0.2f, new Date(2000-1900, 12, 5)));
      rows.add(new TestRow("HORSEY", 23, false, 0.333f, new Date(1999-1900, 11, 15)));
      rows.add(new TestRow("thing go here", 5123, true, 0.999f, new Date(2003-1900, 11, 11)));
      rows.add(new TestRow("I like docs", -5, false, 0.333f, new Date(1999-1900, 2, 3)));
      rows.add(new TestRow("Document", 1235, false, 12.0f, new Date(2005-1900, 1, 1)));
      rows.add(new TestRow("1234567890", 52, false, 5.0f, new Date(1998-1900, 8, 8)));
      rows.add(new TestRow("space", 77, true, 17.5f, new Date(1997-1900, 9, 30)));
      rows.add(new TestRow("House", 12, true, 0.4f, new Date(2001-1900, 7, 15)));
      rows.add(new TestRow("Baboon", 14, true, -0.888f, new Date(2002-1900, 5, 28)));
      rows.add(new TestRow("Woof", 0, true, 0.0f, new Date(2003-1900, 11, 11)));
   }
   
   public List getRows()
   {
      return this.rows;
   }
   
   public void clickBreadcrumb(ActionEvent event)
   {
      if (event.getComponent() instanceof UIBreadcrumb)
      {
         s_logger.debug("clickBreadcrumb action listener called, path now: " + ((UIBreadcrumb)event.getComponent()).getValue());
      }
   }
   
   public void clickActionLink(ActionEvent event)
   {
      s_logger.debug("clickActionLink");
   }
   
   public void clickNameLink(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String value = params.get("name");
      if (value != null)
      {
         s_logger.debug("clicked item in list: " + value);
      }
   }


   private final static Logger s_logger = Logger.getLogger(TestList.class);
   
   private List rows = new ArrayList();;
}
