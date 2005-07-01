/*
 * Created on 27-Jun-2005
 */
package org.alfresco.web.bean.repository;

/**
 * Simple interface used to implement small classes capable of calculating dynamic property values
 * for MapNodes at runtime. This allows bean responsible for building large lists of MapNodes to
 * encapsulate the code needed to retrieve non-standard Node properties. The values are then
 * calculated on demand by the property resolver.
 * 
 * @author Kevin Roast
 */
public interface NodePropertyResolver
{
   /**
    * Get the property value for this resolver
    * 
    * @param node       MapNode this property is for
    * 
    * @return property value
    */
   public Object get(MapNode node);
}
