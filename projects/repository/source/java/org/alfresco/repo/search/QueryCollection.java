/*
 * Created on 23-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

public interface QueryCollection
{
    /**
     * The name of the query collection
     * 
     * @return
     */
    public String getName();
    
    /**
     * Does this collection contain a query for the given QName?
     * @param qName
     * @return
     */
    public boolean containsQueryDefinition(QName qName);
    
    /**
     * Get a query definition by QName.
     * @param qName
     * @return
     */
    public CannedQueryDef getQueryDefinition(QName qName);
    
    /**
     * Does this collection contain a query for the given QName?
     * @param qName
     * @return
     */
    public boolean containsParameterDefinition(QName qName);
    
    /**
     * Get a query definition by QName.
     * @param qName
     * @return
     */
    public QueryParameterDefinition getParameterDefinition(QName qName);
    
    /**
     * Return the mechanism that this query definition uses to map namespace prefixes to URIs.
     * A query may use a predefined set of prefixes for known URIs.
     * I would be unwise to rely on the defaults.
     *  
     * @return
     */
    public NamespacePrefixResolver getNamespacePrefixResolver();
    
}
