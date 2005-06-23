/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
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
