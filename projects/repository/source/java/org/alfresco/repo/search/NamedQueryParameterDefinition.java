/*
 * Created on 23-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.ref.QName;

public interface NamedQueryParameterDefinition 
{

    /**
     * Get the name of this parameter. It could be used as the well known name for the parameter.
     * 
     * Not null
     * 
     * @return
     */
    public QName getQName();
    
    /**
     * Get the query parameter definition
     * @return
     */
    public QueryParameterDefinition getQueryParameterDefinition();
}
