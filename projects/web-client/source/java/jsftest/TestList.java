/*
 * Created on Mar 18, 2005
 */
package jsftest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
      rows.add(new TestRow("monkey", 5, true, 0.1f, new Date(1999, 1, 5)));
      rows.add(new TestRow("biscuit", 15, true, 0.2f, new Date(2000, 12, 5)));
      rows.add(new TestRow("HORSEY", 23, false, 0.333f, new Date(1999, 11, 15)));
      rows.add(new TestRow("thing go here", 5123, true, 0.999f, new Date(2003, 11, 11)));
      rows.add(new TestRow("I like docs", -5, false, 0.333f, new Date(1999, 2, 3)));
      rows.add(new TestRow("Bored with Documents", 1235, false, 12.0f, new Date(2005, 1, 1)));
      rows.add(new TestRow("1234567890", 52, false, 5.0f, new Date(1998, 8, 8)));
      rows.add(new TestRow("space", 77, true, 17.5f, new Date(1997, 9, 30)));
      rows.add(new TestRow("House", 12, true, 0.4f, new Date(2001, 7, 15)));
      rows.add(new TestRow("Baboon", 14, true, -0.888f, new Date(2002, 5, 28)));
      rows.add(new TestRow("Woof", 0, true, 0.0f, new Date(2003, 11, 11)));
   }
   
   public List getRows()
   {
      return this.rows;
   }

   private List rows = new ArrayList();;
}
