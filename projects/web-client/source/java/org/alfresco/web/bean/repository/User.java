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
package org.alfresco.web.bean.repository;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Bean that represents the currently logged in user
 * 
 * @author gavinc
 */
public class User
{
   private String homeSpaceId;
   private String userName;
   private String ticket;
   private NodeRef person;

   /**
    * Constructor
    * 
    * @param userName constructor for the user
    */
   public User(String userName, String ticket, NodeRef person)
   {
      this.userName = userName;  
      this.ticket = ticket;
      this.person = person;
   }
   
   /**
    * @return The user name
    */
   public String getUserName()
   {
      return this.userName;
   }
   
   /**
    * @return Retrieves the user's home space (this may be the id of the company home space)
    */
   public String getHomeSpaceId()
   {
      return this.homeSpaceId;
   }

   /**
    * @param homeSpaceId Sets the id of the users home space
    */
   public void setHomeSpaceId(String homeSpaceId)
   {
      this.homeSpaceId = homeSpaceId;
   }

   /**
    * @return Returns the ticket.
    */
   public String getTicket()
   {
      return this.ticket;
   }

   /**
    * @return Returns the person.
    */
   public NodeRef getPerson()
   {
      return this.person;
   }
}
