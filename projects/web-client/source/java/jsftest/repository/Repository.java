package jsftest.repository;

import java.util.Map;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 * Mock repository to gain access to content objects
 * 
 * @author gavinc
 */
public class Repository
{
   private static Logger s_logger = Logger.getLogger(Repository.class);
   
   public static BaseContentObject getObject(String path)
   {
      BaseContentObject obj = null;
      
      if (path.equalsIgnoreCase("/gav.doc"))
      {
         obj = createObject("Gav", "Gavs Object", new String[] {"gav", "gadget", "gibbon"}, null);
      }
      else if (path.equalsIgnoreCase("/kev.txt"))
      {
         obj = createObject("Kev", "Kevs Object", new String[] {"kev", "monkey"}, null);
      }
      else if (path.equalsIgnoreCase("/sop.txt"))
      {
         obj = createObject("SOP", "A manufacturing SOP", new String[] {"sop", "manufacturing"}, "sop1");
      }
      
      return obj;
   }
   
   private static BaseContentObject createObject(String name, String desc, String[] keywords, String sop)
   {
      BaseContentObject obj;
      
      if (sop == null)
      {
         obj = new BaseContentObject();
      }
      else
      {
         obj = new SOPObject();
         ((SOPObject)obj).setSopId(sop);
      }

      obj.setName(name);
      obj.setDescription(desc);
      obj.setKeywords(keywords);
      
      return obj; 
   }
   
   /**
    * Used to save the properties edited by the user
    * 
    * @return The outcome string
    */
   public String updateProperties()
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      Map sessionMap = ctx.getExternalContext().getSessionMap();
      Object obj = sessionMap.get("obj");
      if (s_logger.isDebugEnabled())
      {
	      if (obj == null)
	      {
	         s_logger.debug("Could not find 'obj' in the session");
	      }
	      else
	      {
	         s_logger.debug("Updating properties for: " + obj);
	      }
      }
      
      return "success";
   }
   
   public String showProperties()
   {
      return "showproperties";
   }
}
