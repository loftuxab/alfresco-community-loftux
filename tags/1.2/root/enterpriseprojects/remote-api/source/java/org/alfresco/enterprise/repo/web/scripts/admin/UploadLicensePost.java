/*
 * Copyright 2013-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.admin;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.descriptor.DescriptorService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;

/**
 * Controller for License Upload Webscript
 * 
 * @author jallison
 * @since 4.2
 */
public class UploadLicensePost extends DeclarativeWebScript
{
    private DescriptorService descriptorService;
    
    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        model.put("success", false);
        model.put("error", "");
        
        // Try to load the user details from the upload
        FormData form = (FormData)req.parseContent();
        if (form == null || !form.getIsMultiPart())
        {
            model.put("success", false);
            model.put("error", "badform");
        }
        else
        {
            boolean processed = false;
            for (FormData.FormField field : form.getFields())
            {
                if (field.getIsFile())
                {
                    processed = true;
                    String result = descriptorService.loadLicense(field.getInputStream());
                    if(result.equals("success"))
                    {
                        model.put("success", true);
                    }
                    else
                    {
                        model.put("success", false);
                        model.put("error", result);
                    }
                    break;
                }
            }
            
            if (!processed)
            {
                model.put("success", false);
                model.put("error", "nofile");
            }
        }
        
        return model;
    }
}
