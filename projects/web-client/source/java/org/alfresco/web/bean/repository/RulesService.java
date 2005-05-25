package org.alfresco.web.bean.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary facade for a Rules Service the repository will provide
 * 
 * @author gavinc
 */
public class RulesService
{
   /**
    * @return Returns a list of rule types
    */
   public List<RuleType> getRuleTypes()
   {
      ArrayList<RuleType> ruleTypes = new ArrayList<RuleType>(2);
      ruleTypes.add(new RuleType("inbound", "Inbound"));
      ruleTypes.add(new RuleType("outbound", "Outbound"));
      return ruleTypes;
   }
   
   /**
    * @return Returns a list of rule actions
    */
   public List<RuleAction> getRuleActions()
   {
      ArrayList<RuleAction> ruleActions = new ArrayList<RuleAction>(8);
      ruleActions.add(new RuleAction("simple-workflow", "Add simple workflow to content",
                        "This will add a simple workflow to the matched content. This " +
                        "will allow the content to be moved to a different location for " +
                        "its next step in a workflow.  You can also give a location for " +
                        "it to be moved to if you want a reject step"));
      ruleActions.add(new RuleAction("link-category", "Link content to category",
                        "This will apply a category to the matched content."));
      ruleActions.add(new RuleAction("add-features", "Add features to content",
                        "This will add a feature to the matched content."));
      ruleActions.add(new RuleAction("copy", "Create a copy of content in a given format at " +
                        "a specific location",
                        "This will copy the matched content to another location with a " +
                        "specific format."));
      ruleActions.add(new RuleAction("move", "Move content to a specific location",
                        "This will move the matched content to another location."));
      ruleActions.add(new RuleAction("email", "Send an email to specified users",
                        "This will send an email to a list of users when the content matches."));
      ruleActions.add(new RuleAction("check-in", "Check in content",
                        "This will check in the matched content."));
      ruleActions.add(new RuleAction("check-out", "Check out content",
                        "This will check out the matched content."));
      
      return ruleActions;
   }
   
   /**
    * @return Returns a list of rule conditions
    */
   public List<RuleCondition> getRuleConditions()
   {
      ArrayList<RuleCondition> ruleConditions = new ArrayList<RuleCondition>(3);
      ruleConditions.add(new RuleCondition("no-condition", "All Content", 
                           "This condition will match any item of content added to this location. " +
                           "Use this when you wish to apply an action to everything " +
                           "added to this location."));
      ruleConditions.add(new RuleCondition("in-category", "Content in a specific category", 
                           "The rule is applied to all content that has a specific category"));
      ruleConditions.add(new RuleCondition("contains-text", "Content which contains " +
                           "specific text in its name", 
                           "The rule is applied to all content that has specific text in " +
                           "its name"));
      return ruleConditions;
   }
   
   /**
    * Represents a type of rule i.e. inbound or outbound
    * 
    * @author gavinc
    */
   public class RuleType
   {
      private String id;
      private String name;
      
      /**
       * @param id Id of the rule type
       * @param name Name of the rule type
       */
      public RuleType(String id, String name)
      {
         this.id = id;
         this.name = name;
      }

      /**
       * @return Returns the id of the rule type
       */
      public String getId()
      {
         return this.id;
      }
      
      /**
       * @return Returns the name of the rule type
       */
      public String getName()
      {
         return this.name;
      }
   }
   
   /**
    * Represents an action for a rule
    * 
    * @author gavinc
    */
   public class RuleAction
   {
      private String id;
      private String name;
      private String description;
      
      /**
       * @param id Id of the rule action
       * @param name Name of the rule action
       * @param description Description of the rule action
       */
      public RuleAction(String id, String name, String description)
      {
         this.id = id;
         this.name = name;
         this.description = description;
      }

      /**
       * @return Returns the id of the rule action
       */
      public String getId()
      {
         return this.id;
      }
      
      /**
       * @return Returns the name of the rule action
       */
      public String getName()
      {
         return this.name;
      }
      
      /**
       * @return Returns the description of the rule action
       */
      public String getDescription()
      {
         return this.description;
      }
   }
   
   
   /**
    * Represents a condition for a rule
    * 
    * @author gavinc
    */
   public class RuleCondition
   {
      private String id;
      private String name;
      private String description;
      
      /**
       * @param id Id of the rule condition
       * @param name Name of the rule condition
       * @param description Description of the rule action
       */
      public RuleCondition(String id, String name, String description)
      {
         this.id = id;
         this.name = name;
         this.description = description;
      }

      /**
       * @return Returns the id of the rule condition
       */
      public String getId()
      {
         return this.id;
      }
      
      /**
       * @return Returns the name of the rule condition
       */
      public String getName()
      {
         return this.name;
      }
      
      /**
       * @return Returns the description of the rule condition
       */
      public String getDescription()
      {
         return this.description;
      }
   }
}
