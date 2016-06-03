package org.alfresco.service.cmr.search;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.namespace.QName;

@AlfrescoPublicApi
public interface NamedQueryParameterDefinition 
{

    /**
     * Get the name of this parameter. It could be used as the well known name for the parameter.
     * 
     * Not null
     * 
     * @return QName
     */
    public QName getQName();
    
    /**
     * Get the query parameter definition
     * @return QueryParameterDefinition
     */
    public QueryParameterDefinition getQueryParameterDefinition();
}
