<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/rules/config/rule-config.lib.js">

function main()
{
   var c = new XML(config.script);
   processScriptConfig(c);

   // Load rule config definitions, or in this case "ActionDefinition:s"
   var actionDefinitions = loadRuleConfigDefinitions(c);
   model.ruleConfigDefinitions = jsonUtils.toJSONString(actionDefinitions);

   // Load constraints for rule types
   var actionConstraints = loadRuleConstraints(c);
   model.constraints = jsonUtils.toJSONString(actionConstraints);
   
}

main();