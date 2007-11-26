package org.alfresco.web.scripts;

import java.util.Map;


public interface ScriptProcessor
{

    /**
     * Find a script at the specified path (within registered Web Script stores)
     * 
     * @param path   script path
     * @return  script location (or null, if not found)
     */
    public ScriptContent findScript(String path);

    /**
     * Execute script
     * 
     * @param path  script path
     * @param model  model
     * @return  script result
     * @throws ScriptException
     */
    public Object executeScript(String path, Map<String, Object> model);

    /**
     * Execute script
     *  
     * @param location  script location
     * @param model  model
     * @return  script result
     */
    public Object executeScript(ScriptContent location, Map<String, Object> model);

    /**
     * Reset script cache
     */
    public void reset();

}
