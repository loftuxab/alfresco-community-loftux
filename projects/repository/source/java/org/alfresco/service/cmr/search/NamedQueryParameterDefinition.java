/*
 * Created on 23-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.service.cmr.search;

import org.alfresco.service.namespace.QName;

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
