/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.config.source.FileConfigSource;
import org.springframework.extensions.config.xml.XMLConfigService;

import junit.framework.TestCase;

/**
 * Base class for all JUnit tests
 * 
 * @author gavinc, Neil McErlean
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

    protected void assertFileIsValid(String fullFileName)
    {
        File f = new File(fullFileName);
        assertTrue("Required file missing: " + fullFileName, f.exists());
        assertTrue("Required file not readable: " + fullFileName, f.canRead());
    }
    
    protected XMLConfigService initXMLConfigService(String xmlConfigFile)
    {
        String fullFileName = getResourcesDir() + xmlConfigFile;
        assertFileIsValid(fullFileName);
    
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(
                fullFileName));
        svc.initConfig();
        return svc;
    }
    
    protected XMLConfigService initXMLConfigService(String xmlConfigFile, String overridingXmlConfigFile)
    {
        List<String> files = new ArrayList<String>(2);
        files.add(xmlConfigFile);
        files.add(overridingXmlConfigFile);
        return initXMLConfigService(files);
    }

    protected XMLConfigService initXMLConfigService(List<String> xmlConfigFilenames)
    {
        List<String> configFiles = new ArrayList<String>();
        for (String filename : xmlConfigFilenames)
        {
            String path = getResourcesDir() + filename;
            assertFileIsValid(path);
            configFiles.add(path);
        }
        XMLConfigService svc = new XMLConfigService(new FileConfigSource(configFiles));
        svc.initConfig();
        return svc;
    }
}
