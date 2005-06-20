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
