package org.alfresco.service.cmr.search;

/**
 * Post-Processors the results of a Stats query
 * ie. for internationalization / transformation.
 *
 * @author Gethin James
 * @since 5.0
 */
public interface StatsProcessor
{
    public StatsResultSet process(StatsResultSet input);
}
