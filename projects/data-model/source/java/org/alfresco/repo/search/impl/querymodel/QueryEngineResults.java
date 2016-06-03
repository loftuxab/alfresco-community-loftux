package org.alfresco.repo.search.impl.querymodel;

import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.search.ResultSet;

/**
 * Encapsulate Query engine results List<Pair<Set<String>, ResultSet>>
 * 
 * @author andyh
 */
public class QueryEngineResults
{
    private Map<Set<String>, ResultSet> results;

    public QueryEngineResults(Map<Set<String>, ResultSet> results)
    {
        this.results = results;
    }
    
    public Map<Set<String>, ResultSet> getResults()
    {
        return results;
    }
}
