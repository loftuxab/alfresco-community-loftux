package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;

/**
 * Script projection of RM Caveat Config Service
 *
 * @author Mark Rogers
 */
public class ScriptRMCaveatConfigService extends BaseScopableProcessorExtension
{
    private RMCaveatConfigService caveatConfigService;

    public void setCaveatConfigService(RMCaveatConfigService rmCaveatConfigService)
    {
        this.caveatConfigService = rmCaveatConfigService;
    }

    public RMCaveatConfigService getRmCaveatConfigService()
    {
        return caveatConfigService;
    }
    
    public String[] getAllConstraintNames()
    {
        Set<String> values = caveatConfigService.getRMConstraintNames();
        return values.toArray(new String[values.size()]);
    }
    
    /**
     * Get the details of the constraint list
     * @param listName
     * @return the constraint list or null if the list does not exist
     */
    public ScriptConstraint[] getConstraintDetails(String listName)
    {
        
        //TODO Temporary conversion
        String xxx = listName.replace("_", ":");
        
        Map<String, List<String>> values = caveatConfigService.getListDetails(xxx);
        
        if (values == null)
        {
            return null;
        }
        
        // Here with some data to return
        Set<String> authorities = values.keySet();
        
        ArrayList<ScriptConstraint> constraints = new ArrayList<ScriptConstraint>(values.size());
        for(String authority : authorities)
        {
             ScriptConstraint constraint = new ScriptConstraint();
             constraint.setAuthorityName(authority);
             constraint.setValues(values.get(authority));
             constraints.add(constraint);             
        }
        
         return constraints.toArray(new ScriptConstraint[constraints.size()]);
    }

}
