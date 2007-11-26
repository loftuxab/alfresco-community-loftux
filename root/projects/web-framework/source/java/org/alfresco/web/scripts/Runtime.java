package org.alfresco.web.scripts;

import java.util.Map;

public interface Runtime
{
    public String getName();
    
    public Map<String, Object> getScriptParameters();
    
    public Map<String, Object> getTemplateParameters();
}
