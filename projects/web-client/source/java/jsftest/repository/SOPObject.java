package jsftest.repository;

import java.util.Date;

/**
 * Represents a custom content object type
 * 
 * @author gavinc
 */
public class SOPObject extends BaseContentObject
{
   private String sopId;
   private Date effective = new Date();
   private boolean approved;
   
   /**
    * @return The type name of this object
    */
   public String getType()
   {
      return "SOP";
   }
   
   /**
    * @return Returns the effective.
    */
   public Date getEffective()
   {
      return effective;
   }
   
   /**
    * @param effective The effective to set.
    */
   public void setEffective(Date effective)
   {
      this.effective = effective;
   }
   
   /**
    * @return Returns the sopId.
    */
   public String getSopId()
   {
      return sopId;
   }
   
   /**
    * @param sopId The sopId to set.
    */
   public void setSopId(String sopId)
   {
      this.sopId = sopId;
   }
   
   /**
    * @return Returns the approved.
    */
   public boolean isApproved()
   {
      return approved;
   }
   
   /**
    * @param approved The approved to set.
    */
   public void setApproved(boolean approved)
   {
      this.approved = approved;
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      
      buffer.append(super.toString());
      buffer.append("; SOPId: ").append(sopId);
      buffer.append("; Effective: ").append(effective);
      buffer.append("; Approved: ").append(approved);
      
      return buffer.toString();
   }
}
