package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.ChildAssoc;
import com.activiti.repo.domain.ContainerNode;
import com.activiti.repo.domain.Node;

/**
 * @author derekh
 */
public class ChildAssocImpl implements ChildAssoc
{
   private long m_id;
   private ContainerNode m_parentNode;
   private Node m_child;
   private String m_name;
   private boolean m_isPrimary;
   
   public long getId()
   {
      return m_id;
   }
   public void setId(long id)
   {
      m_id = id;
   }
   
   public void buildAssociation(ContainerNode parentNode, Node childNode)
   {
      // add the forward associations
      this.setParent(parentNode);
      this.setChild(childNode);
      // add the inverse associations
      parentNode.getChildAssocs().add(this);
      childNode.getParentAssocs().add(this);
   }
   
   public ContainerNode getParent()
   {
      return m_parentNode;
   }
   public void setParent(ContainerNode parentNode)
   {
      m_parentNode = parentNode;
   }
   
   public Node getChild()
   {
      return m_child;
   }
   public void setChild(Node node)
   {
      m_child = node;
   }
   
   public String getName()
   {
      return m_name;
   }
   public void setName(String name)
   {
      m_name = name;
   }
   
   public boolean getIsPrimary()
   {
      return m_isPrimary;
   }
   public void setIsPrimary(boolean isPrimary)
   {
      m_isPrimary = isPrimary;
   }
}
