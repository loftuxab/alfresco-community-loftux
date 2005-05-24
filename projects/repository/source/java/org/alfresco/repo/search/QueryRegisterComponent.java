/*
 * Created on 19-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.ref.QName;

public interface QueryRegisterComponent
{
    /**
     * Get a query defintion by Qname
     * 
     * @param qName
     * @return
     */
    public CannedQueryDef getQueryDefinition(QName qName);
    
    /**
     * Get the name of the collection containing a query
     * 
     * @param qName
     * @return
     */
    public String getCollectionNameforQueryDefinition(QName qName);
    
    /**
     * Get a parameter definition
     * 
     * @param qName
     * @return
     */
    public QueryParameterDefinition getParameterDefinition(QName qName);
    
    /**
     * Get the name of the collection containing a parameter definition
     * 
     * @param qName
     * @return
     */
    public String getCollectionNameforParameterDefinition(QName qName);
    
    
    /**
     * Get a query collection by name
     * 
     * @param name
     * @return
     */
    public QueryCollection getQueryCollection(String name);
    
    
    /**
     * Load a query collection
     * 
     * @param location
     */
    public void loadQueryCollection(String location);
}
