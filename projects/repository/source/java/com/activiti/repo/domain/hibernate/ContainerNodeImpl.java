package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Set;
import com.activiti.repo.domain.ContainerNode;

/**
 * @author derekh
 */
public class ContainerNodeImpl extends RealNodeImpl implements ContainerNode
{
   private Set m_childAssocs;
   
   public ContainerNodeImpl()
   {
      m_childAssocs = new HashSet(3, 0.75F);
   }
   
   public Set getChildAssocs()
   {
      return m_childAssocs;
   }
   public void setChildAssocs(Set childAssocs)
   {
      m_childAssocs = childAssocs;
   }
}
