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
package org.alfresco.util;

import java.net.URL;

/**
 * Class containing debugging utility methods
 * 
 * @author gavinc
 */
public class Debug
{
   /**
    * Returns the location of the file that will be loaded for the given class name 
    * 
    * @param className The class to load
    * @return The location of the file that will be loaded
    * @throws ClassNotFoundException
    */
   public static String whichClass(String className) throws ClassNotFoundException
   {
      String path = className;
      
      // prepare the resource path
      if (path.startsWith("/") == false)
      {
         path = "/" + path;
      }
      path = path.replace('.', '/');
      path = path + ".class";
      
      // get the location
      URL url = Debug.class.getResource(path);
      if (url == null)
      {
         throw new ClassNotFoundException(className);
      }
      
      // format the result
      String location = url.toExternalForm();
      if (location.startsWith("jar"))
      {
         location = location.substring(10, location.lastIndexOf("!"));
      }
      else if (location.startsWith("file:"))
      {
         location = location.substring(6);
      }
      
      return location;
   }
   
   /**
    * Returns the class loader that will load the given class name
    * 
    * @param className The class to load
    * @return The class loader the class will be loaded in
    * @throws ClassNotFoundException
    */
   public static String whichClassLoader(String className) throws ClassNotFoundException
   {
      String result = "Could not determine class loader for " + className;
      
      Class clazz = Class.forName(className);
      ClassLoader loader = clazz.getClassLoader();
      
      if (loader != null)
      {
         result = clazz.getClassLoader().toString();
      }
      
      return result;
   }
   
   /**
    * Returns the class loader hierarchy that will load the given class name
    * 
    * @param className The class to load
    * @return The hierarchy of class loaders used to load the class
    * @throws ClassNotFoundException
    */
   public static String whichClassLoaderHierarchy(String className) throws ClassNotFoundException
   {
      StringBuffer buffer = new StringBuffer();
      Class clazz = Class.forName(className);
      ClassLoader loader = clazz.getClassLoader();
      if (loader != null)
      {
         buffer.append(loader.toString());
         
         ClassLoader parent = loader.getParent();
         while (parent != null)
         {
            buffer.append("\n-> ").append(parent.toString());
            parent = parent.getParent();
         }
      }
      else
      {
         buffer.append("Could not determine class loader for " + className);
      }
      
      return buffer.toString();
   }
}
