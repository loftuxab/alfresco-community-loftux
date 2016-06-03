package org.alfresco.solr;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.solr.core.DirectoryFactory;

/**
 * @author Andy
 *
 */
public class SimpleFSDirectoryFactory extends DirectoryFactory
{

    /* (non-Javadoc)
     * @see org.apache.solr.core.DirectoryFactory#open(java.lang.String)
     */
    @Override
    public Directory open(String path) throws IOException
    {
        return new SimpleFSDirectory(new File(path));
    }

}
