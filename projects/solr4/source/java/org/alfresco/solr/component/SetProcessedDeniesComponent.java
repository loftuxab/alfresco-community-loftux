package org.alfresco.solr.component;

import java.io.IOException;

import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

/**
 * Sets a boolean flag ("processedDenies") in the JSON response indicating that
 * the results (should) have been processed with respect to anyDenyDenies
 * (i.e. {@link org.alfresco.solr.query.AbstractQParser} has added the correct clause to the search query).
 * 
 * @author Matt Ward
 */
public class SetProcessedDeniesComponent extends SearchComponent
{
    public static final String PROCESSED_DENIES = "processedDenies";

    @Override
    public void prepare(ResponseBuilder rb) throws IOException
    {
        // No preparation required
    }

    @Override
    public void process(ResponseBuilder rb) throws IOException
    {
        Boolean processedDenies = (Boolean) rb.req.getContext().get(PROCESSED_DENIES);
        processedDenies = (processedDenies == null) ? false : processedDenies;
        rb.rsp.add(PROCESSED_DENIES, processedDenies);
    }

    @Override
    public String getDescription()
    {
        return "Adds the processedDenies boolean flag to the search results.";
    }

    @Override
    public String getSource()
    {
        return "http://www.alfresco.com";
    }

    @Override
    public String getVersion()
    {
        return "1.0";
    }
    
    @Override
    public void finishStage(ResponseBuilder rb) {
      if (rb.stage != ResponseBuilder.STAGE_GET_FIELDS) {
        return;
      }
      
      Boolean processedDenies = (Boolean) rb.req.getContext().get(PROCESSED_DENIES);
      processedDenies = (processedDenies == null) ? false : processedDenies;
      rb.rsp.add(PROCESSED_DENIES, processedDenies);
    }
}
