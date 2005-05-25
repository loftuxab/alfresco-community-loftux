package org.alfresco.web.bean.repository;

/**
 * Bean that represents the currently logged in user
 * 
 * @author gavinc
 */
public class User
{
   private Node homeSpace;
   private String homeSpaceId;
   private String userName;

   /**
    * Constructor
    * 
    * @param userName constructor for the user
    */
   public User(String userName)
   {
      this.userName = userName;  
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
    * @return The user name
    */
   public String getUserName()
   {
      return this.userName;
   }
}
