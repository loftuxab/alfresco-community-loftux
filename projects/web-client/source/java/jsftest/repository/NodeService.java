package jsftest.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Mock NodeService API
 * 
 * @author gavinc
 */
public class NodeService
{
   private static Logger logger = Logger.getLogger(NodeService.class);
   
   public static NodeRef getNodeRef(String path)
   {
      return new NodeRef(path);
   }
   
   public static String getType(NodeRef nodeRef)
   {
      String id = nodeRef.getId();
      String type = null;
      
      if (id.equalsIgnoreCase("/gav.doc") || 
          id.equalsIgnoreCase("/kev.txt"))
      {
         type = "base"; 
      }
      else if (id.equalsIgnoreCase("/sop.txt"))
      {
         type = "SOP";
      }
      
      return type;
   }
   
   public static Map getProperties(NodeRef nodeRef)
   {
      String id = nodeRef.getId();
      Map properties = null;
      
      if (id.equalsIgnoreCase("/gav.doc"))
      {
         properties = createProperties("Gav", "Gavs Object", 
               new String[] {"gav", "gadget", "gibbon"}, null);
      }
      else if (id.equalsIgnoreCase("/kev.txt"))
      {
         properties = createProperties("Kev", "Kevs Object", 
              new String[] {"kev", "monkey"}, null);
      }
      else if (id.equalsIgnoreCase("/sop.txt"))
      {
         properties = createProperties("SOP", "A manufacturing SOP", 
               new String[] {"sop", "manufacturing"}, "sop1");
      }
      
      return properties;
   }

   private static Map createProperties(String name, String desc, 
         String[] keywords, String sop)
   {
      HashMap props = new HashMap();
      
      Date date = new Date();
      props.put("name", name);
      props.put("description", desc);
      props.put("keywords", keywords);
      props.put("created", date);
      props.put("modified", date);
      
      if (sop != null)
      {
         props.put("sopId", sop);
         props.put("effective", date);
         props.put("approved", new Boolean(true));
         props.put("latestversion", "1.6");
      }
      
      return props; 
   }
}
