package org.alfresco.service.cmr.search;

import java.util.Map;

/**
 * Post-Processors the results of a Stats query using a Map of values.
 * Looks up the value by Map.key and replaces it with Map.value
 * 
 * If its not found then returns the existing value.
 *
 * @author Gethin James
 * @since 5.0
 */
public class StatsProcessorUsingMap implements StatsProcessor
{
    Map<String, String> mapping;
    
    public StatsProcessorUsingMap()
    {
        super();
    }
    
    public StatsProcessorUsingMap(Map<String, String> mapping)
    {
        super();
        this.mapping = mapping;
    }

    @Override
    public StatsResultSet process(StatsResultSet input)
    {
        if (input == null || input.getStats() == null){ return null; }
        
        for (StatsResultStat aStat : input.getStats())
        {
            String processed = mapping.get(aStat.getName());
            if (processed != null) { aStat.setName(processed); }
        }
        return input;
    }

    public void setMapping(Map<String, String> mapping)
    {
        this.mapping = mapping;
    }


}
