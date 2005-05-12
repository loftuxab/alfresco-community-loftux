package org.alfresco.util;

import java.io.File;

import junit.framework.TestCase;

/**
 * Base class for all JUnit tests
 * 
 * @author gavinc
 */
public abstract class BaseTest extends TestCase
{
   private String resourcesDir;
   
   public BaseTest()
   {
      this.resourcesDir = System.getProperty("user.dir") + File.separator + "source" + 
                          File.separator + "test-resources" + File.separator;
   }
   
   public String getResourcesDir()
   {
      return this.resourcesDir;
   }
}
