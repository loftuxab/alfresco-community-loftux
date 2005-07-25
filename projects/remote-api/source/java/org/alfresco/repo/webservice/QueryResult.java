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
package org.alfresco.repo.webservice;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Holds the result of a query
 * 
 * @author gavinc
 */
public class QueryResult
{
   private int hits;
   private NodeRef[] nodes;
   
   public QueryResult(NodeRef[] nodes)
   {
      this.nodes = nodes;
      this.hits = nodes.length;
   }
   
   public QueryResult(NodeRef[] nodes, int hits)
   {
      this.nodes = nodes;
      this.hits = hits;
   }

   public int getHits()
   {
      return this.hits;
   }

   public void setHits(int hits)
   {
      this.hits = hits;
   }
   
   public NodeRef getNodes(int i) 
   {
      return this.nodes[i];
   }
   
   public void setNodes(int i, NodeRef nodeRef) 
   {
      this.nodes[i] = nodeRef;
   }

   public NodeRef[] getNodes()
   {
      return this.nodes;
   }

   public void setNodes(NodeRef[] nodes)
   {
      this.nodes = nodes;
   }
}
