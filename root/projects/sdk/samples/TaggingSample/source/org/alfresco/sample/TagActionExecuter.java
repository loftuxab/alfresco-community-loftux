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
package org.alfresco.sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Tag action executer.
 * 
 * This action adds the tag:taggable aspect to a node.
 * 
 * @author gavinc
 */
public class TagActionExecuter extends ActionExecuterAbstractBase
{
   /** The name of the action */
   public static final String NAME = "tag";

   /** The parameter names */
   public static final String PARAM_TAGS = "tags";

   /**
    * The node service
    */
   private NodeService nodeService;
   
    /**
     * Sets the node service
     * 
     * @param nodeService   the node service
     */
   public void setNodeService(NodeService nodeService) 
   {
      this.nodeService = nodeService;
   }
   
   /**
    * This action will take the comma separated list of tags and add them
    * separately to the tags property after applying the taggable aspect.
    * 
    * If no tags are supplied the aspect is still applied.
    */
   @Override
   protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
   {
      if (this.nodeService.exists(actionedUponNodeRef) == true)
      {
         // add the aspect if it is not already present on the node
         QName tagAspect = QName.createQName("extension.tags", "taggable");
         if (this.nodeService.hasAspect(actionedUponNodeRef, tagAspect) == false)
         {
            this.nodeService.addAspect(actionedUponNodeRef, tagAspect, null);
         }
         
         // create the tags as a list
         String tags = (String)action.getParameterValue(PARAM_TAGS);
         List<String> tagsList = new ArrayList<String>();
         if (tags != null && tags.length() > 0)
         {
            StringTokenizer tokenizer = new StringTokenizer(tags, ",");
            while (tokenizer.hasMoreTokens())
            {
               tagsList.add(tokenizer.nextToken().trim());
            }
         }
         
         // set the tags property
         QName tagsProp = QName.createQName("extension.tags", "tags");
         this.nodeService.setProperty(actionedUponNodeRef, tagsProp, (Serializable)tagsList);
      }
  }
   
  /**
   * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
   */
   @Override
   protected void addParameterDefinitions(List<ParameterDefinition> paramList)
   {
      // Specify the parameters
      paramList.add(new ParameterDefinitionImpl(PARAM_TAGS,
            DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_TAGS)));
   }
}
