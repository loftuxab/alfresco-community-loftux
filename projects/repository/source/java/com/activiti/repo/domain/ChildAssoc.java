package com.activiti.repo.domain;

/**
 * @author derekh
 */
public interface ChildAssoc
{
   public long getId();
   public void setId(long id);

   public void buildAssociation(ContainerNode parentNode, Node childNode);
   
   public ContainerNode getParent();
   public void setParent(ContainerNode node);
   
   public Node getChild();
   public void setChild(Node node);
   
   public String getName();
   public void setName(String name);
   
   public boolean getIsPrimary();
   public void setIsPrimary(boolean isPrimary);
}
