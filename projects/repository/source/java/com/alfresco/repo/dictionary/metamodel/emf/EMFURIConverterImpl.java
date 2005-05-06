package org.alfresco.repo.dictionary.metamodel.emf;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;


/**
 * Custom URI Converter to support Classpath resolution of Resources
 * 
 * @author David Caruana
 */
public class EMFURIConverterImpl extends URIConverterImpl
{

    public InputStream createInputStream(URI uri) throws IOException
    {
      URI converted = normalize(uri);
      if (converted.isFile())
      {
        String filePath = converted.toFileString();
        return createFileInputStream(filePath);
      }
      else if ("platform".equals(converted.scheme()) && converted.segmentCount() > 1 && "resource".equals(converted.segment(0)))
      {
        StringBuffer platformResourcePath = new StringBuffer();
        for (int i = 1, size = converted.segmentCount(); i < size; ++i)
        {
          platformResourcePath.append('/');
          platformResourcePath.append(URI.decode(converted.segment(i)));
        }
        return createPlatformResourceInputStream(platformResourcePath.toString());
      }
      else if ("classpath".equals(converted.scheme()))
      {
          String name = converted.toString();
          name = name.substring(name.indexOf(":") +1);
          InputStream inputStream = EMFURIConverterImpl.class.getResourceAsStream(name);
          if (inputStream == null)
          {
              throw new IOException("Failed to open " + uri);
          }
          return inputStream;
      }
      else
      {
        return createURLInputStream(converted);
      }
    }
    
}
