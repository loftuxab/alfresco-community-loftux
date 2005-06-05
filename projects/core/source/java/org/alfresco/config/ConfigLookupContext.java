package org.alfresco.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold the context for a config lookup. 
 * 
 * @author gavinc
 */
public class ConfigLookupContext
{
   private boolean includeGlobalSection = true;
   private List<String> areas = new ArrayList<String>();
   private ConfigLookupAlgorithm algorithm;
   
   /**
    * Default constructor
    */
   public ConfigLookupContext()
   {
      this.algorithm = new DefaultLookupAlgorithm();
   }

   /**
    * @return Returns the lookup algorithm, uses the default implementation if a 
    *         custom algorithm is not supplied
    */
   public ConfigLookupAlgorithm getAlgorithm()
   {
      return this.algorithm;
   }

   /**
    * @param algorithm Sets the lookup algorithm to use
    */
   public void setAlgorithm(ConfigLookupAlgorithm algorithm)
   {
      this.algorithm = algorithm;
   }

   /**
    * @return Returns the list of areas to search within
    */
   public List<String> getAreas()
   {
      return this.areas;
   }

   /**
    * @param areas Sets the lists of areas to search within
    */
   public void setAreas(List<String> areas)
   {
      this.areas = areas;
   }
   
   /**
    * @param area Adds the area to the list of areas to be searched
    */
   public void addArea(String area)
   {
      this.areas.add(area);
   }

   /**
    * @return Determines whether the global section should be included in the 
    *         results, true by default
    */
   public boolean includeGlobalSection()
   {
      return this.includeGlobalSection;
   }

   /**
    * @param includeGlobalSection Sets whether the global section will be 
    *        included in the results
    */
   public void setIncludeGlobalSection(boolean includeGlobalSection)
   {
      this.includeGlobalSection = includeGlobalSection;
   }
}
