/*
 * Created on Mar 24, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.ref;

import java.io.Serializable;

import com.activiti.util.EqualsHelper;

/**
 * This class represents a child relationship between two nodes. This
 * relationship is named.
 * <p>
 * So it requires the parent node ref, the child node ref and the name of the
 * child within the particular parent.
 * <p>
 * This combination is not a unique identifier for the relationship with regard
 * to structure. In use this does not matter as we have no concept of order,
 * particularly in the index.
 * 
 * @author andyh
 * 
 */
public class ChildAssocRef implements EntityRef, Serializable
{
   private static final long serialVersionUID = 4051322336257127729L;

   private NodeRef parentRef;

   private QName childQName;

   private NodeRef childRef;
   
   private int nthSibling;

   /**
    * Construct a representation of a parent --- name ----> child relationship.
    * 
    * @param parentRef the parent reference
    * @param childQName the qualified name of the association
    * @param childRef the child node reference. This must not be null.
    * @param nthSibling the nth association with the same properties.  Usually -1 to be ignored.
    */
   public ChildAssocRef(NodeRef parentRef, QName childQName, NodeRef childRef, int nthSibling)
   {
      this.parentRef = parentRef;
      this.childQName = childQName;
      this.childRef = childRef;
      this.nthSibling = nthSibling;
      
      // check
      if (childRef == null)
      {
          throw new IllegalArgumentException("Child reference may not be null");
      }
   }
   
   /**
    * @see ChildAssocRef#ChildRelationshipRef(NodeRef, QName, NodeRef, int) 
    */
   public ChildAssocRef(NodeRef parentRef, QName childQName, NodeRef childRef)
   {
       this(parentRef, childQName, childRef, -1);
   }

   /**
    * Get the qualified name of the parent-child association
    * 
    * @return
    */
   public QName getName()
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
   
   /**
    * 
    * @return Returns the nth sibling required
    */
   public int getNthSibling()
   {
       return nthSibling;
   }

   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (!(o instanceof ChildAssocRef))
      {
         return false;
      }
      ChildAssocRef other = (ChildAssocRef) o;

      return (EqualsHelper.nullSafeEquals(this.parentRef, other.parentRef)
              && EqualsHelper.nullSafeEquals(this.childQName, other.childQName)
              && EqualsHelper.nullSafeEquals(this.childRef, other.childRef)
              && this.nthSibling == other.nthSibling);
   }

   public int hashCode()
   {
      int hashCode = getParentRef().hashCode();
      hashCode = 37 * hashCode + getName().hashCode();
      hashCode = 37 * hashCode + getChildRef().hashCode();
      return hashCode;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getParentRef());
      buffer.append(" --- ").append(getName()).append(" ---> ");
      buffer.append(getChildRef());
      return buffer.toString();
   }
}
