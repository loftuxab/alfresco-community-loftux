// Overrides Alfresco.RuleEdit's getRuleTemplate method and makes it cloud specific
// by making all the rules to be execuded asynchronously.
Alfresco.RuleEdit.prototype.getRuleTemplate = function()
{
   var ruleTemplate = this.options.ruleTemplate;
   ruleTemplate.executeAsynchronously = true;
   return ruleTemplate;
};