package jsftest.repository;

import java.io.Serializable;

/**
 * Mock NodeRef object that comes from the Mock NodeService API.
 * 
 * @author gavinc
 */
public class NodeRef implements Serializable
{
   private static final long serialVersionUID = 3833183614468175153L;

   private String id;
   
   public NodeRef(String id)
   {
      this.id = id;
   }

   /**
    * @return Returns the id.
    */
   public String getId()
   {
      return id;
   }
}
