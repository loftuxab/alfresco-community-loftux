package com.activiti.repo.domain.hibernate;

import com.activiti.repo.domain.ReferenceNode;

/**
 * @author derekh
 */
public class ReferenceNodeImpl extends NodeImpl implements ReferenceNode
{
   private String m_referencedPath;
   
   public String getReferencedPath()
   {
      return m_referencedPath;
   }
   public void setReferencedPath(String path)
   {
      m_referencedPath = path;
   }
}
