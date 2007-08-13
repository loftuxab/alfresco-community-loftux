/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.module.knowledgeBase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.alfresco.repo.importer.ImporterComponent;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * @author Roy Wetherall
 */
public class KBUpdateExampleTest extends BaseSpringTest
{
    public void testUpdateTemplatesAndScripts()
    {                       
        //importFile("alfresco/module/ask/script/script-import.xml", "/app:company_home/app:dictionary/app:scripts");
        //importFile("alfresco/module/ask/template/template-import.xml", "/app:company_home/app:dictionary/app:content_templates");
        //importFile("alfresco/module/ask/bootstrap/kb_datadictionary.xml", "/app:company_home/app:dictionary/app:content_templates");
        setComplete();
        endTransaction();
    }
    
    private void importFile(String file, String destination)
    {        
        ImporterComponent importer = (ImporterComponent)this.applicationContext.getBean("importerComponent");
        
        InputStream viewStream = getClass().getClassLoader().getResourceAsStream(file);
        InputStreamReader inputReader = new InputStreamReader(viewStream);
        BufferedReader reader = new BufferedReader(inputReader);

        Location location = new Location(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"));
        location.setPath(destination);
        
        importer.importView(reader, location, new TempBinding(), null);
    }
    
    private static class TempBinding implements ImporterBinding
    {   
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#getValue(java.lang.String)
         */
        public String getValue(String key)
        {
            return null;
        }

        /*
         *  (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#getUUIDBinding()
         */
        public UUID_BINDING getUUIDBinding()
        {
            // always use create new strategy for bootstrap import
            return UUID_BINDING.UPDATE_EXISTING;
        }

        /*
         *  (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#searchWithinTransaction()
         */
        public boolean allowReferenceWithinTransaction()
        {
            return true;
        }

        /*
         *  (non-Javadoc)
         * @see org.alfresco.service.cmr.view.ImporterBinding#getExcludedClasses()
         */
        public QName[] getExcludedClasses()
        {
            // Note: Do not exclude any classes, we want to import all
            return new QName[] {};
        }
    }

}
