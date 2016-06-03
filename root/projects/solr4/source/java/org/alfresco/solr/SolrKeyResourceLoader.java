package org.alfresco.solr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.encryption.KeyResourceLoader;
import org.apache.solr.core.SolrResourceLoader;

/**
 * Loads encryption key resources from a Solr core installation using a SolrResourceLoader.
 * 
 * @since 4.0
 */
public class SolrKeyResourceLoader implements KeyResourceLoader
{
	private SolrResourceLoader loader;

	public SolrKeyResourceLoader(SolrResourceLoader loader)
	{
		this.loader = loader;
	}

	@Override
	public InputStream getKeyStore(String location)
			throws FileNotFoundException
	{
		try
        {
            return loader.openResource(location);
        }
        catch (IOException e)
        {
            // TODO: SOLR API changes mean that IOException must be handled.
            // This may need revisiting.
            throw new FileNotFoundException("Caused by " + e.getMessage());
        }
	}

	@Override
	public Properties loadKeyMetaData(String location) throws IOException
	{
		Properties p = new Properties();
		InputStream stream = loader.openResource(location);
		p.load(stream);
		stream.close();
		return p;
	}
}
