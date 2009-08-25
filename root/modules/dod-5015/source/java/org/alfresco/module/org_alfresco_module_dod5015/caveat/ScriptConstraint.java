package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.alfresco.service.cmr.security.AuthorityService;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScriptConstraint implements Serializable
{
   /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private RMConstraintInfo info;
    
    private RMCaveatConfigService rmCaveatconfigService;
    
    private AuthorityService authorityService;
    
    ScriptConstraint(RMConstraintInfo info, RMCaveatConfigService rmCaveatconfigService, AuthorityService authorityService)
    {
        this.info = info;
        this.rmCaveatconfigService = rmCaveatconfigService; 
        this.authorityService = authorityService;
    }
    
    public void setTitle(String title)
    {
        info.setTitle(title);
    }
    public String getTitle()
    {
        return info.getTitle();
    }
    public void setName(String name)
    {
        info.setName(name);
    }
    
    public String getName()
    {
        String xxx = info.getName().replace(":", "_");
        return xxx;
    }
    
    public boolean isCaseSensitive()
    {
        return info.isCaseSensitive();
    }
    
    public String[] getAllowedValues()
    {
        return info.getAllowedValues();
    }
    
    public ScriptConstraintAuthority[] getAuthorities()
    {
         Map<String, List<String>> values = rmCaveatconfigService.getListDetails(info.getName());
                  
         if (values == null)
         {
             return null;
         }
         
         // Here with some data to return
         Set<String> authorities = values.keySet();
         
         ArrayList<ScriptConstraintAuthority> constraints = new ArrayList<ScriptConstraintAuthority>(values.size());
         for(String authority : authorities)
         {
              ScriptConstraintAuthority constraint = new ScriptConstraintAuthority();
              constraint.setAuthorityName(authority);
              constraint.setValues(values.get(authority));
              constraints.add(constraint);             
         }
         
         ScriptConstraintAuthority[] retVal = constraints.toArray(new ScriptConstraintAuthority[constraints.size()]);
         
         return retVal;   
    }
    
    /**
     * updateTitle
     */
    public void updateTitle(String newTitle)
    {
        rmCaveatconfigService.updateRMConstraintTitle(info.getName(), newTitle)  ;
    }
    
    /**
     * updateAllowedValues
     */
    public void updateAllowedValues(String[] allowedValues)
    {
        rmCaveatconfigService.updateRMConstraintAllowedValues(info.getName(), allowedValues);
    }
    
    /**
     * Update a value
     * @param values
     * @param authorities
     */
    public void updateValues(JSONArray bodge) throws Exception
    {
        for(int i = 0; i < bodge.length(); i++)
        {
            
            JSONObject obj = bodge.getJSONObject(i);
            String value = obj.getString("value");
            JSONArray authorities = obj.getJSONArray("authorities");
            List<String> aList = new ArrayList<String>();
            for(int j = 0; j < authorities.length();j++)
            {
                aList.add(authorities.getString(j));
            }
            rmCaveatconfigService.updateRMConstraintListValue(info.getName(), value, aList);    
        }    
    }
        
    /**
     * Update a value
     * @param values
     * @param authorities
     */
    public void updateValues(String value, String[] authorities)
    {
        List<String> list = Arrays.asList(authorities);
        rmCaveatconfigService.updateRMConstraintListValue(info.getName(), value, list); 
    }
    
    /**
     * Cascade delete an authority
     * @param authority
     */
    public void deleteAuthority(String authority)
    {
        
    }
    
    /**
     * Cascade delete a value
     * @param value
     */
    public void deleteValue(String value)
    {
        
    }
                                     
    public ScriptConstraintValue[] getValues()
    {
        Map<String, List<String>> details = rmCaveatconfigService.getListDetails(info.getName());
        
        if (details == null)
        {
            return null;
        }
        
        Map<String, List<String>> pivot = PivotUtil.getPivot(details);
        
        // Here with some data to return
        Set<String> values = pivot.keySet();
        
        ArrayList<ScriptConstraintValue> constraints = new ArrayList<ScriptConstraintValue>(pivot.size());
        for(String value : values)
        {
             ScriptConstraintValue constraint = new ScriptConstraintValue();
             constraint.setValueName(value);
             constraint.setValueTitle(value);
             
             List<String>authorities = pivot.get(value);
             List<ScriptAuthority> sauth = new ArrayList<ScriptAuthority>();
             for(String authority : authorities)
             {
                 ScriptAuthority a = new ScriptAuthority();
                 a.setAuthorityName(authority);
                 
                 String displayName = authorityService.getAuthorityDisplayName(authority);
                 if(displayName != null)
                 {
                     a.setAuthorityTitle(displayName);
                 }
                 else
                 {
                     a.setAuthorityTitle(authority);
                 }
                 sauth.add(a);
             }
             constraint.setAuthorities(sauth);       
             constraints.add(constraint);             
        }
        ScriptConstraintValue[] retVal = constraints.toArray(new ScriptConstraintValue[constraints.size()]);
        return retVal;
    }
                                     
}
