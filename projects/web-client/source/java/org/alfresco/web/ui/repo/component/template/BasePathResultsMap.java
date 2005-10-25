/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.repo.component.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.web.bean.repository.Repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A special Map that executes an XPath against the parent Node as part of the get()
 * Map interface implementation.
 * 
 * @author Kevin Roast
 */
public abstract class BasePathResultsMap extends HashMap implements Cloneable
{
   protected static Log logger = LogFactory.getLog(BasePathResultsMap.class);
   protected NodeRef parent;
   protected NodeService nodeService;
   private ServiceRegistry services = null;
   
   /**
    * Constructor
    * 
    * @param nodeService   The NodeService to use
    * @param parent        The parent NodeRef to execute searches from 
    */
   public BasePathResultsMap(NodeService nodeService, NodeRef parent)
   {
      super(1, 1.0f);
      this.nodeService = nodeService;
      this.parent = parent;
   }
   
   /**
    * @see java.util.Map#get(java.lang.Object)
    */
   public abstract Object get(Object key);
   
   protected List<TemplateNode> getChildrenByXPath(String xpath, boolean firstOnly)
   {
      List<TemplateNode> result = null;
      
      if (xpath.length() != 0)
      {
         if (logger.isDebugEnabled())
            logger.debug("Executing xpath: " + xpath);
         
         List<NodeRef> nodes = getServiceRegistry().getSearchService().selectNodes(
               this.parent,
               xpath,
               null,
               getServiceRegistry().getNamespaceService(),
               false);
         
         // see if we only want the first result
         if (firstOnly == true)
         {
            if (nodes.size() != 0)
            {
               result = new ArrayList<TemplateNode>(1);
               result.add(new TemplateNode(nodes.get(0), this.nodeService));
            }
         }
         // or all the results
         else
         {
            result = new ArrayList<TemplateNode>(nodes.size()); 
            for (NodeRef ref : nodes)
            {
               result.add(new TemplateNode(ref, this.nodeService));
            }
         }
      }
      
      return result != null ? result : new ArrayList<TemplateNode>(0);
   }
   
   /**
    * @return The Service Registry
    */
   protected ServiceRegistry getServiceRegistry()
   {
      if (services == null)
      {
         services = Repository.getServiceRegistry(FacesContext.getCurrentInstance());
      }
      return services;
   }
}
