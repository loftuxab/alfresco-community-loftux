package org.alfresco.repo.search.impl.querymodel;

/**
 * @author andyh
 *
 */
public interface Ordering
{
    public Column getColumn();
    
    public Order getOrder();
}
