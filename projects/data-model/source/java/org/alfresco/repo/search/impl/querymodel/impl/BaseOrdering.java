package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.repo.search.impl.querymodel.Column;
import org.alfresco.repo.search.impl.querymodel.Order;
import org.alfresco.repo.search.impl.querymodel.Ordering;

/**
 * @author andyh
 *
 */
public class BaseOrdering implements Ordering
{
    private Column column;
    
    private Order order;
    
    public BaseOrdering(Column column, Order order)
    {
        this.column = column;
        this.order = order;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Ordering#getColumn()
     */
    public Column getColumn()
    {
       return column;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Ordering#getOrder()
     */
    public Order getOrder()
    {
       return order;
    }
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseOrdering[");
        builder.append("Column=" + getColumn()).append(", ");
        builder.append("Order=" + getOrder());
        builder.append("]");
        return builder.toString();
    }

}
