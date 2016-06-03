package org.alfresco.service.cmr.search;

/**
 * The two types of result set - column based and Node Ref based.
 * @author andyh
 *
 */
public enum ResultSetType
{
    /**
     * Just the node ref is available for each row in the results.
     */
    NODE_REF,
    /**
     * The node ref is available and a specified list of columns.
     */
    COLUMN_AND_NODE_REF
}
