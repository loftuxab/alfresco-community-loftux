/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.repo.component.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import javax.faces.context.FacesContext;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.bean.repository.Repository;

import freemarker.cache.TemplateLoader;

/**
 * Custom FreeMarker template loader to locate templates stored either from the ClassPath
 * or in a Alfresco Repository.
 * <p>
 * The template name should be supplied either as a NodeRef String or a ClassPath path String.
 * 
 * @author Kevin Roast
 */
public class ClassPathRepoTemplateLoader implements TemplateLoader
{
   /**
    * Return an object wrapping a source for a template
    */
   public Object findTemplateSource(String name)
      throws IOException
   {
      if (name.startsWith(Repository.getStoreRef().toString()))
      {
         NodeRef ref = new NodeRef(name);
         if (getServiceRegistry().getNodeService().exists(ref) == true)
         {
            return new RepoTemplateSource(ref);
         }
         else
         {
            return null;
         }
      }
      else
      {
         URL url = this.getClass().getClassLoader().getResource(name);
         return url == null ? null : new ClassPathTemplateSource(url);
      }
   }
   
   public long getLastModified(Object templateSource)
   {
      return ((BaseTemplateSource)templateSource).lastModified();
   }
   
   public Reader getReader(Object templateSource, String encoding) throws IOException
   {
      return ((BaseTemplateSource)templateSource).getReader();
   }
   
   public void closeTemplateSource(Object templateSource) throws IOException
   {
      ((BaseTemplateSource)templateSource).close();
   }
   
   ServiceRegistry getServiceRegistry()
   {
      return Repository.getServiceRegistry(FacesContext.getCurrentInstance());
   }
   
   
   /**
    * Class used as a base for custom Template Source objects
    */
   abstract class BaseTemplateSource
   {
      public abstract Reader getReader() throws IOException;
      
      public abstract void close() throws IOException;
      
      public abstract long lastModified();
   }
   
   
   /**
    * Class providing a ClassPath based Template Source
    */
   class ClassPathTemplateSource extends BaseTemplateSource
   {
      private final URL url;
      private URLConnection conn;
      private InputStream inputStream;

      ClassPathTemplateSource(URL url) throws IOException
      {
         this.url = url;
         this.conn = url.openConnection();
      }

      public boolean equals(Object o)
      {
         if (o instanceof ClassPathTemplateSource)
         {
            return url.equals(((ClassPathTemplateSource)o).url);
         }
         else
         {
            return false;
         }
      }

      public int hashCode()
      {
         return url.hashCode();
      }

      public String toString()
      {
         return url.toString();
      }
    
      public long lastModified()
      {
         return conn.getLastModified();
      }
       
      public Reader getReader() throws IOException
      {
         inputStream = conn.getInputStream();
         return new InputStreamReader(inputStream);
      }

      public void close() throws IOException
      {
         try
         {
            if (inputStream != null)
            {
               inputStream.close();
            }
         }
         finally
         {
            inputStream = null;
            conn = null;
         }
      }
   }
   
   /**
    * Class providing a Repository based Template Source
    */
   class RepoTemplateSource extends BaseTemplateSource
   {
      private final NodeRef nodeRef;
      private InputStream inputStream;
      private ContentReader conn;

      RepoTemplateSource(NodeRef ref) throws IOException
      {
         this.nodeRef = ref;
         this.conn = getServiceRegistry().getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
      }

      public boolean equals(Object o)
      {
         if (o instanceof RepoTemplateSource)
         {
            return nodeRef.equals(((RepoTemplateSource)o).nodeRef);
         }
         else
         {
            return false;
         }
      }

      public int hashCode()
      {
         return nodeRef.hashCode();
      }

      public String toString()
      {
         return nodeRef.toString();
      }
    
      public long lastModified()
      {
         return conn.getLastModified();
      }
       
      public Reader getReader() throws IOException
      {
         inputStream = conn.getContentInputStream();
         return new InputStreamReader(inputStream);
      }

      public void close() throws IOException
      {
         try
         {
            if (inputStream != null)
            {
               inputStream.close();
            }
         }
         finally
         {
            inputStream = null;
            conn = null;
         }
      }
   }
}
