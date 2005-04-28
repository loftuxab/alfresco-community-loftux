package jsftest;

import java.util.Properties;

/**
 * Object that can be used as a backing bean for components in the zoo
 * 
 * @author gavinc
 */
public class DummyBean
{
   private String name;
   private Properties properties;
   
   public DummyBean()
   {
      this.properties = new Properties();
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
}
