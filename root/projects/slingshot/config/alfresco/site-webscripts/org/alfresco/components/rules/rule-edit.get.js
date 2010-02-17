function main()
{
   var ruleNodeRef = page.url.args.nodeRef,
      ruleId = page.url.args.ruleId,
      connector = remote.connect("alfresco"),
      rule = null;

   // Load rule to edit of given in url
   if (ruleNodeRef && ruleId)
   {
      result = connector.get("/api/node/" + ruleNodeRef.replace("://", "/") + "/ruleset/rules/" + ruleId);
      if (result.status == 200)
      {
         var data = eval('(' + result + ')');
         rule = jsonUtils.toJSONString(data);
      }
   }
   model.rule = rule;

   // Load list of values needed
   // todo: replace with real webscript
   /*
   result = connector.get("/api/sites/" + siteId);
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
   }
   */
   var scripts =
   [
      { value: "", label: msg.get("label.none") },
      { value: "/some/script.js", label: "/some/script.js" },
      { value: "/some/other/script.js", label: "/some/other/script.java" },
   ];
   model.scripts = scripts;

   // Repository Library root node
   var rootNode = "alfresco://company/home",
      repoConfig = config.scoped["RepositoryLibrary"]["root-node"];
   if (repoConfig !== null)
   {
      rootNode = repoConfig.value;
   }

   model.rootNode = rootNode;
}

main();