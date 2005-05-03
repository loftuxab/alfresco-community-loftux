package jsftest;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Object that can be used as a backing bean for components in the zoo
 * 
 * @author gavinc
 */
public class DummyBean
{
   private static Logger logger = Logger.getLogger(DummyBean.class);
   
   private String name;
   private Properties properties;
   
   public DummyBean()
   {
      this.properties = new Properties();
      this.properties.put("one", "");
      this.properties.put("two", "");
      this.properties.put("three", "");
      this.properties.put("four", "");
   }
   
   public Properties getProperties()
   {
      return this.properties;
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuilder builder = new StringBuilder(super.toString());
      builder.append(" (name=").append(this.name);
      builder.append(" properties=").append(this.properties).append(")");
      return builder.toString();
   }

   /**
    * Method to call on form submit buttons 
    */
   public void submit()
   {
      if (logger.isDebugEnabled())
         logger.debug("Submit called on DummyBean, state = " + toString());
   }
}
