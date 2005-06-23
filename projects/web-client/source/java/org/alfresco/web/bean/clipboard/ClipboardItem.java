/*
 * Created on 01-Jun-2005
 */
package org.alfresco.web.bean.clipboard;

import org.alfresco.web.bean.repository.Node;

/**
 * @author Kevin Roast
 */
public class ClipboardItem
{
   /**
    * Constructor
    * 
    * @param node       The node on the clipboard
    * @param mode       The ClipboardStatus enum value
    */
   public ClipboardItem(Node node, ClipboardStatus mode)
   {
      this.Node = node;
      this.Mode = mode;
   }
   
   /**
    * Override equals() to compare NodeRefs
    */
   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof ClipboardItem)
      {
         return ((ClipboardItem)obj).Node.getNodeRef().equals(Node.getNodeRef());
      }
      else
      {
         return false;
      }
   }
   
   /**
    * Override hashCode() to use the internal NodeRef hashcode instead
    */
   public int hashCode()
   {
      return Node.getNodeRef().hashCode();
   }
   
   
   public Node Node;
   public ClipboardStatus Mode;
}
