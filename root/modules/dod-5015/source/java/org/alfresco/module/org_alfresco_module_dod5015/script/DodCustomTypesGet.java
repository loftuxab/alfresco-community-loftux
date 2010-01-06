/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class provides the implementation for the dodcustomtypes.get webscript.
 * 
 * @author Neil McErlean
 */
public class DodCustomTypesGet extends DeclarativeWebScript
{
    // TODO Investigate a way of not hard-coding the 4 custom types here.
    private final static List<QName> customTypeAspects = Arrays.asList(new QName[]{DOD5015Model.ASPECT_SCANNED_RECORD,
            DOD5015Model.ASPECT_PDF_RECORD, DOD5015Model.ASPECT_DIGITAL_PHOTOGRAPH_RECORD, DOD5015Model.ASPECT_WEB_RECORD});

    private DictionaryService dictionaryService;
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
  
        List<AspectDefinition> customTypeAspectDefinitions = new ArrayList<AspectDefinition>(4);
        for (QName aspectQName : customTypeAspects)
        {
            AspectDefinition nextAspectDef = dictionaryService.getAspect(aspectQName);
            customTypeAspectDefinitions.add(nextAspectDef);
        }
    	model.put("dodCustomTypes", customTypeAspectDefinitions);
    	
        return model;
    }
}