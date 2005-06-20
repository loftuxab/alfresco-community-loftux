package org.alfresco.repo.importer;

import java.io.InputStream;
import java.util.Properties;


/**
 * Importer Service.  Entry point for importing xml data sources into the Repository.
 * 
 * @author David Caruana
 *
 */
public interface ImporterService
{

    /**
     * Import Nodes into the Repository Location
     * 
     * @param inputStream  input stream containing xml to parse
     * @param location  the location to import under
     * @param configuration  property values used for binding property place holders in import stream
     * @param progress  progress monitor (optional)
     */
    public void importNodes(InputStream inputStream, Location location, Properties configuration, Progress progress);
    

}
