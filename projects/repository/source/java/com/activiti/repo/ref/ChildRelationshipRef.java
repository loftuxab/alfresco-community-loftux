/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.ref;

import java.io.Serializable;

/**
 * This class represents a child relationship between two nodes. This
 * relationship is named.
 * 
 * So it requires the parent node ref, the child node ref and the name of the
 * child within the particular parent.
 * 
 * This combination is not a unique identifier for the relationship with regard
 * to structure. In use this does not matter as we have no concept of order,
 * particularly in the index.
 * 
 * We could add child position to resolve this.
 * 
 * @author andyh
 * 
 */
public class ChildRelationshipRef implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 4051322336257127729L;

   private NodeRef parentRef;

   private QName childQName;

   private NodeRef childRef;

   /**
    * Construct a representation of a parent --- name ----> child relationship.
    * 
    * @param parentRef
    * 
    * The parent reference. This could be null if we are making the root node.
    * 
    * @param childName
    * 
    * The child name. Must be non null, length greater then zero and following a
    * name convention. This should be enforced elsewhere.
    * 
    * We mandate:
    * <OL>
    * <LI> not null,
    * <LI> greater then zero length,
    * <LI> and can not contain '/'.
    * </OL>
    * 
    * @param childRef
    * 
    * The child reference. This must no be null.
    */

   public ChildRelationshipRef(NodeRef parentRef, QName childQName, NodeRef childRef)
   {
      this.parentRef = parentRef;
      this.childQName = childQName;
      this.childRef = childRef;
   }

   /**
    * Get the name of the child within the parent
    * 
    * @return
    */
   public QName getChildName()
   {
      return childQName;
   }

   /**
    * Get the child node reference;
    * 
    * @return
    */
   public NodeRef getChildRef()
   {
      return childRef;
   }

   /**
    * Get the parent node reference
    * 
    * @return
    */
   public NodeRef getParentRef()
   {
      return parentRef;
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof ChildRelationshipRef))
      {
         return false;
      }
      ChildRelationshipRef other = (ChildRelationshipRef) o;

      return getParentRef().equals(other.getParentRef()) && getChildName().equals(other.getChildName())
            && getChildRef().equals(other.getChildRef());
   }

   public int hashCode()
   {
      int hashCode = getParentRef().hashCode();
      hashCode = 37 * hashCode + getChildName().hashCode();
      hashCode = 37 * hashCode + getChildRef().hashCode();
      return hashCode;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getParentRef().toString());
      buffer.append(" --- ").append(getChildName()).append(" ---> ");
      buffer.append(getChildRef().toString());
      return buffer.toString();
   }

}
