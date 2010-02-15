<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/config/rule-config.lib.js">

function main()
{
   var c = new XML(config.script);
   processScriptConfig(c);

   // Load rule config definitions, or in this case "ActionConditionDefinition:s"
   var actionConditionDefinitions = loadRuleConfigDefinitions(c);
   /**
    * Remove the "compare-property-value" action condition definition from the list
    * and put it as a dedicated variable since that condition is a special case
    * and will be dynamically created on the page based on which property that is selected.
    */
   var i = 0;
   for (var il = actionConditionDefinitions.length; i < il; i++)
   {
      if (actionConditionDefinitions[i].name == "compare-property-value")
      {
         model.comparePropertyValueDefinition = jsonUtils.toJSONString(actionConditionDefinitions[i]);
         break;
      }
   }
   if (i < il)
   {
      actionConditionDefinitions.splice(i, 1);
   }
   model.ruleConfigDefinitions = jsonUtils.toJSONString(actionConditionDefinitions);

   // Load constraints for rule types
   var actionConstraints = loadRuleConstraints(c);
   model.constraints = jsonUtils.toJSONString(actionConstraints);


   // Save proeprty-evaluator config as json
   var propertyEvaluatorMap = {},
      propertyEvaluatorNodes = c.elements("property-evaluators"),
      propertyEvaluatorNode = propertyEvaluatorNodes && propertyEvaluatorNodes.length() > 0 ? propertyEvaluatorNodes[0] : null,
      evaluatorNode,
      propertyNode,
      propertyTypes;
   if (propertyEvaluatorNode)
   {
      for each (evaluatorNode in propertyEvaluatorNode.elements("evaluator"))
      {
         propertyTypes = [];
         for each (propertyNode in evaluatorNode.elements("property"))
         {
            propertyTypes.push(propertyNode.@type.toString());
         }
         propertyEvaluatorMap[evaluatorNode.@name.toString()] = propertyTypes;
      }
   }
   model.propertyEvaluatorMap = jsonUtils.toJSONString(propertyEvaluatorMap);

   // Load user preferences for which proeprties to show in menu as default
   var connector = remote.connect("alfresco");
   var result = connector.get("/api/people/" + user.id + "/preferences?pf=org.alfresco.share.rule.properties");
   if (result.status == 200)
   {
      var prefs = eval('(' + result + ')'),
         ruleProperties = {};
      // Get all default properties
      if (c.defaults)
      {
         for each (propertyNode in c.defaults.elements("property"))
         {
            ruleProperties[propertyNode.text()] = "show";
         }
      }

      // Complete with user preferences
      if (prefs && prefs.org && prefs.org.alfresco && prefs.org.alfresco.share && prefs.org.alfresco.share.rule && prefs.org.alfresco.share.rule.properties)
      {
         var userProperties = prefs.org.alfresco.share.rule.properties;
         for each (propertyName in userProperties)
         {
            // Will set value to "show" or "hide"
            ruleProperties[propertyName] = userProperties[propertyName];
         }
      }

      // Get info such as type and display name about the properties to display
      var propertiesParam = [];
      for each (propertyName in ruleProperties)
      {
         if(ruleProperties[propertyName] == "show")
         {
            propertiesParam.push(propertyName);
         }
      }

      result = connector.get("/api/sites"); // todo replace with data dictionary webscript and use the propertiesParam
      if (result.status == 200)
      {
         var properties = [];

         // todo remove test data once webscript exists
         properties.push(
         {
            id: "name",
            type: "d:text",
            displayLabel: "Name"
         });
         properties.push(
         {
            id: "title",
            type: "d:text",
            displayLabel: "Title"
         });
         properties.push(
         {
            id: "size",
            type: "d:long",
            displayLabel: "Size"
         });
         properties.push(
         {
            id: "date-created",
            type: "d:datetime",
            displayLabel: "Date created"
         });

         model.properties = jsonUtils.toJSONString(properties);
      }

   }
}

main();