package com.activiti.repo.domain.hibernate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.activiti.repo.domain.Node;

/**
 * Simple named node to test out various features
 * 
 * @author derekh
 * 
 */
public class NodeImpl implements Node
{
   private Long m_id;
   private Set m_parentAssocs;
   private Map m_properties;
   
   public NodeImpl()
   {
      m_parentAssocs = new HashSet(3, 0.75F);
   }
   
   public Long getId()
   {
      return m_id;
   }
   public void setId(Long id)
   {
      m_id = id;
   }
   
   public Set getParentAssocs()
   {
      return m_parentAssocs;
   }
   public void setParentAssocs(Set parentAssocs)
   {
      m_parentAssocs = parentAssocs;
   }
   
   public Map getProperties()
   {
      return m_properties;
   }
   public void setProperties(Map properties)
   {
      m_properties = properties;
   }
}
