package org.alfresco.service.cmr.search;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Meta Data associated with a result set.
 * 
 * @author Andy Hind
 */
@AlfrescoPublicApi
public interface ResultSetMetaData
{
    
    /**
     * Return how, <b>in fact</b>, the result set was limited.
     * This may not be how it was requested.
     * 
     * If a limit of 100 were requested and there were 100 or less actual results
     * this will report LimitBy.UNLIMITED.
     * 
     * @return LimitBy
     */
    public LimitBy getLimitedBy();
    
    /**
     * Return how permission evaluations are being made.
     * 
     * @return PermissionEvaluationMode
     */
    public PermissionEvaluationMode getPermissionEvaluationMode();
    
    /**
     * Get the parameters that were specified to define this search.
     * 
     * @return SearchParameters
     */
    public SearchParameters getSearchParameters();
    
    /**
     * Get the result set type
     * @return ResultSetType
     */
    public ResultSetType getResultSetType();
    
    /**
     * The selector meta-data.
     * @return - the selector meta-data.
     */
    public ResultSetSelector[] getSelectors();
    
    
    /**
     * The column meta-data.
     * @return - the column meta-data.
     */
    public ResultSetColumn[] getColumns();
    
    /**
     * Get the names of the selectors.
     * @return - the selector names.
     */
    public String[] getSelectorNames();
    
    /**
     * Get the column names.
     * @return - the names of the columns.
     */
    public String[] getColumnNames();
    
    /**
     * Get the selector meta-data by name.
     * @param name String
     * @return - the selector meta-data.
     */
    public ResultSetSelector getSelector(String name);
    
    /**
     * Get the column meta-data by column name.
     * @param name String
     * @return - the column meta-data.
     */
    public ResultSetColumn getColumn(String name);
    
    
    
}
