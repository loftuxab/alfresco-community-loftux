
package org.alfresco.module.vti.metadata.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PavelYur
 *
 */
public class MeetingsInformation
{
    
    private boolean allowCreate = true;
    
    private List<Integer> templateLanguages;
    
    private List<MwsTemplate> templates;
    
    private MwsStatus status;
    
    public MeetingsInformation()
    {   
    }
    
    public void setAllowCreate(boolean allowCreate)
    {
        this.allowCreate = allowCreate;
    }
    
    public boolean isAllowCreate()
    {
        return allowCreate;
    }
    
    public List<Integer> getTemplateLanguages()
    {
        if (templateLanguages == null)
        {
            templateLanguages = new ArrayList<Integer>();
        }
        
        return templateLanguages;
    }
    
    public List<MwsTemplate> getTemplates()
    {
        if (templates == null)
        {
            templates = new ArrayList<MwsTemplate>();
        }
        return templates;
    }
    
    public void setStatus(MwsStatus status)
    {
        this.status = status;
    }
    
    public MwsStatus getStatus()
    {
        return status;
    }
}