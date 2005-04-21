package com.activiti.web.bean.repository;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Lighweight client side representation of a node held in the repository. 
 * 
 * @author gavinc
 */
public class Node implements Serializable
{ 
   private static final long serialVersionUID = 3544390322739034169L;

   private static Logger logger = Logger.getLogger(Node.class);
   
   private String type;
   private Map properties;
   
   public Node(String type)
   {
      this.type = type;
   }

   /**
    * @return Returns the type.
    */
   public String getType()
   {
      return type;
   }

   /**
    * @return Returns the properties.
    */
   public Map getProperties()
   {
      return properties;
   }

   /**
    * @param properties The properties to set.
    */
   public void setProperties(Map properties)
   {
      this.properties = properties;
   }
   
   /**
    * Used to save the properties edited by the user
    * 
    * @return The outcome string
    */
   public String persist()
   {
      logger.debug("Updating properties for: " + this + "; properties = " + this.properties);
      
      // TODO: Use whatever service to persist the Node back to the repository
      
      return "success";
   }

   /**
    * @see java.lang.Object#toString()
    */
//   public String toString()
//   {
//      StringBuilder buffer = new StringBuilder();
//      buffer.append(super.toString());
//      buffer.append(" (type=").append(this.type);
//      buffer.append(" properties=").append(this.properties).append(")");
//      return buffer.toString();
//   }
}
