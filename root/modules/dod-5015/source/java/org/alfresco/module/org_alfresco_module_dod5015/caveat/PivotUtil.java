package org.alfresco.module.org_alfresco_module_dod5015.caveat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* package scope */ class PivotUtil
{
    static Map<String, List<String>> getPivot(Map<String, List<String>> source)
    {
    
        Map<String, List<String>> pivot = new HashMap<String, List<String>>();
    
        for(String authority : source.keySet())
        {
            List<String>values = source.get(authority);
            for(String value : values)
            {
                if(pivot.containsKey(value))
                {
                    // already exists
                    List<String> list = pivot.get(value);
                    list.add(authority);
                }
                else
                {
                    // New value
                    List<String> list = new ArrayList<String>();
                    list.add(authority);
                    pivot.put(value, list);
                }
            }
        }
        return pivot;
    }
}
