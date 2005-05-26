/*
 * Created on 19-May-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.Collection;
import java.util.Map;

import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;

/**
 * The definition of a canned query
 * 
 * @author andyh
 * 
 */
public interface CannedQueryDef
{
    /**
     * Get the unique name for the query
     * 
     * @return
     */
    public QName getQname();

    /**
     * Get the language in which the query is defined.
     * 
     * @return
     */
    public String getLanguage();

    /**
     * Get the definitions for any query parameters.
     * 
     * @return
     */
    public Collection<QueryParameterDefinition> getQueryParameterDefs();

    /**
     * Get the query string.
     * 
     * @return
     */
    public String getQuery();

    /**
     * Return the mechanism that this query definition uses to map namespace
     * prefixes to URIs. A query may use a predefined set of prefixes for known
     * URIs. I would be unwise to rely on the defaults.
     * 
     * @return
     */
    public NamespacePrefixResolver getNamespacePrefixResolver();

    /**
     * Get a map to look up definitions by Qname
     * 
     * @return
     */
    public Map<QName, QueryParameterDefinition> getQueryParameterMap();
}
