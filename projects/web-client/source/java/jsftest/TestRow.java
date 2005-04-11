/*
 * Created on Mar 18, 2005
 */
package jsftest;

import java.util.Date;

/**
 * @author kevinr
 */
public class TestRow
{
   /**
    * Test a row bean with various data types
    */
   public TestRow(String name, int count, boolean valid, float relevance, Date created)
   {
      this.name = name;
      this.count = count;
      this.valid = valid;
      this.relevance = relevance;
      this.created = created;
   }
   
   public String getName()
   {
      return name;
   }
   
   public int getCount()
   {
      return count;
   }
   
   public boolean getValid()
   {
      return valid;
   }
   
   public float getRelevance()
   {
      return relevance;
   }

   public Date getCreated()
   {
      return created;
   }
   
   public void setCreated(Date date)
   {
      this.created = date;
   }
   
   public TestRow getObject()
   {
      return this;
   }
   
   
   private String name;
   private int count;
   private boolean valid;
   private float relevance;
   private Date created;
}
